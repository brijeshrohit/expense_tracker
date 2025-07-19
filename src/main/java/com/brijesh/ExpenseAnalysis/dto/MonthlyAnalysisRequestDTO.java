package com.brijesh.ExpenseAnalysis.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAnalysisRequestDTO {
    private String month;  // e.g., "July"
    private int year;      // e.g., 2025
}
