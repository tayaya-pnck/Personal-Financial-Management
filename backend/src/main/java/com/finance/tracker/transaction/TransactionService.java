package com.finance.tracker.transaction;

import com.finance.tracker.account.AccountRepository;
import com.finance.tracker.category.CategoryRepository;
import com.finance.tracker.common.PageResponse;
import com.finance.tracker.common.ResourceNotFoundException;
import com.finance.tracker.transaction.dto.TransactionRequest;
import com.finance.tracker.transaction.dto.TransactionResponse;
import com.finance.tracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public PageResponse<TransactionResponse> getUserTransactions(UUID userId, Pageable pageable) {
        var page = transactionRepository.findByUserIdOrderByDateDescCreatedAtDesc(userId, pageable);
        var content = page.getContent().stream()
            .map(transactionMapper::toResponse)
            .toList();
        return PageResponse.from(page, content);
    }

    public PageResponse<TransactionResponse> getTransactionsByAccount(UUID userId, UUID accountId, Pageable pageable) {
        var page = transactionRepository.findByUserIdAndAccountIdOrderByDateDesc(userId, accountId, pageable);
        var content = page.getContent().stream()
            .map(transactionMapper::toResponse)
            .toList();
        return PageResponse.from(page, content);
    }

    public TransactionResponse getTransaction(UUID userId, UUID transactionId) {
        return transactionMapper.toResponse(findTransaction(userId, transactionId));
    }

    @Transactional
    public TransactionResponse createTransaction(UUID userId, TransactionRequest request, User user) {
        var transaction = transactionMapper.toEntity(request);
        transaction.setUser(user);

        var account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("Account", request.getAccountId()));
        if (!account.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Account", request.getAccountId());
        }
        transaction.setAccount(account);

        if (request.getCategoryId() != null) {
            categoryRepository.findById(request.getCategoryId())
                .ifPresent(transaction::setCategory);
        }

        updateBalance(account, request.getType(), request.getAmount());
        transaction = transactionRepository.save(transaction);
        return transactionMapper.toResponse(transaction);
    }

    @Transactional
    public void deleteTransaction(UUID userId, UUID transactionId) {
        var transaction = findTransaction(userId, transactionId);
        // Reverse balance change
        var account = transaction.getAccount();
        var reversalAmount = transaction.getAmount().negate();
        updateBalance(account, transaction.getType(), reversalAmount);

        transactionRepository.delete(transaction);
    }

    private void updateBalance(com.finance.tracker.account.Account account, Transaction.TransactionType type, BigDecimal amount) {
        switch (type) {
            case INCOME -> account.setBalance(account.getBalance().add(amount));
            case EXPENSE -> account.setBalance(account.getBalance().subtract(amount));
            case TRANSFER -> {} // Handled by source/dest logic
        }
        accountRepository.save(account);
    }

    private Transaction findTransaction(UUID userId, UUID transactionId) {
        var transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));
        if (!transaction.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Transaction", transactionId);
        }
        return transaction;
    }
}
