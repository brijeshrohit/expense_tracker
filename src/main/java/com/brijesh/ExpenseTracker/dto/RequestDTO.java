package com.brijesh.ExpenseTracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class RequestDTO {
    private String description;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private String tag;
    private String category;

    private double amount;

}
