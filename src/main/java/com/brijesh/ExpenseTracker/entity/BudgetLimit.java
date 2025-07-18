package com.brijesh.ExpenseTracker.entity;

import com.brijesh.ExpenseTracker.utils.ExpenseCategory;
import com.brijesh.ExpenseTracker.utils.ExpenseTag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    @Enumerated(EnumType.STRING)
    private ExpenseTag tag;

    private double limitAmount;

}