package com.brijesh.ExpenseAnalysis.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategoryExpenseDTO {
    private String category;
    private double amount;
}
