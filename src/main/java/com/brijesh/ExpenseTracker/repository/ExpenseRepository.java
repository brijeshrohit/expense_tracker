package com.brijesh.ExpenseTracker.repository;

import com.brijesh.ExpenseTracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, String> {
    List<Expense> findAllByDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = ?1")
    List<Expense> findAllByYear(int year);
}
