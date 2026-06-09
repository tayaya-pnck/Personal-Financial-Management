package com.finance.tracker.budget;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    List<Budget> findByUserIdAndBudgetMonthOrderByCategoryNameAsc(UUID userId, String budgetMonth);

    Optional<Budget> findByUserIdAndCategoryIdAndBudgetMonth(UUID userId, UUID categoryId, String budgetMonth);

    List<Budget> findByUserIdAndBudgetMonthAndIsNotificationSentFalse(UUID userId, String budgetMonth);

    boolean existsByUserIdAndCategoryIdAndBudgetMonth(UUID userId, UUID categoryId, String budgetMonth);
}
