package com.code44.finance.ui.accounts;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.adapters.AccountsAdapter;
import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.db.Tables;
import com.code44.finance.db.model.Account;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.utils.Query;

public class AccountsFragment extends ModelListFragment {
    public static AccountsFragment newInstance() {
        final Bundle args = makeArgs();

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
        // TODO Start
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
    protected Query getQuery() {
        return Query.get()
                .appendProjection(Tables.Accounts.ID.getNameWithTable())
                .appendProjection(Tables.Accounts.PROJECTION)
                .appendProjection(Tables.Currencies.PROJECTION)
                .appendSelection(Tables.Accounts.OWNER + "<>?")
                .appendArgs(Account.Owner.SYSTEM.asString())
                .build();
    }
}
