package com.brijesh.ExpenseTracker.controller;

import com.brijesh.ExpenseTracker.dto.ExpenseAnalysisDTO;
import com.brijesh.ExpenseTracker.entity.Expense;
import com.brijesh.ExpenseTracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService service;

    @PostMapping
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense) {
        return ResponseEntity.ok(service.saveExpense(expense));
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(service.getAllExpenses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable String id) {
        Optional<Expense> expense = service.getExpenseById(id);
        return expense.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable String id) {
        service.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/monthly")
    public ResponseEntity<ExpenseAnalysisDTO> getMonthlyReport(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(service.getMonthlyAnalysis(month, year));
    }

    @GetMapping("/yearly")
    public ResponseEntity<ExpenseAnalysisDTO> getYearlyReport(@RequestParam int year) {
        return ResponseEntity.ok(service.getYearlyAnalysis(year));
    }
}