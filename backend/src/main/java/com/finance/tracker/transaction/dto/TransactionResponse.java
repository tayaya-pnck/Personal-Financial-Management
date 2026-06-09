package com.finance.tracker.transaction.dto;

import com.finance.tracker.transaction.Transaction.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class TransactionResponse {
    private UUID id;
    private UUID accountId;
    private String accountName;
    private UUID categoryId;
    private String categoryName;
    private String categoryIcon;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private TransactionType type;
    private boolean isRecurring;
    private LocalDateTime createdAt;
}
