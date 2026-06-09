package com.finance.tracker.report;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MonthlySummary {
    private String month;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal net;
}
