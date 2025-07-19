package com.brijesh.ExpenseAnalysis.service;

import com.brijesh.ExpenseAnalysis.dto.MonthlyAnalysisResponseDTO;
import com.brijesh.ExpenseAnalysis.dto.MonthlyAnalysisRequestDTO;
import com.brijesh.ExpenseAnalysis.dto.YearlyAnalysisRequestDTO;
import com.brijesh.ExpenseAnalysis.dto.YearlyAnalysisResponseDTO;
import com.brijesh.ExpenseAnalysis.entity.Expense;
import com.brijesh.ExpenseAnalysis.repository.ExpenseRepository;
import com.brijesh.ExpenseAnalysis.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public Map<String, Object> addExpenseAndReturnAlerts(Expense expense) {
        double totalForMonth = expenseRepository.sumMonthlyTotal(expense.getDate().getMonthValue(), expense.getDate().getYear());
        double categoryTotalForMonth = expenseRepository.sumMonthlyCategoryTotal(expense.getCategory(), expense.getDate().getMonthValue(), expense.getDate().getYear());
        double tagTotalForMonth = expenseRepository.sumMonthlyTagTotal(expense.getTag(), expense.getDate().getMonthValue(), expense.getDate().getYear());

        double monthlyLimit = BudgetLimitUtil.TOTAL_MONTHLY_BUDGET;
        double categoryLimit = switch (expense.getCategory()) {
            case FIXED -> BudgetLimitUtil.TOTAL_FIXED_EXPENSE_BUDGET;
            case VARIABLE -> BudgetLimitUtil.TOTAL_VARIABLE_BUDGET;
            case INVESTMENT -> BudgetLimitUtil.TOTAL_INVESTMENT_BUDGET;
            case INSURANCE -> BudgetLimitUtil.TOTAL_INSURANCE_BUDGET;
            default -> Double.MAX_VALUE;
        };

        double tagLimit = BudgetLimitUtil.TAG_BUDGETS.getOrDefault(expense.getTag(), Double.MAX_VALUE);

        List<String> alerts = new ArrayList<>();

        if ((totalForMonth + expense.getAmount()) > monthlyLimit) {
            alerts.add("⚠️ Total monthly budget exceeded.");
        }
        if ((categoryTotalForMonth + expense.getAmount()) > categoryLimit) {
            alerts.add("⚠️ " + expense.getCategory() + " category budget exceeded.");
        }
        if ((tagTotalForMonth + expense.getAmount()) > tagLimit) {
            alerts.add("⚠️ Budget exceeded for tag: " + expense.getTag());
        }

        Map<String, Object> response = new HashMap<>();
        if (ExpenseValidationMap.isValidTagForCategory(expense.getCategory(), expense.getTag())) {
            expenseRepository.save(expense);
            response.put(StatusMap.STATUS.name(), StatusMap.SUCCESS.name());
            response.put("expense", expense);
            response.put("alerts", alerts);
        } else {
            response.put(StatusMap.STATUS.name(), StatusMap.ERROR.name());
            String tip = "Please add correct tag for " + expense.getCategory();
            response.put("tip", tip);
        }

        return response;
    }

    public List<Expense> getAllExpenses() {
        List<Expense> expenseList = expenseRepository.findAll();
        log.info("Found {} expense", expenseList.size());
        return expenseList;
    }

    public boolean deleteExpenseById(Long id) {
        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if (optionalExpense.isPresent()) {
            expenseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public YearlyAnalysisResponseDTO getYearlyAnalysis(YearlyAnalysisRequestDTO request) {
        int year = request.getYear();
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.now().isBefore(LocalDate.of(year, 12, 31)) ?
                LocalDate.now() : LocalDate.of(year, 12, 31);

        List<Expense> expenses = expenseRepository.findByDateBetween(startDate, endDate);

        return buildAnalysisResponse(expenses, endDate.getMonthValue(), true);
    }

    public MonthlyAnalysisResponseDTO getMonthlyAnalysis(MonthlyAnalysisRequestDTO request) {
        Month month = Month.valueOf(request.getMonth().toUpperCase());
        int year = request.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(1).equals(startDate) ?
                LocalDate.now() : startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Expense> expenses = expenseRepository.findByDateBetween(startDate, endDate);

        return buildAnalysisResponse(expenses, 1, false);
    }

    private <T> T buildAnalysisResponse(List<Expense> expenses, int monthsCount, boolean isYearly) {
        Map<String, Map<String, Double>> categoryTagMap = new LinkedHashMap<>();
        Map<String, Double> categoryExpenseMap = new LinkedHashMap<>();
        Map<String, Double> categoryBudgetMap = Map.of(
                "FIXED", BudgetLimitUtil.TOTAL_FIXED_EXPENSE_BUDGET,
                "VARIABLE", BudgetLimitUtil.TOTAL_VARIABLE_BUDGET,
                "INVESTMENT", BudgetLimitUtil.TOTAL_INVESTMENT_BUDGET,
                "INSURANCE", BudgetLimitUtil.TOTAL_INSURANCE_BUDGET
        );

        double totalExpense = 0.0;
        Map<String, Double> monthsTotal = new LinkedHashMap<>();

        Map<String, Map<String, Double>> overBudgetTags = new HashMap<>();
        Map<String, Map<String, Double>> overBudgetCategories = new HashMap<>();

        for (ExpenseCategory category : ExpenseCategory.values()) {
            Map<String, Double> tagWiseMap = new LinkedHashMap<>();
            for (Expense expense : expenses) {
                if (expense.getCategory() == category) {
                    String tag = expense.getTag().name();
                    tagWiseMap.put(tag, tagWiseMap.getOrDefault(tag, 0.0) + expense.getAmount());

                    Month expenseMonth = expense.getDate().getMonth();
                    String monthName = expenseMonth.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    monthsTotal.put(monthName, monthsTotal.getOrDefault(monthName, 0.0) + expense.getAmount());
                }
            }
            categoryTagMap.put(category.name(), tagWiseMap);
            categoryExpenseMap.put(category.name(), tagWiseMap.values().stream().mapToDouble(Double::doubleValue).sum());
        }

        for (String category : categoryBudgetMap.keySet()) {
            double budget = categoryBudgetMap.get(category) * monthsCount;
            double actual = categoryExpenseMap.getOrDefault(category, 0.0);
            if (actual > budget) {
                overBudgetCategories.put(category, Map.of("budget", budget, "actual", actual));

                Map<String, Double> tags = categoryTagMap.get(category);
                Map<String, Double> tagExceed = new LinkedHashMap<>();

                for (String tag : tags.keySet()) {
                    double actualTagAmt = tags.get(tag);
                    double tagBudget = BudgetLimitUtil.TAG_BUDGETS.getOrDefault(ExpenseTag.valueOf(tag), Double.MAX_VALUE) * monthsCount;
                    if (actualTagAmt > tagBudget) {
                        tagExceed.put(tag, actualTagAmt - tagBudget);
                    }
                }
                if (!tagExceed.isEmpty()) overBudgetTags.put(category, tagExceed);
            }
        }

        double totalBudget = BudgetLimitUtil.TOTAL_MONTHLY_BUDGET * monthsCount;
        totalExpense = categoryExpenseMap.values().stream().mapToDouble(Double::doubleValue).sum();

        Map<String, Object> summary = Map.of(
                "totalExpense", totalExpense,
                "totalBudget", totalBudget,
                "budgetRemaining", totalBudget - totalExpense
        );

        Map<String, Double> monthsExceeding = monthsTotal.entrySet().stream()
                .filter(e -> e.getValue() > BudgetLimitUtil.TOTAL_MONTHLY_BUDGET)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (isYearly) {
            return (T) YearlyAnalysisResponseDTO.builder()
                    .categoryTagWiseExpenses(categoryTagMap)
                    .categoryBudgetVsExpense(Map.of("Budget", categoryBudgetMap, "Actual", categoryExpenseMap))
                    .overBudgetCategories(overBudgetCategories)
                    .overBudgetTagsInOverBudgetCategories(overBudgetTags)
                    .summary(summary)
                    .monthsExceedingBudget(monthsExceeding)
                    .build();
        } else {
            return (T) MonthlyAnalysisResponseDTO.builder()
                    .categoryTagWiseExpenses(categoryTagMap)
                    .categoryBudgetVsExpense(Map.of("Budget", categoryBudgetMap, "Actual", categoryExpenseMap))
                    .overBudgetCategories(overBudgetCategories)
                    .overBudgetTagsInOverBudgetCategories(overBudgetTags)
                    .summary(summary)
                    .build();
        }
    }
}