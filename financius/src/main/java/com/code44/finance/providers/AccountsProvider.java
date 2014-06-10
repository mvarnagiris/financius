package com.code44.finance.providers;

import android.net.Uri;

import com.code44.finance.db.Tables;

public class AccountsProvider extends BaseModelProvider {
    public static Uri uriAccounts() {
        return uriModels(AccountsProvider.class, Tables.Accounts.TABLE_NAME);
    }

    public static Uri uriAccount(long accountId) {
        return uriModel(AccountsProvider.class, Tables.Accounts.TABLE_NAME, accountId);
    }

    @Override
    protected String getModelTable() {
        return Tables.Accounts.TABLE_NAME;
    }

    @Override
    protected String getQueryTables() {
        return getModelTable() + " inner join " + Tables.Currencies.TABLE_NAME + " on " + Tables.Currencies.ID.getNameWithTable() + "=" + Tables.Accounts.CURRENCY_ID.getName();
    }
}
