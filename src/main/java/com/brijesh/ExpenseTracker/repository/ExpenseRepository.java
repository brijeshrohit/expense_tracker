package com.brijesh.ExpenseTracker.repository;

import com.brijesh.ExpenseTracker.entity.Expense;
import com.brijesh.ExpenseTracker.utils.ExpenseCategory;
import com.brijesh.ExpenseTracker.utils.ExpenseTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e WHERE e.category = :category AND e.tag = :tag AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
    List<Expense> findByCategoryAndTagAndMonthYear(ExpenseCategory category, ExpenseTag tag, int month, int year);

    @Query("SELECT e FROM Expense e WHERE MONTH(e.date) = :month AND YEAR(e.date) = :year")
    List<Expense> findAllByMonthAndYear(int month, int year);

    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year")
    List<Expense> findAllByYear(int year);
}
