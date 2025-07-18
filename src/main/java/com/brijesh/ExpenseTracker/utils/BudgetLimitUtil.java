package com.brijesh.ExpenseTracker.utils;

import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

@Getter
public class BudgetLimitUtil {
    private static final Map<ExpenseCategory, Map<ExpenseTag, Double>> budgetLimits = new EnumMap<>(ExpenseCategory.class);

    static {
        // Fixed Budgets
        setLimit(ExpenseCategory.FIXED, ExpenseTag.RENT, 15000.0);
        setLimit(ExpenseCategory.FIXED, ExpenseTag.ELECTRICITY, 2000.0);

        // Variable Budgets
        setLimit(ExpenseCategory.VARIABLE, ExpenseTag.PETROL, 3000.0);
        setLimit(ExpenseCategory.VARIABLE, ExpenseTag.GROCERIES, 4000.0);
        setLimit(ExpenseCategory.VARIABLE, ExpenseTag.CHAI_NASHTA, 1500.0);

        // Misc Budgets
        setLimit(ExpenseCategory.MISCELLANEOUS, ExpenseTag.GROOMING, 1000.0);
        setLimit(ExpenseCategory.MISCELLANEOUS, ExpenseTag.UDHAR, 2000.0);
    }

    public static void setLimit(ExpenseCategory category, ExpenseTag tag, Double limit) {
        budgetLimits.computeIfAbsent(category, k -> new EnumMap<>(ExpenseTag.class)).put(tag, limit);
    }

    public static Double getLimit(ExpenseCategory category, ExpenseTag tag) {
        return budgetLimits.getOrDefault(category, new EnumMap<>(ExpenseTag.class)).get(tag);
    }

    public static boolean isOverLimit(ExpenseCategory category, ExpenseTag tag, Double totalSpent) {
        Double limit = getLimit(category, tag);
        return limit != null && totalSpent > limit;
    }

    public static Map<ExpenseCategory, Map<ExpenseTag, Double>> getAllLimits() {
        return budgetLimits;
    }
}


