package com.finance.tracker.budget;

import com.finance.tracker.budget.dto.BudgetRequest;
import com.finance.tracker.budget.dto.BudgetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categoryIcon", source = "category.icon")
    @Mapping(target = "categoryColor", source = "category.color")
    @Mapping(target = "month", source = "budgetMonth")
    @Mapping(target = "spent", ignore = true)
    @Mapping(target = "remaining", ignore = true)
    @Mapping(target = "percentageUsed", ignore = true)
    BudgetResponse toResponse(Budget budget);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "budgetMonth", source = "month")
    @Mapping(target = "notificationSent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Budget toEntity(BudgetRequest request);

    default BudgetResponse toResponseWithSpent(Budget budget, BigDecimal spent) {
        var response = toResponse(budget);
        var remaining = budget.getAmount().subtract(spent);
        var percentage = budget.getAmount().compareTo(BigDecimal.ZERO) > 0
            ? spent.multiply(BigDecimal.valueOf(100)).divide(budget.getAmount(), 2, java.math.RoundingMode.HALF_UP)
            .doubleValue()
            : 0.0;

        return BudgetResponse.builder()
            .id(response.getId())
            .categoryId(response.getCategoryId())
            .categoryName(response.getCategoryName())
            .categoryIcon(response.getCategoryIcon())
            .categoryColor(response.getCategoryColor())
            .month(response.getMonth())
            .amount(response.getAmount())
            .spent(spent)
            .remaining(remaining)
            .percentageUsed(percentage)
            .createdAt(response.getCreatedAt())
            .build();
    }
}
