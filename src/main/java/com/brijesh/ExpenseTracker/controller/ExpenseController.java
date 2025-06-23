package com.brijesh.ExpenseTracker.controller;

import com.brijesh.ExpenseTracker.entity.Expense;
import com.brijesh.ExpenseTracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    //    add expenses
    @PostMapping("/addexpense")
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense){
        Expense newExpense = expenseService.addExpense(expense);
        return ResponseEntity.ok(newExpense);
    }


//    get all expenses
//    delete expenses
//    update expenses

//    get expenses by id

//    get expenses by category
//    get expenses by tag
}
