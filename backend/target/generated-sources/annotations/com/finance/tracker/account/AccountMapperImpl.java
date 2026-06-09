package com.finance.tracker.account;

import com.finance.tracker.account.dto.AccountRequest;
import com.finance.tracker.account.dto.AccountResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-09T16:17:48+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Ubuntu)"
)
@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public AccountResponse toResponse(Account account) {
        if ( account == null ) {
            return null;
        }

        AccountResponse.AccountResponseBuilder accountResponse = AccountResponse.builder();

        accountResponse.id( account.getId() );
        accountResponse.name( account.getName() );
        accountResponse.type( account.getType() );
        accountResponse.balance( account.getBalance() );
        accountResponse.currency( account.getCurrency() );
        accountResponse.color( account.getColor() );
        accountResponse.createdAt( account.getCreatedAt() );
        accountResponse.updatedAt( account.getUpdatedAt() );

        return accountResponse.build();
    }

    @Override
    public Account toEntity(AccountRequest request) {
        if ( request == null ) {
            return null;
        }

        Account account = new Account();

        account.setName( request.getName() );
        account.setType( request.getType() );
        account.setBalance( request.getBalance() );
        account.setCurrency( request.getCurrency() );
        account.setColor( request.getColor() );

        return account;
    }

    @Override
    public void updateEntity(AccountRequest request, Account account) {
        if ( request == null ) {
            return;
        }

        account.setName( request.getName() );
        account.setType( request.getType() );
        account.setBalance( request.getBalance() );
        account.setCurrency( request.getCurrency() );
        account.setColor( request.getColor() );
    }
}
