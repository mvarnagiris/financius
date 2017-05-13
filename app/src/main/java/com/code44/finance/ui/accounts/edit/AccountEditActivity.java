package com.code44.finance.ui.accounts.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.common.activities.CalculatorActivity;
import com.code44.finance.ui.common.activities.ModelEditActivity;
import com.code44.finance.ui.currencies.list.CurrenciesActivity;
import com.code44.finance.utils.analytics.Screens;

import javax.inject.Inject;

public class AccountEditActivity extends ModelEditActivity<Account, AccountEditData> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_CURRENCY = 1;
    private static final int REQUEST_BALANCE = 2;

    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    private EditText titleEditText;
    private Button currencyButton;
    private Button balanceButton;
    private EditText noteEditText;
    private CheckBox includeInTotalsCheckBox;

    public static void start(Context context, String accountId) {
        makeActivityStarter(context, AccountEditActivity.class, accountId).start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        // Get views
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        currencyButton = (Button) findViewById(R.id.currencyButton);
        balanceButton = (Button) findViewById(R.id.balanceButton);
        noteEditText = (EditText) findViewById(R.id.noteEditText);
        includeInTotalsCheckBox = (CheckBox) findViewById(R.id.includeInTotalsCheckBox);

        // Setup
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override public void afterTextChanged(Editable s) {
                getModelEditData().setTitle(titleEditText.getText().toString());
            }
        });
        noteEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override public void afterTextChanged(Editable s) {
                getModelEditData().setNote(noteEditText.getText().toString());
            }
        });
        currencyButton.setOnClickListener(this);
        balanceButton.setOnClickListener(this);
        includeInTotalsCheckBox.setOnCheckedChangeListener(this);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CURRENCY:
                    getModelEditData().setCurrencyCode(((CurrencyFormat) CurrenciesActivity.getModelExtra(data)).getCode());
                    onDataChanged(getModelEditData());
                    break;

                case REQUEST_BALANCE:
                    getModelEditData().setBalance(CalculatorActivity.getResultValue(data));
                    onDataChanged(getModelEditData());
                    break;
            }
        }
    }

    @NonNull @Override protected AccountEditData createModelEditData() {
        final AccountEditData accountEditData = new AccountEditData();
        accountEditData.setCurrencyCode(currenciesManager.getMainCurrencyCode());
        return accountEditData;
    }

    @NonNull @Override protected ModelEditValidator<AccountEditData> createModelEditValidator() {
        return new AccountEditValidator(titleEditText, currencyButton);
    }

    @Override protected void onDataChanged(@NonNull AccountEditData modelEditData) {
        titleEditText.setText(modelEditData.getTitle());
        currencyButton.setText(modelEditData.getCurrencyCode());
        balanceButton.setText(amountFormatter.format(modelEditData.getCurrencyCode(), modelEditData.getBalance()));
        noteEditText.setText(modelEditData.getNote());
        includeInTotalsCheckBox.setChecked(modelEditData.isIncludeInTotals());
    }

    @NonNull @Override protected Uri getSaveUri() {
        return AccountsProvider.uriAccounts();
    }

    @NonNull @Override protected CursorLoader getModelCursorLoader(@NonNull String modelId) {
        return Tables.Accounts.getQuery().asCursorLoader(this, AccountsProvider.uriAccount(modelId));
    }

    @NonNull @Override protected Account getModelFrom(@NonNull Cursor cursor) {
        final Account account = Account.from(cursor);
        if (account.getCurrencyCode() == null) {
            account.setCurrencyCode(currenciesManager.getMainCurrencyCode());
        }
        return account;
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.currencyButton:
                CurrenciesActivity.startSelect(this, REQUEST_CURRENCY);
                break;

            case R.id.balanceButton:
                CalculatorActivity.start(this, REQUEST_BALANCE, getModelEditData().getBalance());
                break;
        }
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        getModelEditData().setIncludeInTotals(isChecked);
        onDataChanged(getModelEditData());
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.AccountEdit;
    }
}
