package com.code44.finance.api.requests;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.backend.endpoint.accounts.Accounts;
import com.code44.finance.backend.endpoint.accounts.model.AccountEntity;
import com.code44.finance.backend.endpoint.accounts.model.AccountsBody;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.model.Account;

import java.util.ArrayList;
import java.util.List;

public class PostAccountsRequest extends PostRequest<AccountsBody> {
    private final Accounts accountsService;
    private final List<Account> accounts;

    public PostAccountsRequest(GcmRegistration gcmRegistration, Accounts accountsService, List<Account> accounts) {
        super(null, gcmRegistration);
        Preconditions.notNull(accountsService, "Accounts service cannot be null.");
        Preconditions.notNull(accounts, "Accounts list cannot be null.");

        this.accountsService = accountsService;
        this.accounts = accounts;
    }

    @Override protected AccountsBody createBody() {
        return new AccountsBody();
    }

    @Override protected void onAddPostData(AccountsBody body) {
        final List<AccountEntity> accountEntities = new ArrayList<>();
        for (Account account : accounts) {
            accountEntities.add(account.asEntity());
        }
        body.setAccounts(accountEntities);
    }

    @Override protected boolean isPostDataEmpty(AccountsBody body) {
        return body.getAccounts().isEmpty();
    }

    @Override protected void performRequest(AccountsBody body) throws Exception {
        accountsService.save(body);
    }
}
