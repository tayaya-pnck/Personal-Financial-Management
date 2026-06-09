package com.finance.tracker.budget.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class BudgetRequest {

    @NotNull(message = "Category is required")
    private UUID categoryId;

    @NotNull(message = "Month is required (format: yyyy-MM)")
    private String month;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}
