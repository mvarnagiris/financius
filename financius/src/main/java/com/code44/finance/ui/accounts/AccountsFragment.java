package com.code44.finance.ui.accounts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.adapters.AccountsAdapter;
import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.common.model.AccountOwner;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.utils.MoneyFormatter;

public class AccountsFragment extends ModelListFragment {
    private TextView balance_TV;

    public static AccountsFragment newInstance(ModelListActivity.Mode mode) {
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
    protected Uri getUri() {
        return AccountsProvider.uriAccounts();
    }

    @Override
    protected BaseModel modelFrom(Cursor cursor) {
        return Account.from(cursor);
    }

    @Override
    protected Query getQuery() {
        return Query.create()
                .projectionId(Tables.Accounts.ID)
                .projection(Tables.Accounts.PROJECTION)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Accounts.MODEL_STATE + "=?", ModelState.NORMAL.asString())
                .selection(" and " + Tables.Accounts.OWNER + "<>?", AccountOwner.SYSTEM.asString());
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
