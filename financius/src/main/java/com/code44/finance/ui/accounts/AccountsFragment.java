package com.code44.finance.ui.accounts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.adapters.AccountsAdapter;
import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.ui.ModelListFragment;

public class AccountsFragment extends ModelListFragment {
    public static AccountsFragment newInstance(int mode) {
        final Bundle args = makeArgs(mode);

        final AccountsFragment fragment = new AccountsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accounts, container, false);
    }

    @Override
    protected void startModelActivity(Context context, View expandFrom, long modelId) {
        // TODO Start
    }

    @Override
    protected void startModelEditActivity(Context context, View expandFrom, long modelId) {
        AccountEditActivity.start(context, expandFrom, modelId);
    }

    @Override
    protected BaseModelsAdapter createAdapter(Context context) {
        return new AccountsAdapter(context);
    }

    @Override
    protected Uri getUri() {
        return AccountsProvider.uriAccounts();
    }

    @Override
    protected BaseModel modelFrom(Cursor cursor) {
        return Account.from(cursor);
    }

    @Override
    protected Query getQuery() {
        return Query.get()
                .projectionId(Tables.Accounts.ID)
                .projection(Tables.Accounts.PROJECTION)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Accounts.OWNER + "<>?")
                .args(Account.Owner.SYSTEM.asString());
    }
}
