package com.finance.tracker.goal;

import com.finance.tracker.goal.dto.GoalRequest;
import com.finance.tracker.goal.dto.GoalResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target = "percentageComplete", expression = "java(calculatePercentage(goal.getCurrentAmount(), goal.getTargetAmount()))")
    @Mapping(target = "daysRemaining", expression = "java(calculateDaysRemaining(goal.getDeadline()))")
    GoalResponse toResponse(Goal goal);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "achieved", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Goal toEntity(GoalRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "achieved", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(GoalRequest request, @MappingTarget Goal goal);

    default double calculatePercentage(BigDecimal current, BigDecimal target) {
        if (target.compareTo(BigDecimal.ZERO) <= 0) return 0.0;
        return current.multiply(BigDecimal.valueOf(100))
            .divide(target, 2, java.math.RoundingMode.HALF_UP)
            .doubleValue();
    }

    default long calculateDaysRemaining(LocalDate deadline) {
        if (deadline == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), deadline);
    }
}
