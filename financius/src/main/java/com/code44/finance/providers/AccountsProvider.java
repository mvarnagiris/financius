package com.code44.finance.providers;

import android.content.ContentUris;
import android.net.Uri;

import com.code44.finance.db.model.Account;

public class AccountsProvider extends BaseModelProvider<Account> {
    public static Uri uriAccounts() {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(AccountsProvider.class) + "/" + Account.class.getSimpleName());
    }

    public static Uri uriAccount(long accountId) {
        return ContentUris.withAppendedId(uriAccounts(), accountId);
    }

    @Override
    protected Class<Account> getModelClass() {
        return Account.class;
    }
}
