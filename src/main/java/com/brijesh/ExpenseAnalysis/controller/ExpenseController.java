package com.brijesh.ExpenseAnalysis.controller;

import com.brijesh.ExpenseAnalysis.dto.MonthlyAnalysisResponseDTO;
import com.brijesh.ExpenseAnalysis.dto.MonthlyAnalysisRequestDTO;
import com.brijesh.ExpenseAnalysis.dto.YearlyAnalysisRequestDTO;
import com.brijesh.ExpenseAnalysis.dto.YearlyAnalysisResponseDTO;
import com.brijesh.ExpenseAnalysis.entity.Expense;
import com.brijesh.ExpenseAnalysis.service.ExpenseService;
import com.brijesh.ExpenseAnalysis.util.StatusMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/monthly-analysis")
    public ResponseEntity<?> getMonthlyAnalysis(@RequestBody MonthlyAnalysisRequestDTO request) {
        MonthlyAnalysisResponseDTO response = expenseService.getMonthlyAnalysis(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/yearly-analysis")
    public ResponseEntity<YearlyAnalysisResponseDTO> getYearlyAnalysis(@RequestBody YearlyAnalysisRequestDTO request) {
        YearlyAnalysisResponseDTO response = expenseService.getYearlyAnalysis(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/monthly-expenses")
    public ResponseEntity<List<Expense>> getMonthlyExpenses(@RequestBody MonthlyAnalysisRequestDTO request) {
        List<Expense> expenses = expenseService.getMonthlyExpenses(request);
        return ResponseEntity.ok(expenses);
    }

    @PostMapping("/yearly-expenses")
    public ResponseEntity<List<Expense>> getYearlyExpenses(@RequestBody YearlyAnalysisRequestDTO request) {
        List<Expense> expenses = expenseService.getYearlyExpenses(request);
        return ResponseEntity.ok(expenses);
    }

}

