package com.finance.tracker.goal;

import com.finance.tracker.common.CurrentUser;
import com.finance.tracker.goal.dto.GoalRequest;
import com.finance.tracker.goal.dto.GoalResponse;
import com.finance.tracker.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    public ResponseEntity<List<GoalResponse>> getGoals(@CurrentUser User user) {
        return ResponseEntity.ok(goalService.getUserGoals(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoal(@CurrentUser User user, @PathVariable UUID id) {
        return ResponseEntity.ok(goalService.getGoal(user.getId(), id));
    }

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@CurrentUser User user, @Valid @RequestBody GoalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.createGoal(user.getId(), request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(@CurrentUser User user, @PathVariable UUID id, @Valid @RequestBody GoalRequest request) {
        return ResponseEntity.ok(goalService.updateGoal(user.getId(), id, request));
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<GoalResponse> updateProgress(@CurrentUser User user, @PathVariable UUID id, @RequestBody Map<String, BigDecimal> body) {
        return ResponseEntity.ok(goalService.updateGoalProgress(user.getId(), id, body.get("currentAmount")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@CurrentUser User user, @PathVariable UUID id) {
        goalService.deleteGoal(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
