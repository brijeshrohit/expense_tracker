package com.brijesh.ExpenseTracker.repository;

import com.brijesh.ExpenseTracker.entity.BudgetLimit;
import com.brijesh.ExpenseTracker.utils.ExpenseCategory;
import com.brijesh.ExpenseTracker.utils.ExpenseTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetLimitRepository extends JpaRepository<BudgetLimit, Long> {
    Optional<BudgetLimit> findByCategoryAndTag(ExpenseCategory category, ExpenseTag tag);
}
