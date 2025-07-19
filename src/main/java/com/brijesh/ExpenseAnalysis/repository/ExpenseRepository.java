package com.brijesh.ExpenseAnalysis.repository;

import com.brijesh.ExpenseAnalysis.entity.Expense;
import com.brijesh.ExpenseAnalysis.util.ExpenseCategory;
import com.brijesh.ExpenseAnalysis.util.ExpenseTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE MONTH(e.date) = :month AND YEAR(e.date) = :year")
    double sumMonthlyTotal(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.category = :category AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
    double sumMonthlyCategoryTotal(@Param("category") ExpenseCategory category, @Param("month") int month, @Param("year") int year);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.tag = :tag AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
    double sumMonthlyTagTotal(@Param("tag") ExpenseTag tag, @Param("month") int month, @Param("year") int year);

    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
