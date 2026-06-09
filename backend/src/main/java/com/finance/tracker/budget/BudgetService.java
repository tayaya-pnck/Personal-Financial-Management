package com.finance.tracker.budget;

import com.finance.tracker.budget.dto.BudgetRequest;
import com.finance.tracker.budget.dto.BudgetResponse;
import com.finance.tracker.category.CategoryRepository;
import com.finance.tracker.common.ResourceNotFoundException;
import com.finance.tracker.transaction.Transaction;
import com.finance.tracker.transaction.TransactionRepository;
import com.finance.tracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public List<BudgetResponse> getUserBudgets(UUID userId, String month) {
        var budgets = budgetRepository.findByUserIdAndBudgetMonthOrderByCategoryNameAsc(userId, month);

        var start = LocalDate.parse(month + "-01");
        var end = start.withDayOfMonth(start.lengthOfMonth());

        return budgets.stream()
            .map(budget -> {
                var spent = transactionRepository.sumByCategoryAndDateBetween(
                    userId, budget.getCategory().getId(), start, end);
                return budgetMapper.toResponseWithSpent(budget, spent);
            })
            .toList();
    }

    public BudgetResponse getBudget(UUID userId, UUID budgetId) {
        var budget = findBudget(userId, budgetId);
        var month = budget.getBudgetMonth();
        var start = LocalDate.parse(month + "-01");
        var end = start.withDayOfMonth(start.lengthOfMonth());
        var spent = transactionRepository.sumByCategoryAndDateBetween(
            userId, budget.getCategory().getId(), start, end);
        return budgetMapper.toResponseWithSpent(budget, spent);
    }

    @Transactional
    public BudgetResponse createBudget(UUID userId, BudgetRequest request, User user) {
        var category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        if (!category.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Category", request.getCategoryId());
        }

        var existing = budgetRepository.findByUserIdAndCategoryIdAndBudgetMonth(userId, request.getCategoryId(), request.getMonth());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Budget already exists for this category and month");
        }

        var budget = budgetMapper.toEntity(request);
        budget.setUser(user);
        budget.setCategory(category);
        budget = budgetRepository.save(budget);

        var spent = BigDecimal.ZERO;
        return budgetMapper.toResponseWithSpent(budget, spent);
    }

    @Transactional
    public BudgetResponse updateBudget(UUID userId, UUID budgetId, BudgetRequest request) {
        var budget = findBudget(userId, budgetId);
        budget.setAmount(request.getAmount());
        budget = budgetRepository.save(budget);

        var month = budget.getBudgetMonth();
        var start = LocalDate.parse(month + "-01");
        var end = start.withDayOfMonth(start.lengthOfMonth());
        var spent = transactionRepository.sumByCategoryAndDateBetween(
            userId, budget.getCategory().getId(), start, end);
        return budgetMapper.toResponseWithSpent(budget, spent);
    }

    @Transactional
    public void deleteBudget(UUID userId, UUID budgetId) {
        var budget = findBudget(userId, budgetId);
        budgetRepository.delete(budget);
    }

    private Budget findBudget(UUID userId, UUID budgetId) {
        var budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new ResourceNotFoundException("Budget", budgetId));
        if (!budget.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Budget", budgetId);
        }
        return budget;
    }
}
