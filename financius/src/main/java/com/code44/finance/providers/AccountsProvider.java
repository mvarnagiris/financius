package com.code44.finance.providers;

import com.code44.finance.db.model.Account;

public class AccountsProvider extends BaseModelProvider<Account> {
    @Override
    protected Class<Account> getModelClass() {
        return Account.class;
    }
}
