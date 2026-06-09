package com.finance.tracker.account;

import com.finance.tracker.account.dto.AccountRequest;
import com.finance.tracker.account.dto.AccountResponse;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAccounts(@CurrentUser User user) {
        return ResponseEntity.ok(accountService.getUserAccounts(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@CurrentUser User user, @PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccount(user.getId(), id));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@CurrentUser User user, @Valid @RequestBody AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(user.getId(), request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(@CurrentUser User user, @PathVariable UUID id, @Valid @RequestBody AccountRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(user.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@CurrentUser User user, @PathVariable UUID id) {
        accountService.deleteAccount(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
