package com.finance.tracker.goal.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GoalResponse {
    private UUID id;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private double percentageComplete;
    private LocalDate deadline;
    private long daysRemaining;
    private String icon;
    private String color;
    private boolean isAchieved;
    private LocalDateTime createdAt;
}
