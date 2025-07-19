package com.brijesh.ExpenseAnalysis.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SummaryDTO {
    private double totalExpense;
    private double totalBudget;
    private double budgetRemaining;
}