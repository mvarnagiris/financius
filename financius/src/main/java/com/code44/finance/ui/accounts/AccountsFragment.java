package com.code44.finance.ui.accounts;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.adapters.AccountsAdapter;
import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.BaseModel;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.utils.MoneyFormatter;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AccountsFragment extends ModelListFragment {
    @Inject @Main Currency mainCurrency;

    @InjectView(R.id.container_V) protected View container_V;
    @InjectView(R.id.balance_TV) protected TextView balance_TV;

    public static AccountsFragment newInstance(Mode mode) {
        final Bundle args = makeArgs(mode, null);

        final AccountsFragment fragment = new AccountsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accounts, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override protected void setListShown(boolean shown) {
        super.setListShown(shown);

        if (shown) {
            container_V.setVisibility(View.VISIBLE);
        } else {
            container_V.setVisibility(View.GONE);
        }
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODELS) {
            updateBalance(data);
        }
        super.onLoadFinished(loader, data);
    }

    @OnClick(R.id.create_B) protected void onCreateButtonClicked() {
        startModelEdit(getActivity(), null);
    }

    @Override protected BaseModelsAdapter createAdapter(Context context) {
        return new AccountsAdapter(context, mainCurrency);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Accounts.getQuery().asCursorLoader(context, AccountsProvider.uriAccounts());
    }

    @Override protected BaseModel modelFrom(Cursor cursor) {
        return Account.from(cursor);
    }

    @Override protected void onModelClick(Context context, View view, int position, String modelServerId, BaseModel model) {
        AccountActivity.start(context, modelServerId);
    }

    @Override protected void startModelEdit(Context context, String modelServerId) {
        AccountEditActivity.start(context, modelServerId);
    }

    private void updateBalance(Cursor cursor) {
        long balance = 0;
        if (cursor.moveToFirst()) {
            do {
                final Account account = Account.from(cursor);
                if (account.includeInTotals()) {
                    balance += account.getBalance() * account.getCurrency().getExchangeRate();
                }
            } while (cursor.moveToNext());
        }
        balance_TV.setText(MoneyFormatter.format(mainCurrency, balance));
    }
}
