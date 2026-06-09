package com.finance.tracker.goal;

import com.finance.tracker.common.ResourceNotFoundException;
import com.finance.tracker.goal.dto.GoalRequest;
import com.finance.tracker.goal.dto.GoalResponse;
import com.finance.tracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    public List<GoalResponse> getUserGoals(UUID userId) {
        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(goalMapper::toResponse)
            .toList();
    }

    public GoalResponse getGoal(UUID userId, UUID goalId) {
        return goalMapper.toResponse(findGoal(userId, goalId));
    }

    @Transactional
    public GoalResponse createGoal(UUID userId, GoalRequest request, User user) {
        var goal = goalMapper.toEntity(request);
        goal.setUser(user);
        goal = goalRepository.save(goal);
        return goalMapper.toResponse(goal);
    }

    @Transactional
    public GoalResponse updateGoal(UUID userId, UUID goalId, GoalRequest request) {
        var goal = findGoal(userId, goalId);
        goalMapper.updateEntity(request, goal);
        goal = goalRepository.save(goal);
        return goalMapper.toResponse(goal);
    }

    @Transactional
    public GoalResponse updateGoalProgress(UUID userId, UUID goalId, java.math.BigDecimal amount) {
        var goal = findGoal(userId, goalId);
        goal.setCurrentAmount(amount);
        if (goal.getTargetAmount() != null && amount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setAchieved(true);
        }
        goal = goalRepository.save(goal);
        return goalMapper.toResponse(goal);
    }

    @Transactional
    public void deleteGoal(UUID userId, UUID goalId) {
        var goal = findGoal(userId, goalId);
        goalRepository.delete(goal);
    }

    private Goal findGoal(UUID userId, UUID goalId) {
        var goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", goalId));
        if (!goal.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Goal", goalId);
        }
        return goal;
    }
}
