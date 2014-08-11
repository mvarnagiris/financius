package com.code44.finance.api.requests;

import com.code44.finance.backend.endpoint.accounts.Accounts;
import com.code44.finance.backend.endpoint.accounts.model.AccountEntity;
import com.code44.finance.backend.endpoint.accounts.model.AccountsBody;
import com.code44.finance.data.db.model.Account;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PostAccountsRequest extends PostRequest<AccountsBody> {
    private final List<Account> accounts;
    @Inject Accounts accountsService;

    public PostAccountsRequest(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override protected AccountsBody createBody() {
        return new AccountsBody();
    }

    @Override protected void onAddPostData(AccountsBody body) {
        final List<AccountEntity> accountEntities = new ArrayList<>();
        for (Account account : accounts) {
            accountEntities.add(account.toEntity());
        }
        body.setAccounts(accountEntities);
    }

    @Override protected void performRequest(AccountsBody body) throws Exception {
        accountsService.save(body);
    }
}
