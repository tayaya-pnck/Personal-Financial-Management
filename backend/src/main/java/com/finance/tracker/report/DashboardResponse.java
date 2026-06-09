package com.finance.tracker.report;

import com.finance.tracker.account.dto.AccountResponse;
import com.finance.tracker.transaction.dto.TransactionResponse;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class DashboardResponse {
    private BigDecimal netWorth;
    private BigDecimal totalAssets;
    private BigDecimal totalLiabilities;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpense;
    private BigDecimal savingsRate;
    private List<AccountResponse> accounts;
    private List<TransactionResponse> recentTransactions;
}
