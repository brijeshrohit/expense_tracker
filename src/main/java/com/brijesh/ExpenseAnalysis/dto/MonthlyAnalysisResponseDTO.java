package com.brijesh.ExpenseAnalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAnalysisResponseDTO {
    private Map<String, Map<String, Double>> categoryTagWiseExpenses;
    private Map<String, Object> categoryBudgetVsExpense;
    private Map<String, Map<String, Double>> overBudgetCategories;
    private Map<String, Map<String, Double>> overBudgetTagsInOverBudgetCategories;
    private Map<String, Object> summary;
}

