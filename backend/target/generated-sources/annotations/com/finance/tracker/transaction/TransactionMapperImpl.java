package com.finance.tracker.transaction;

import com.finance.tracker.account.Account;
import com.finance.tracker.category.Category;
import com.finance.tracker.transaction.dto.TransactionRequest;
import com.finance.tracker.transaction.dto.TransactionResponse;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-09T16:17:48+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Ubuntu)"
)
@Component
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public TransactionResponse toResponse(Transaction transaction) {
        if ( transaction == null ) {
            return null;
        }

        TransactionResponse.TransactionResponseBuilder transactionResponse = TransactionResponse.builder();

        transactionResponse.accountId( transactionAccountId( transaction ) );
        transactionResponse.accountName( transactionAccountName( transaction ) );
        transactionResponse.categoryId( transactionCategoryId( transaction ) );
        transactionResponse.categoryName( transactionCategoryName( transaction ) );
        transactionResponse.categoryIcon( transactionCategoryIcon( transaction ) );
        transactionResponse.id( transaction.getId() );
        transactionResponse.amount( transaction.getAmount() );
        transactionResponse.description( transaction.getDescription() );
        transactionResponse.date( transaction.getDate() );
        transactionResponse.type( transaction.getType() );
        transactionResponse.createdAt( transaction.getCreatedAt() );

        return transactionResponse.build();
    }

    @Override
    public Transaction toEntity(TransactionRequest request) {
        if ( request == null ) {
            return null;
        }

        Transaction transaction = new Transaction();

        transaction.setAmount( request.getAmount() );
        transaction.setDescription( request.getDescription() );
        transaction.setDate( request.getDate() );
        transaction.setType( request.getType() );
        transaction.setRecurring( request.isRecurring() );
        transaction.setRecurrenceRule( request.getRecurrenceRule() );

        return transaction;
    }

    private UUID transactionAccountId(Transaction transaction) {
        Account account = transaction.getAccount();
        if ( account == null ) {
            return null;
        }
        return account.getId();
    }

    private String transactionAccountName(Transaction transaction) {
        Account account = transaction.getAccount();
        if ( account == null ) {
            return null;
        }
        return account.getName();
    }

    private UUID transactionCategoryId(Transaction transaction) {
        Category category = transaction.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getId();
    }

    private String transactionCategoryName(Transaction transaction) {
        Category category = transaction.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getName();
    }

    private String transactionCategoryIcon(Transaction transaction) {
        Category category = transaction.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getIcon();
    }
}
