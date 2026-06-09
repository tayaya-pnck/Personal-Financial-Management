package com.finance.tracker.report;

import com.finance.tracker.account.AccountRepository;
import com.finance.tracker.account.AccountMapper;
import com.finance.tracker.category.CategoryRepository;
import com.finance.tracker.transaction.Transaction;
import com.finance.tracker.transaction.TransactionMapper;
import com.finance.tracker.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    public DashboardResponse getDashboard(UUID userId) {
        var assets = accountRepository.sumAssetsByUserId(userId);
        var liabilities = accountRepository.sumLiabilitiesByUserId(userId);
        var netWorth = assets.subtract(liabilities);

        var monthStart = LocalDate.now().withDayOfMonth(1);
        var monthEnd = LocalDate.now();

        var income = transactionRepository.sumByTypeAndDateBetween(
            userId, Transaction.TransactionType.INCOME, monthStart, monthEnd);
        var expense = transactionRepository.sumByTypeAndDateBetween(
            userId, Transaction.TransactionType.EXPENSE, monthStart, monthEnd);

        var savingsRate = income.compareTo(BigDecimal.ZERO) > 0
            ? income.subtract(expense).multiply(BigDecimal.valueOf(100))
                .divide(income, 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        var accounts = accountRepository.findByUserIdAndIsArchivedFalseOrderByNameAsc(userId)
            .stream()
            .map(accountMapper::toResponse)
            .toList();

        var recentTransactions = transactionRepository
            .findByUserIdOrderByDateDescCreatedAtDesc(userId, PageRequest.of(0, 5))
            .getContent()
            .stream()
            .map(transactionMapper::toResponse)
            .toList();

        return DashboardResponse.builder()
            .netWorth(netWorth)
            .totalAssets(assets)
            .totalLiabilities(liabilities)
            .monthlyIncome(income)
            .monthlyExpense(expense)
            .savingsRate(savingsRate)
            .accounts(accounts)
            .recentTransactions(recentTransactions)
            .build();
    }

    public List<CategorySpending> getSpendingByCategory(UUID userId, LocalDate start, LocalDate end) {
        var results = transactionRepository.sumGroupedByCategory(userId, Transaction.TransactionType.EXPENSE, start, end);
        var total = results.stream()
            .map(row -> (BigDecimal) row[1])
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return results.stream()
            .map(row -> {
                var categoryId = (UUID) row[0];
                var amount = (BigDecimal) row[1];
                var percentage = total.compareTo(BigDecimal.ZERO) > 0
                    ? amount.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;

                var category = categoryId != null
                    ? categoryRepository.findById(categoryId).orElse(null)
                    : null;

                return CategorySpending.builder()
                    .categoryId(categoryId)
                    .categoryName(category != null ? category.getName() : "Uncategorized")
                    .categoryIcon(category != null ? category.getIcon() : null)
                    .categoryColor(category != null ? category.getColor() : null)
                    .amount(amount)
                    .percentage(percentage)
                    .build();
            })
            .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
            .toList();
    }

    public List<MonthlySummary> getMonthlySummary(UUID userId, int months) {
        var summaries = new ArrayList<MonthlySummary>();
        var today = LocalDate.now();

        for (int i = months - 1; i >= 0; i--) {
            var month = today.minusMonths(i);
            var start = month.withDayOfMonth(1);
            var end = month.withDayOfMonth(month.lengthOfMonth());

            var income = transactionRepository.sumByTypeAndDateBetween(
                userId, Transaction.TransactionType.INCOME, start, end);
            var expense = transactionRepository.sumByTypeAndDateBetween(
                userId, Transaction.TransactionType.EXPENSE, start, end);

            summaries.add(MonthlySummary.builder()
                .month(month.getYear() + "-" + String.format("%02d", month.getMonthValue()))
                .income(income)
                .expense(expense)
                .net(income.subtract(expense))
                .build());
        }

        return summaries;
    }
}
