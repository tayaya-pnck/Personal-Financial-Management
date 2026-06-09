package com.finance.tracker.account.dto;

import com.finance.tracker.account.Account.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountRequest {

    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotNull(message = "Account type is required")
    private AccountType type;

    private BigDecimal balance = BigDecimal.ZERO;

    private String currency = "USD";

    private String color;
}
