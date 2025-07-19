package com.brijesh.ExpenseAnalysis.entity;

import com.brijesh.ExpenseAnalysis.util.ExpenseCategory;
import com.brijesh.ExpenseAnalysis.util.ExpenseTag;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "expense")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseTag tag;

    @Column(nullable = false)
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;

    @Column(nullable = false)
    @Size(min = 8, message = "Description must be at least 8 characters long")
    private String description;

    @Column(nullable = false)
    @Min(value = 1, message = "Amount must be greater than 0")
    private double amount;
}
