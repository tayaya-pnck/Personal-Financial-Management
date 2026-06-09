package com.finance.tracker.goal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {

    List<Goal> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Goal> findByUserIdAndIsAchievedFalseOrderByDeadlineAsc(UUID userId);
}
