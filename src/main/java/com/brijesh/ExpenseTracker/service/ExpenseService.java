package com.brijesh.expense_tracker.service;

import com.brijesh.expense_tracker.entity.Expense;
import com.brijesh.expense_tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository){
        this.expenseRepository = expenseRepository;
    }

    //    add expenses
    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }


//    get all expenses
//    delete expenses
//    update expenses

//    get expenses by id

//    get expenses by category
//    get expenses by tag

}
