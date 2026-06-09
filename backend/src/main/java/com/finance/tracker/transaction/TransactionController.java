package com.finance.tracker.transaction;

import com.finance.tracker.common.CurrentUser;
import com.finance.tracker.common.PageResponse;
import com.finance.tracker.transaction.dto.TransactionRequest;
import com.finance.tracker.transaction.dto.TransactionResponse;
import com.finance.tracker.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponse>> getTransactions(
            @CurrentUser User user,
            @PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(transactionService.getUserTransactions(user.getId(), pageable));
    }

    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<PageResponse<TransactionResponse>> getTransactionsByAccount(
            @CurrentUser User user,
            @PathVariable UUID accountId,
            @PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccount(user.getId(), accountId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@CurrentUser User user, @PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getTransaction(user.getId(), id));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@CurrentUser User user, @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(user.getId(), request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@CurrentUser User user, @PathVariable UUID id) {
        transactionService.deleteTransaction(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
