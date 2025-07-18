package com.brijesh.ExpenseTracker.service;

import com.brijesh.ExpenseTracker.dto.ExpenseAnalysisDTO;
import com.brijesh.ExpenseTracker.entity.Expense;
import com.brijesh.ExpenseTracker.repository.ExpenseRepository;
import com.brijesh.ExpenseTracker.utils.ExpenseCategory;
import com.brijesh.ExpenseTracker.utils.ExpenseTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetLimitService budgetLimitService;

    public String saveExpenseWithBudgetCheck(Expense expense) {
        Double currentTotal = expenseRepository
                .findByCategoryAndTagAndMonthYear(
                        expense.getCategory(), expense.getTag(),
                        expense.getDate().getMonthValue(), expense.getDate().getYear())
                .stream().mapToDouble(Expense::getAmount).sum();

        Double limit = budgetLimitService.getLimit(expense.getCategory(), expense.getTag());

        if (limit != null && currentTotal + expense.getAmount() > limit) {
            return "Warning: Budget limit exceeded for " + expense.getTag();
        }

        expenseRepository.save(expense);
        return "Expense saved successfully";
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public ExpenseAnalysisDTO getMonthlyAnalysisWithBudget(int month, int year) {
        List<Expense> expenses = expenseRepository.findAllByMonthAndYear(month, year);
        return analyzeExpenses(expenses, month, year);
    }

    public ExpenseAnalysisDTO getYearlyAnalysis(int year) {
        List<Expense> expenses = expenseRepository.findAllByYear(year);
        return analyzeExpenses(expenses, -1, year);
    }

    private ExpenseAnalysisDTO analyzeExpenses(List<Expense> expenses, int month, int year) {
        Map<String, Double> categoryTotals = new HashMap<>();
        Map<String, Double> variableTagTotals = new HashMap<>();
        Map<String, Double> miscellaneousTagTotals = new HashMap<>();
        Map<String, String> budgetWarnings = new HashMap<>();

        for (Expense expense : expenses) {
            String category = expense.getCategory().name();
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + expense.getAmount());

            if (expense.getCategory() == ExpenseCategory.VARIABLE) {
                String tag = expense.getTag().name();
                variableTagTotals.put(tag, variableTagTotals.getOrDefault(tag, 0.0) + expense.getAmount());
            }

            if (expense.getCategory() == ExpenseCategory.MISCELLANEOUS) {
                String tag = expense.getTag().name();
                miscellaneousTagTotals.put(tag, miscellaneousTagTotals.getOrDefault(tag, 0.0) + expense.getAmount());
            }
        }

        variableTagTotals.forEach((tag, total) -> {
            Double limit = budgetLimitService.getLimit(ExpenseCategory.VARIABLE, ExpenseTag.valueOf(tag));
            if (limit != null && total > limit) {
                budgetWarnings.put(tag, "Limit exceeded: " + total + " / " + limit);
            }
        });

        miscellaneousTagTotals.forEach((tag, total) -> {
            Double limit = budgetLimitService.getLimit(ExpenseCategory.MISCELLANEOUS, ExpenseTag.valueOf(tag));
            if (limit != null && total > limit) {
                budgetWarnings.put(tag, "Limit exceeded: " + total + " / " + limit);
            }
        });

        String topVariableTag = variableTagTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);

        String topMiscTag = miscellaneousTagTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);

        return new ExpenseAnalysisDTO(categoryTotals, variableTagTotals, miscellaneousTagTotals, topVariableTag, topMiscTag, budgetWarnings);
    }
}

