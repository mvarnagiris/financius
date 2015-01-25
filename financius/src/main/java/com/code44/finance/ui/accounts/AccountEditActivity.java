package com.code44.finance.ui.accounts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.CalculatorActivity;
import com.code44.finance.ui.common.ModelEditActivity;
import com.code44.finance.ui.common.ModelListActivity;
import com.code44.finance.ui.currencies.list.CurrenciesActivity;
import com.code44.finance.utils.analytics.Analytics;

import javax.inject.Inject;

public class AccountEditActivity extends ModelEditActivity<Account> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_CURRENCY = 1;
    private static final int REQUEST_BALANCE = 2;

    @Inject CurrenciesManager currenciesManager;

    private EditText titleEditText;
    private Button currencyButton;
    private Button balanceButton;
    private EditText noteEditText;
    private CheckBox includeInTotalsCheckBox;

    public static void start(Context context, String accountId) {
        startActivity(context, makeIntent(context, AccountEditActivity.class, accountId));
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_account_edit;
    }

    @Override protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        // Get views
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        currencyButton = (Button) findViewById(R.id.currencyButton);
        balanceButton = (Button) findViewById(R.id.balanceButton);
        noteEditText = (EditText) findViewById(R.id.noteEditText);
        includeInTotalsCheckBox = (CheckBox) findViewById(R.id.includeInTotalsCheckBox);

        // Setup
        currencyButton.setOnClickListener(this);
        balanceButton.setOnClickListener(this);
        includeInTotalsCheckBox.setOnCheckedChangeListener(this);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CURRENCY:
                    ensureModelUpdated(model);
                    model.setCurrencyCode(ModelListActivity.<CurrencyFormat>getModelExtra(data).getCode());
                    onModelLoaded(model);
                    return;

                case REQUEST_BALANCE:
                    model.setBalance(data.getLongExtra(CalculatorActivity.RESULT_EXTRA_RESULT, 0));
                    onModelLoaded(model);
                    return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override protected boolean onSave(Account model) {
        boolean canSave = true;

        if (TextUtils.isEmpty(model.getTitle())) {
            canSave = false;
            // TODO Show error
        }

        if (canSave) {
            DataStore.insert().values(model.asContentValues()).into(this, AccountsProvider.uriAccounts());
        }

        return canSave;
    }

    @Override protected void ensureModelUpdated(Account model) {
        model.setTitle(titleEditText.getText().toString());
        model.setNote(noteEditText.getText().toString());
    }

    @Override protected CursorLoader getModelCursorLoader(String modelId) {
        return Tables.Accounts.getQuery().asCursorLoader(this, AccountsProvider.uriAccount(modelId));
    }

    @Override protected Account getModelFrom(Cursor cursor) {
        final Account account = Account.from(cursor);
        if (account.getCurrencyCode() == null) {
            account.setCurrencyCode(currenciesManager.getMainCurrencyCode());
        }
        return account;
    }

    @Override protected void onModelLoaded(Account model) {
        titleEditText.setText(model.getTitle());
        currencyButton.setText(model.getCurrencyCode());
        balanceButton.setText(currenciesManager.formatMoney(model.getCurrencyCode(), model.getBalance()));
        noteEditText.setText(model.getNote());
        includeInTotalsCheckBox.setChecked(model.includeInTotals());
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.currencyButton:
                CurrenciesActivity.startSelect(this, REQUEST_CURRENCY);
                break;

            case R.id.balanceButton:
                CalculatorActivity.start(this, REQUEST_BALANCE, model.getBalance());
                break;
        }
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        model.setIncludeInTotals(isChecked);
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.AccountEdit;
    }
}
