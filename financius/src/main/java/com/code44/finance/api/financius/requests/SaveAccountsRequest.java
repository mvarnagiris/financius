package com.code44.finance.api.financius.requests;

import android.content.Context;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.accounts.model.AccountEntity;
import com.code44.finance.backend.endpoint.accounts.model.AccountsBody;
import com.code44.finance.data.db.model.Account;

import java.util.ArrayList;
import java.util.List;

public class SaveAccountsRequest extends FinanciusBaseRequest<Void> {
    private final AccountsBody body;

    public SaveAccountsRequest(Context context, User user, List<Account> accounts) {
        super(null, context, user);
        body = preparePostBody(accounts);
    }

    @Override
    protected Void performRequest() throws Exception {
        getAccountsService().save(body).execute();
        return null;
    }

    private AccountsBody preparePostBody(List<Account> accounts) {
        final List<AccountEntity> accountEntities = new ArrayList<>();
        for (Account account : accounts) {
            accountEntities.add(account.toEntity());
        }

        final AccountsBody body = new AccountsBody();
        body.setAccounts(accountEntities);
        body.setDeviceRegId(GcmRegistration.get().getRegistrationId());

        return body;
    }
}
