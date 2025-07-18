package com.brijesh.ExpenseTracker.service;

import com.brijesh.ExpenseTracker.dto.ExpenseAnalysisDTO;
import com.brijesh.ExpenseTracker.entity.Expense;
import com.brijesh.ExpenseTracker.repository.ExpenseRepository;
import com.brijesh.ExpenseTracker.utils.ExpenseCategory;
import com.brijesh.ExpenseTracker.utils.ExpenseTag;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.*;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository repository;

    public Expense saveExpense(Expense expense) {
        validateEnumValues(expense);
        return repository.save(expense);
    }

    public Expense updateExpense(String id, Expense updatedExpense) {
        validateEnumValues(updatedExpense);
        return repository.findById(id).map(expense -> {
            updatedExpense.setId(id);
            return repository.save(updatedExpense);
        }).orElseThrow(() -> new NoSuchElementException("Expense not found with id: " + id));
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

    public List<Expense> getExpensesAfterDate(LocalDate date) {
        return repository.findByDateAfter(date);
    }

    public ByteArrayInputStream exportExpensesToCSV() throws Exception {
        List<Expense> expenses = repository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(out);

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("ID", "Category", "Tag", "Date", "Description", "Amount"))) {
            for (Expense e : expenses) {
                csvPrinter.printRecord(
                        e.getId(),
                        e.getCategory(),
                        e.getTag(),
                        e.getDate(),
                        e.getDescription(),
                        e.getAmount()
                );
            }
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ExpenseAnalysisDTO getMonthlyAnalysis(int month, int year) {
        validateMonthYear(month, year);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        List<Expense> expenses = repository.findAllByDateBetween(start, end);
        return buildAnalysis(expenses);
    }

    public ExpenseAnalysisDTO getYearlyAnalysis(int year) {
        validateMonthYear(1, year);
        List<Expense> expenses = repository.findAllByYear(year);
        return buildAnalysis(expenses);
    }

    private void validateMonthYear(int month, int year) {
        int currentYear = LocalDate.now().getYear();
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month. It should be between 1 and 12.");
        }
        if (year < currentYear - 1 || year > currentYear) {
            throw new IllegalArgumentException("Invalid year. Only current or previous year is allowed.");
        }
    }

    private void validateEnumValues(Expense expense) {
        if (expense.getCategory() == null || expense.getTag() == null) {
            throw new IllegalArgumentException("Category and Tag cannot be null.");
        }
        boolean isValid = EnumSet.allOf(ExpenseTag.class).contains(expense.getTag());
        if (!isValid) {
            throw new IllegalArgumentException("Invalid tag: " + expense.getTag());
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
