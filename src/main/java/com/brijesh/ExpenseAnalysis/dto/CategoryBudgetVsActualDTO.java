package com.brijesh.ExpenseAnalysis.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategoryBudgetVsActualDTO {
    private String category;
    private double budget;
    private double actual;
}
