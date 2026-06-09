package com.finance.tracker.account.dto;

import com.finance.tracker.account.Account.AccountType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AccountResponse {
    private UUID id;
    private String name;
    private AccountType type;
    private BigDecimal balance;
    private String currency;
    private String color;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
