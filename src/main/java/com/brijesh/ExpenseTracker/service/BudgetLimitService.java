package com.brijesh.ExpenseTracker.service;

import com.brijesh.ExpenseTracker.entity.BudgetLimit;
import com.brijesh.ExpenseTracker.repository.BudgetLimitRepository;
import com.brijesh.ExpenseTracker.utils.ExpenseCategory;
import com.brijesh.ExpenseTracker.utils.ExpenseTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//@Service
//public class BudgetLimitService {
//
//    @Autowired
//    private BudgetLimitRepository repository;
//
//    public BudgetLimit setLimit(ExpenseCategory category, ExpenseTag tag, double limit) {
//        Optional<BudgetLimit> existing = repository.findByCategoryAndTag(category, tag);
//        BudgetLimit budgetLimit = existing.orElse(new BudgetLimit());
//        budgetLimit.setCategory(category);
//        budgetLimit.setTag(tag);
//        budgetLimit.setLimitAmount(limit);
//        return repository.save(budgetLimit);
//    }
//
//    public Optional<BudgetLimit> getLimit(ExpenseCategory category, ExpenseTag tag) {
//        return repository.findByCategoryAndTag(category, tag);
//    }
//
//    public List<BudgetLimit> getAllLimits() {
//        return repository.findAll();
//    }
//
//    public void deleteLimit(Long id) {
//        repository.deleteById(id);
//    }
//
//    public boolean isExceedingLimit(ExpenseCategory category, ExpenseTag tag, double currentSpent) {
//        Optional<BudgetLimit> limitOpt = repository.findByCategoryAndTag(category, tag);
//        return limitOpt.map(limit -> currentSpent > limit.getLimitAmount()).orElse(false);
//    }
//}

@Service
public class BudgetLimitService {

    @Autowired
    private BudgetLimitRepository repository;

    public Double getLimit(ExpenseCategory category, ExpenseTag tag) {
        Optional<BudgetLimit> budget = repository.findByCategoryAndTag(category, tag);
        return budget.map(BudgetLimit::getLimitAmount).orElse(null);
    }
}