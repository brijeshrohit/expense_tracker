package com.brijesh.ExpenseAnalysis.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TagOverBudgetDTO {
    private String category;
    private String tag;
    private double budget;
    private double actual;
    private double exceededBy;
}
