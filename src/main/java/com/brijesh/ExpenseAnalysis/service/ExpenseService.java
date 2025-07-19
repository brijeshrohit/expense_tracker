package com.brijesh.ExpenseAnalysis.service;

import com.brijesh.ExpenseAnalysis.entity.Expense;
import com.brijesh.ExpenseAnalysis.repository.ExpenseRepository;
import com.brijesh.ExpenseAnalysis.util.BudgetLimitUtil;
import com.brijesh.ExpenseAnalysis.util.ExpenseValidationMap;
import com.brijesh.ExpenseAnalysis.util.StatusMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public Map<String, Object> addExpenseAndReturnAlerts(Expense expense) {
        double totalForMonth = expenseRepository.sumMonthlyTotal(expense.getDate().getMonthValue(), expense.getDate().getYear());
        double categoryTotalForMonth = expenseRepository.sumMonthlyCategoryTotal(expense.getCategory(), expense.getDate().getMonthValue(), expense.getDate().getYear());
        double tagTotalForMonth = expenseRepository.sumMonthlyTagTotal(expense.getTag(), expense.getDate().getMonthValue(), expense.getDate().getYear());

        double monthlyLimit = BudgetLimitUtil.TOTAL_MONTHLY_BUDGET;
        double categoryLimit = switch (expense.getCategory()) {
            case FIXED -> BudgetLimitUtil.TOTAL_FIXED_EXPENSE_BUDGET;
            case VARIABLE -> BudgetLimitUtil.TOTAL_VARIABLE_BUDGET;
            case INVESTMENT -> BudgetLimitUtil.TOTAL_INVESTMENT_BUDGET;
            case INSURANCE -> BudgetLimitUtil.TOTAL_INSURANCE_BUDGET;
            default -> Double.MAX_VALUE;
        };

        double tagLimit = BudgetLimitUtil.TAG_BUDGETS.getOrDefault(expense.getTag(), Double.MAX_VALUE);

        List<String> alerts = new ArrayList<>();

        if ((totalForMonth + expense.getAmount()) > monthlyLimit) {
            alerts.add("⚠️ Total monthly budget exceeded.");
        }
        if ((categoryTotalForMonth + expense.getAmount()) > categoryLimit) {
            alerts.add("⚠️ " + expense.getCategory() + " category budget exceeded.");
        }
        if ((tagTotalForMonth + expense.getAmount()) > tagLimit) {
            alerts.add("⚠️ Budget exceeded for tag: " + expense.getTag());
        }

        Map<String, Object> response = new HashMap<>();
        if(ExpenseValidationMap.isValidTagForCategory(expense.getCategory(), expense.getTag())) {
            expenseRepository.save(expense);
            response.put(StatusMap.STATUS.name(), StatusMap.SUCCESS.name());
            response.put("expense", expense);
            response.put("alerts", alerts);
        } else {
            response.put(StatusMap.STATUS.name(), StatusMap.ERROR.name());
            String tip = "Please add correct tag for " + expense.getCategory();
            response.put("tip", tip);
        }

        return response;
    }


    public List<Expense> getAllExpenses() {
        List<Expense> expenseList = expenseRepository.findAll();
        log.info("Found {} expense", expenseList.size());
        return expenseList;
    }
}
