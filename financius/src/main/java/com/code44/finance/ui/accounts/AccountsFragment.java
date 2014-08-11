package com.code44.finance.ui.accounts;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.adapters.AccountsAdapter;
import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.utils.MoneyFormatter;

public class AccountsFragment extends ModelListFragment {
    private TextView balance_TV;

    public static AccountsFragment newInstance(Mode mode) {
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        balance_TV = (TextView) view.findViewById(R.id.balance_TV);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODELS) {
            updateBalance(data);
        }
        super.onLoadFinished(loader, data);
    }

    @Override
    protected BaseModelsAdapter createAdapter(Context context) {
        return new AccountsAdapter(context);
    }

    @Override
    protected CursorLoader getModelsCursorLoader() {
        return Tables.Accounts.getQuery().asCursorLoader(AccountsProvider.uriAccounts());
    }

    @Override
    protected BaseModel modelFrom(Cursor cursor) {
        return Account.from(cursor);
    }

    @Override
    protected void onModelClick(Context context, View view, int position, String modelServerId, BaseModel model) {
        AccountActivity.start(context, modelServerId);
    }

    @Override
    protected void startModelEdit(Context context, String modelServerId) {
        AccountEditActivity.start(context, modelServerId);
    }

    private void updateBalance(Cursor cursor) {
        long balance = 0;
        if (cursor.moveToFirst()) {
            do {
                final Account account = Account.from(cursor);
                balance += account.getBalance() * account.getCurrency().getExchangeRate();
            } while (cursor.moveToNext());
        }
        balance_TV.setText(MoneyFormatter.format(Currency.getDefault(), balance));
    }
}
