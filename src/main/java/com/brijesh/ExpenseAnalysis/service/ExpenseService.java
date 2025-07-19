package com.brijesh.ExpenseAnalysis.service;

import com.brijesh.ExpenseAnalysis.dto.*;
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

        return buildSimplifiedAnalysisResponse(expenses, endDate.getMonthValue(), true);
    }

    public MonthlyAnalysisResponseDTO getMonthlyAnalysis(MonthlyAnalysisRequestDTO request) {
        Month month = Month.valueOf(request.getMonth().toUpperCase());
        int year = request.getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(1).equals(startDate) ?
                LocalDate.now() : startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Expense> expenses = expenseRepository.findByDateBetween(startDate, endDate);

        return buildSimplifiedAnalysisResponse(expenses, 1, false);
    }

    private <T> T buildSimplifiedAnalysisResponse(List<Expense> expenses, int monthsCount, boolean isYearly) {
        Map<String, Double> categoryExpenses = new LinkedHashMap<>();
        List<CategoryBudgetVsActualDTO> budgetVsActualList = new ArrayList<>();
        List<TagOverBudgetDTO> tagOverBudgetList = new ArrayList<>();
        List<MonthBudgetExceedDTO> monthsExceeding = new ArrayList<>();

        Map<String, Double> categoryBudgets = Map.of(
                "FIXED", BudgetLimitUtil.TOTAL_FIXED_EXPENSE_BUDGET * monthsCount,
                "VARIABLE", BudgetLimitUtil.TOTAL_VARIABLE_BUDGET * monthsCount,
                "INVESTMENT", BudgetLimitUtil.TOTAL_INVESTMENT_BUDGET * monthsCount,
                "INSURANCE", BudgetLimitUtil.TOTAL_INSURANCE_BUDGET * monthsCount
        );

        double totalExpense = 0.0;
        double totalBudget = BudgetLimitUtil.TOTAL_MONTHLY_BUDGET * monthsCount;

        Map<String, Map<String, Double>> tagWiseExpenses = new LinkedHashMap<>();
        Map<String, Double> monthlyExpenseMap = new LinkedHashMap<>();

        for (Expense expense : expenses) {
            String category = expense.getCategory().name();
            String tag = expense.getTag().name();
            double amount = expense.getAmount();
            totalExpense += amount;

            categoryExpenses.put(category, categoryExpenses.getOrDefault(category, 0.0) + amount);

            tagWiseExpenses.putIfAbsent(category, new LinkedHashMap<>());
            Map<String, Double> tagMap = tagWiseExpenses.get(category);
            tagMap.put(tag, tagMap.getOrDefault(tag, 0.0) + amount);

            if (isYearly) {
                String monthName = expense.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                monthlyExpenseMap.put(monthName, monthlyExpenseMap.getOrDefault(monthName, 0.0) + amount);
            }
        }

        for (String category : categoryBudgets.keySet()) {
            double budget = categoryBudgets.get(category);
            double actual = categoryExpenses.getOrDefault(category, 0.0);

            budgetVsActualList.add(CategoryBudgetVsActualDTO.builder()
                    .category(category)
                    .budget(budget)
                    .actual(actual)
                    .build());

            if (actual > budget) {
                Map<String, Double> tagMap = tagWiseExpenses.getOrDefault(category, Collections.emptyMap());
                for (Map.Entry<String, Double> entry : tagMap.entrySet()) {
                    double tagBudget = BudgetLimitUtil.TAG_BUDGETS.getOrDefault(ExpenseTag.valueOf(entry.getKey()), Double.MAX_VALUE) * monthsCount;
                    if (entry.getValue() > tagBudget) {
                        tagOverBudgetList.add(TagOverBudgetDTO.builder()
                                .category(category)
                                .tag(entry.getKey())
                                .budget(tagBudget)
                                .actual(entry.getValue())
                                .exceededBy(entry.getValue() - tagBudget)
                                .build());
                    }
                }
            }
        }

        SummaryDTO summary = SummaryDTO.builder()
                .totalExpense(totalExpense)
                .totalBudget(totalBudget)
                .budgetRemaining(totalBudget - totalExpense)
                .build();

        if (isYearly) {
            monthlyExpenseMap.forEach((month, expense) -> {
                if (expense > BudgetLimitUtil.TOTAL_MONTHLY_BUDGET) {
                    monthsExceeding.add(MonthBudgetExceedDTO.builder()
                            .month(month)
                            .expense(expense)
                            .build());
                }
            });

            List<CategoryExpenseDTO> categoryExpenseDTOList = categoryExpenses.entrySet().stream()
                    .map(e -> CategoryExpenseDTO.builder().category(e.getKey()).amount(e.getValue()).build())
                    .collect(Collectors.toList());

            return (T) YearlyAnalysisResponseDTO.builder()
                    .categoryExpenses(categoryExpenseDTOList)
                    .budgetVsActual(budgetVsActualList)
                    .tagOverBudgetDetails(tagOverBudgetList)
                    .summary(summary)
                    .monthsExceedingBudget(monthsExceeding)
                    .build();
        } else {
            List<CategoryExpenseDTO> categoryExpenseDTOList = categoryExpenses.entrySet().stream()
                    .map(e -> CategoryExpenseDTO.builder().category(e.getKey()).amount(e.getValue()).build())
                    .collect(Collectors.toList());

            return (T) MonthlyAnalysisResponseDTO.builder()
                    .categoryExpenses(categoryExpenseDTOList)
                    .budgetVsActual(budgetVsActualList)
                    .tagOverBudgetDetails(tagOverBudgetList)
                    .summary(summary)
                    .build();
        }
    }
}
