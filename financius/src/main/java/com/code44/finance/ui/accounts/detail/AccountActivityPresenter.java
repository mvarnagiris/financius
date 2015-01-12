package com.code44.finance.ui.accounts.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.ui.accounts.AccountEditActivity;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.ModelActivityPresenter;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.MoneyFormatter;

class AccountActivityPresenter extends ModelActivityPresenter<Account> {
    private final Currency mainCurrency;

    private TextView titleTextView;
    private TextView balanceTextView;
    private TextView mainCurrencyBalanceTextView;
    private TextView noteTextView;

    protected AccountActivityPresenter(EventBus eventBus, Currency mainCurrency) {
        super(eventBus);
        this.mainCurrency = mainCurrency;
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);

        titleTextView = findView(activity, R.id.titleTextView);
        balanceTextView = findView(activity, R.id.balanceTextView);
        mainCurrencyBalanceTextView = findView(activity, R.id.mainCurrencyBalanceTextView);
        noteTextView = findView(activity, R.id.noteTextView);
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Accounts.getQuery().asCursorLoader(context, AccountsProvider.uriAccount(modelId));
    }

    @Override protected Account getModelFrom(Cursor cursor) {
        return Account.from(cursor);
    }

    @Override protected void onModelLoaded(Account model) {
        titleTextView.setText(model.getTitle());
        balanceTextView.setText(MoneyFormatter.format(model.getCurrency(), model.getBalance()));
        noteTextView.setText(model.getNote());
        if (model.getCurrency() == null || model.getCurrency().getId().equals(mainCurrency.getId())) {
            mainCurrencyBalanceTextView.setVisibility(View.GONE);
        } else {
            mainCurrencyBalanceTextView.setVisibility(View.VISIBLE);
            mainCurrencyBalanceTextView.setText(MoneyFormatter.format(mainCurrency, (long) (model.getBalance() * model.getCurrency().getExchangeRate())));
        }
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        AccountEditActivity.start(context, modelId);
    }

    @Override protected Uri getDeleteUri() {
        return AccountsProvider.uriAccounts();
    }

    @Override protected Pair<String, String[]> getDeleteSelection(String modelId) {
        return Pair.create(Tables.Accounts.ID + "=?", new String[]{modelId});
    }
}
