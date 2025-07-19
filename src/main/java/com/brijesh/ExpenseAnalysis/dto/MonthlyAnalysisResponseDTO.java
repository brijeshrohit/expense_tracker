package com.brijesh.ExpenseAnalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAnalysisResponseDTO {
    private List<CategoryExpenseDTO> categoryExpenses;          // for Pie Chart
    private List<CategoryBudgetVsActualDTO> budgetVsActual;     // for Bar Graph
    private List<TagOverBudgetDTO> tagOverBudgetDetails;        // optional, only if any tag crossed
    private SummaryDTO summary;                                 // total, budget, remaining
}


