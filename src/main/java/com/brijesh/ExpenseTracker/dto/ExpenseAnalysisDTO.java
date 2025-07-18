package com.brijesh.ExpenseTracker.dto;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseAnalysisDTO {
    private Map<String, Double> categoryTotals;
    private Map<String, Double> variableTagTotals;
    private Map<String, Double> miscellaneousTagTotals;
    private String topVariableTag;
    private String topMiscTag;
    private Map<String, String> budgetWarnings;
}