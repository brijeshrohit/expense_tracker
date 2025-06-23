package com.brijesh.ExpenseTracker.service;

import com.brijesh.ExpenseTracker.entity.Expense;
import com.brijesh.ExpenseTracker.repository.ExpenseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    @PersistenceContext
    private EntityManager entityManager;


    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository){
        this.expenseRepository = expenseRepository;
    }

    //    add expenses
    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> getExpenseByDate(LocalDate date){
        String sql = "SELECT * FROM EXPENSE WHERE DATE > :date";
        return entityManager.createQuery(sql, Expense.class)
                .setParameter("date", date)
                .getResultList();
    }

//    get all expenses
//    delete expenses
//    update expenses

//    get expenses by id

//    get expenses by category
//    get expenses by tag

}
