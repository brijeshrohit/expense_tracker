package com.brijesh.ExpenseTracker.controller;

import com.brijesh.ExpenseTracker.dto.ExpenseAnalysisDTO;
import com.brijesh.ExpenseTracker.entity.Expense;
import com.brijesh.ExpenseTracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/add")
    public ResponseEntity<String> addExpense(@RequestBody Expense expense) {
        String message = expenseService.saveExpenseWithBudgetCheck(expense);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @GetMapping("/analysis/monthly")
    public ResponseEntity<ExpenseAnalysisDTO> getMonthlyAnalysis(@RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(expenseService.getMonthlyAnalysisWithBudget(month, year));
    }

    @GetMapping("/analysis/yearly")
    public ResponseEntity<ExpenseAnalysisDTO> getYearlyAnalysis(@RequestParam int year) {
        return ResponseEntity.ok(expenseService.getYearlyAnalysis(year));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok("Expense deleted successfully");
    }
}