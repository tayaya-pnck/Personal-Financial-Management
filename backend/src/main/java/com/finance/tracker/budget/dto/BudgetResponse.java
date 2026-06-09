package com.finance.tracker.budget.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class BudgetResponse {
    private UUID id;
    private UUID categoryId;
    private String categoryName;
    private String categoryIcon;
    private String categoryColor;
    private String month;
    private BigDecimal amount;
    private BigDecimal spent;
    private BigDecimal remaining;
    private double percentageUsed;
    private LocalDateTime createdAt;
}
