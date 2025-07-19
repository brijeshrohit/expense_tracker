package com.brijesh.ExpenseAnalysis.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MonthBudgetExceedDTO {
    private String month;
    private double expense;
}
