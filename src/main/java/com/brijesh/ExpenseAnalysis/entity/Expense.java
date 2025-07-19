package com.brijesh.ExpenseAnalysis.entity;

import com.brijesh.ExpenseAnalysis.util.ExpenseCategory;
import com.brijesh.ExpenseAnalysis.util.ExpenseTag;
import jakarta.persistence.*;
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
    private LocalDate date;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double amount;
}
