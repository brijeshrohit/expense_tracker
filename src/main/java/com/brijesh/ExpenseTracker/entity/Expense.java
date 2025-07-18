package com.brijesh.ExpenseTracker.entity;

import com.brijesh.ExpenseTracker.utils.ExpenseCategory;
import com.brijesh.ExpenseTracker.utils.ExpenseTag;
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
    private String id = UUID.randomUUID().toString();

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
