package com.brijesh.ExpenseAnalysis.util;

import java.util.Map;

public class BudgetLimitUtil {
    public static final double TOTAL_MONTHLY_BUDGET = 83000.0;
    public static final double TOTAL_FIXED_EXPENSE_BUDGET = 22368.0;
    public static final double TOTAL_INVESTMENT_BUDGET = 35000.0;
    public static final double TOTAL_INSURANCE_BUDGET = 13680.64;
    public static final double TOTAL_VARIABLE_BUDGET = 7800;

    public static final Map<ExpenseTag, Double> TAG_BUDGETS = Map.ofEntries(
            // FIXED
            Map.entry(ExpenseTag.RENT, 16500.0),
            Map.entry(ExpenseTag.ELECTRICITY, 534.33),
            Map.entry(ExpenseTag.INTERNET, 600.0),
            Map.entry(ExpenseTag.COOK, 2834.333333),
            Map.entry(ExpenseTag.CLEANER, 500.0),
            Map.entry(ExpenseTag.DAIRY, 900.0),
            Map.entry(ExpenseTag.FURNITURE_RENT, 500.0),

            // VARIABLE
            Map.entry(ExpenseTag.PETROL, 2000.0),
            Map.entry(ExpenseTag.GROCERIES, 2000.0),
            Map.entry(ExpenseTag.ZOMATO_BLINKIT_BIGBASKET, 2000.0),
            Map.entry(ExpenseTag.CHAI_NASHTA, 300.0),
            Map.entry(ExpenseTag.SHOPPING, 500.0),
            Map.entry(ExpenseTag.DRY_FRUITS, 1000.0),

            // INVESTMENT
            Map.entry(ExpenseTag.SIP_LONG_TERM, 20000.0),
            Map.entry(ExpenseTag.SIP_SHORT_TERM, 15000.0),

            // INSURANCE
            Map.entry(ExpenseTag.TERM_LIFE, 13680.64),

            // MISCELLANEOUS (excluding CUSTOM)
            Map.entry(ExpenseTag.TRAIN_TICKET, 1000.0),
            Map.entry(ExpenseTag.FLIGHT_TICKET, 4000.0),
            Map.entry(ExpenseTag.CYLINDER, 1200.0),
            Map.entry(ExpenseTag.GROOMING, 200.0),
            Map.entry(ExpenseTag.URBAN_CLAP, 1500.0),
            Map.entry(ExpenseTag.SEND_ME, 1000.0),
            Map.entry(ExpenseTag.UDHAR, 00.0)
    );
}

