package com.code44.finance.ui.accounts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Pair;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.ui.ModelActivity;
import com.code44.finance.ui.ModelFragment;

public class AccountActivity extends ModelActivity {
    public static void start(Context context, long accountId) {
        final Intent intent = makeIntent(context, AccountActivity.class, accountId);
        start(context, intent);
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.account;
    }

    @Override
    protected ModelFragment createModelFragment(long modelId) {
        return AccountFragment.newInstance(modelId);
    }

    @Override
    protected void startEditActivity(long modelId) {
        AccountEditActivity.start(this, modelId);
    }

    @Override
    protected Uri getDeleteUri() {
        return AccountsProvider.uriAccounts();
    }

    @Override
    protected Pair<String, String[]> getDeleteSelection() {
        return Pair.create(Tables.Accounts.ID + "=?", new String[]{String.valueOf(modelId)});
    }
}
