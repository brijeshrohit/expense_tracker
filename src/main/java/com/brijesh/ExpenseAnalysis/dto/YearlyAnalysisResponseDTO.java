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
public class YearlyAnalysisResponseDTO {
    private List<CategoryExpenseDTO> categoryExpenses;
    private List<CategoryBudgetVsActualDTO> budgetVsActual;
    private List<TagOverBudgetDTO> tagOverBudgetDetails;
    private SummaryDTO summary;
    private List<MonthBudgetExceedDTO> monthsExceedingBudget;
}


