package com.finance.tracker.budget;

import com.finance.tracker.budget.dto.BudgetRequest;
import com.finance.tracker.budget.dto.BudgetResponse;
import com.finance.tracker.common.CurrentUser;
import com.finance.tracker.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets(
            @CurrentUser User user,
            @RequestParam(required = false) String month) {
        var budgetMonth = month != null ? month : java.time.LocalDate.now().toString().substring(0, 7);
        return ResponseEntity.ok(budgetService.getUserBudgets(user.getId(), budgetMonth));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudget(@CurrentUser User user, @PathVariable UUID id) {
        return ResponseEntity.ok(budgetService.getBudget(user.getId(), id));
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@CurrentUser User user, @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.createBudget(user.getId(), request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(@CurrentUser User user, @PathVariable UUID id, @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.updateBudget(user.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@CurrentUser User user, @PathVariable UUID id) {
        budgetService.deleteBudget(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
