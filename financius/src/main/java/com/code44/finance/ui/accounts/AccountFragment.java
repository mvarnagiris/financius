package com.code44.finance.ui.accounts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.ui.ModelFragment;
import com.code44.finance.utils.MoneyFormatter;

import javax.inject.Inject;

public class AccountFragment extends ModelFragment<Account> {
    @Inject @Main Currency mainCurrency;

    private TextView title_TV;
    private TextView balance_TV;
    private TextView mainCurrencyBalance_TV;
    private TextView note_TV;

    public static AccountFragment newInstance(String accountServerId) {
        final Bundle args = makeArgs(accountServerId);

        final AccountFragment fragment = new AccountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        title_TV = (TextView) view.findViewById(R.id.titleTextView);
        balance_TV = (TextView) view.findViewById(R.id.balance_TV);
        mainCurrencyBalance_TV = (TextView) view.findViewById(R.id.mainCurrencyBalance_TV);
        note_TV = (TextView) view.findViewById(R.id.note_TV);
    }

    @Override
    protected CursorLoader getModelCursorLoader(Context context, String modelServerId) {
        return Tables.Accounts.getQuery().asCursorLoader(context, AccountsProvider.uriAccount(modelServerId));
    }

    @Override
    protected Account getModelFrom(Cursor cursor) {
        return Account.from(cursor);
    }

    @Override
    protected void onModelLoaded(Account model) {
        title_TV.setText(model.getTitle());
        balance_TV.setText(MoneyFormatter.format(model.getCurrency(), model.getBalance()));
        note_TV.setText(model.getNote());
        if (model.getCurrency() == null || model.getCurrency().getId().equals(mainCurrency.getId())) {
            mainCurrencyBalance_TV.setVisibility(View.GONE);
        } else {
            mainCurrencyBalance_TV.setVisibility(View.VISIBLE);
            mainCurrencyBalance_TV.setText(MoneyFormatter.format(mainCurrency, (long) (model.getBalance() * model.getCurrency().getExchangeRate())));
        }
    }

    @Override
    protected Uri getDeleteUri() {
        return AccountsProvider.uriAccounts();
    }

    @Override
    protected Pair<String, String[]> getDeleteSelection() {
        return Pair.create(Tables.Accounts.ID + "=?", new String[]{modelId});
    }

    @Override
    protected void startModelEdit(Context context, String modelServerId) {
        AccountEditActivity.start(context, modelServerId);
    }
}
