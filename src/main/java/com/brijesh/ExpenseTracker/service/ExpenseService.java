package com.brijesh.ExpenseTracker.service;

import com.brijesh.ExpenseTracker.dto.ExpenseAnalysisDTO;
import com.brijesh.ExpenseTracker.entity.Expense;
import com.brijesh.ExpenseTracker.repository.ExpenseRepository;
import com.brijesh.ExpenseTracker.utils.ExpenseCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository repository;

    public Expense saveExpense(Expense expense) {
        return repository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return repository.findAll();
    }

    public Optional<Expense> getExpenseById(String id) {
        return repository.findById(id);
    }

    public void deleteExpense(String id) {
        repository.deleteById(id);
    }

    public ExpenseAnalysisDTO getMonthlyAnalysis(int month, int year) {
        validateMonthYear(month, year);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        List<Expense> expenses = repository.findAllByDateBetween(start, end);
        return buildAnalysis(expenses);
    }

    public ExpenseAnalysisDTO getYearlyAnalysis(int year) {
        validateMonthYear(1, year); // dummy month for year-only check
        List<Expense> expenses = repository.findAllByYear(year);
        return buildAnalysis(expenses);
    }

    private void validateMonthYear(int month, int year) {
        int currentYear = LocalDate.now().getYear();
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month. It should be between 1 and 12.");
        }
        if (year > currentYear) {
            throw new IllegalArgumentException("Invalid year. Only current or previous year is allowed.");
        }
    }

    private ExpenseAnalysisDTO buildAnalysis(List<Expense> expenses) {
        Map<String, Double> categoryTotals = new HashMap<>();
        Map<String, Double> variableTagTotals = new HashMap<>();
        Map<String, Double> miscellaneousTagTotals = new HashMap<>();

        for (ExpenseCategory cat : ExpenseCategory.values()) {
            categoryTotals.put(cat.name(), 0.0);
        }

        for (Expense exp : expenses) {
            categoryTotals.put(exp.getCategory().name(),
                    categoryTotals.get(exp.getCategory().name()) + exp.getAmount());

            if (exp.getCategory() == ExpenseCategory.VARIABLE) {
                variableTagTotals.put(exp.getTag().name(),
                        variableTagTotals.getOrDefault(exp.getTag().name(), 0.0) + exp.getAmount());
            }

            if (exp.getCategory() == ExpenseCategory.MISCELLANEOUS) {
                miscellaneousTagTotals.put(exp.getTag().name(),
                        miscellaneousTagTotals.getOrDefault(exp.getTag().name(), 0.0) + exp.getAmount());
            }
        }

        return new ExpenseAnalysisDTO(categoryTotals, variableTagTotals, miscellaneousTagTotals);
    }
}