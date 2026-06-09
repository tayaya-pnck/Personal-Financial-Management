package com.finance.tracker.account;

import com.finance.tracker.account.dto.AccountRequest;
import com.finance.tracker.account.dto.AccountResponse;
import com.finance.tracker.common.ResourceNotFoundException;
import com.finance.tracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public List<AccountResponse> getUserAccounts(UUID userId) {
        return accountRepository.findByUserIdAndIsArchivedFalseOrderByNameAsc(userId)
            .stream()
            .map(accountMapper::toResponse)
            .toList();
    }

    public AccountResponse getAccount(UUID userId, UUID accountId) {
        var account = findAccount(userId, accountId);
        return accountMapper.toResponse(account);
    }

    @Transactional
    public AccountResponse createAccount(UUID userId, AccountRequest request, User user) {
        var account = accountMapper.toEntity(request);
        account.setUser(user);
        account = accountRepository.save(account);
        return accountMapper.toResponse(account);
    }

    @Transactional
    public AccountResponse updateAccount(UUID userId, UUID accountId, AccountRequest request) {
        var account = findAccount(userId, accountId);
        accountMapper.updateEntity(request, account);
        account = accountRepository.save(account);
        return accountMapper.toResponse(account);
    }

    @Transactional
    public void deleteAccount(UUID userId, UUID accountId) {
        var account = findAccount(userId, accountId);
        account.setArchived(true);
        accountRepository.save(account);
    }

    private Account findAccount(UUID userId, UUID accountId) {
        var account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        if (!account.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Account", accountId);
        }
        return account;
    }
}
