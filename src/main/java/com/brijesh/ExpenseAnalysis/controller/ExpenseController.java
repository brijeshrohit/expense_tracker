package com.brijesh.ExpenseAnalysis.controller;

import com.brijesh.ExpenseAnalysis.entity.Expense;
import com.brijesh.ExpenseAnalysis.service.ExpenseService;
import com.brijesh.ExpenseAnalysis.util.StatusMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/add")
    public ResponseEntity<?> addExpense(@RequestBody Expense expense) {
        Map<String, Object>  response = expenseService.addExpenseAndReturnAlerts(expense);
        if (response.get(StatusMap.STATUS.name()) == StatusMap.SUCCESS.name() ) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.unprocessableEntity().body(response);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        boolean deleted = expenseService.deleteExpenseById(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Expense deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Expense not found with ID: " + id));
        }
    }

}

