package com.code44.finance.ui.accounts;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.ui.ModelFragment;

public class AccountFragment extends ModelFragment<Account> {
    public static AccountFragment newInstance(long accountId) {
        final Bundle args = makeArgs(accountId);

        final AccountFragment fragment = new AccountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected Uri getUri(long modelId) {
        return AccountsProvider.uriAccount(modelId);
    }

    @Override
    protected Account getModelFrom(Cursor cursor) {
        return Account.from(cursor);
    }

    @Override
    protected void onModelLoaded(Account model) {

    }
}
