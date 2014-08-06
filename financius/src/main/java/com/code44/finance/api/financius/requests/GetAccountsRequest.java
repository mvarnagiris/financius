package com.code44.finance.api.financius.requests;

import android.content.ContentValues;
import android.content.Context;

import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.accounts.model.AccountEntity;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.providers.AccountsProvider;

import java.util.ArrayList;
import java.util.List;

public class GetAccountsRequest extends FinanciusBaseRequest<Void> {
    public GetAccountsRequest(Context context, User user) {
        super(null, context, user);
    }

    @Override
    protected Void performRequest() throws Exception {
        long accountsTimestamp = user.getAccountsTimestamp();
        final List<AccountEntity> accountEntities = getAccountsService().list(accountsTimestamp).execute().getItems();
        final List<ContentValues> accountValues = new ArrayList<>();

        for (AccountEntity entity : accountEntities) {
            accountValues.add(Account.from(entity).asContentValues());
            if (accountsTimestamp < entity.getEditTs()) {
                accountsTimestamp = entity.getEditTs();
            }
        }

        DataStore.bulkInsert().values(accountValues).into(AccountsProvider.uriAccounts());
        user.setAccountsTimestamp(accountsTimestamp);
        return null;
    }
}
