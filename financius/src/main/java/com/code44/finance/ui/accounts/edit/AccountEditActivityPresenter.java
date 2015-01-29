package com.code44.finance.ui.accounts.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.CalculatorActivity;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.ModelEditActivityPresenter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.ui.currencies.list.CurrenciesActivity;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.ThemeUtils;

class AccountEditActivityPresenter extends ModelEditActivityPresenter<Account> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_CURRENCY = 1;
    private static final int REQUEST_BALANCE = 2;

    private static final String STATE_TITLE = "STATE_TITLE";
    private static final String STATE_CURRENCY_CODE = "STATE_CURRENCY_CODE";
    private static final String STATE_BALANCE = "STATE_BALANCE";
    private static final String STATE_NOTE = "STATE_NOTE";
    private static final String STATE_INCLUDE_IN_REPORTS = "STATE_INCLUDE_IN_REPORTS";

    private final CurrenciesManager currenciesManager;
    private final AmountFormatter amountFormatter;

    private EditText titleEditText;
    private Button currencyButton;
    private Button balanceButton;
    private EditText noteEditText;
    private CheckBox includeInTotalsCheckBox;

    private String title;
    private String currencyCode;
    private Long balance;
    private String note;
    private Boolean includeInTotals;

    public AccountEditActivityPresenter(EventBus eventBus, CurrenciesManager currenciesManager, AmountFormatter amountFormatter) {
        super(eventBus);
        this.currenciesManager = currenciesManager;
        this.amountFormatter = amountFormatter;
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);

        titleEditText = findView(activity, R.id.titleEditText);
        currencyButton = findView(activity, R.id.currencyButton);
        balanceButton = findView(activity, R.id.balanceButton);
        noteEditText = findView(activity, R.id.noteEditText);
        includeInTotalsCheckBox = findView(activity, R.id.includeInTotalsCheckBox);

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override public void afterTextChanged(Editable s) {
                title = titleEditText.getText().toString();
            }
        });
        noteEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override public void afterTextChanged(Editable s) {
                note = noteEditText.getText().toString();
            }
        });
        currencyButton.setOnClickListener(this);
        balanceButton.setOnClickListener(this);
        includeInTotalsCheckBox.setOnCheckedChangeListener(this);

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(STATE_TITLE);
            currencyCode = savedInstanceState.getString(STATE_CURRENCY_CODE);
            balance = savedInstanceState.getLong(STATE_BALANCE);
            if (balance == -1) {
                balance = null;
            }
            note = savedInstanceState.getString(STATE_NOTE);
            final int includeInTotalsValue = savedInstanceState.getInt(STATE_INCLUDE_IN_REPORTS);
            includeInTotals = includeInTotalsValue == -1 ? null : includeInTotalsValue == 1;
            onDataChanged(getStoredModel());
        }
    }

    @Override public void onActivityResult(BaseActivity activity, int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CURRENCY:
                    currencyCode = ((CurrencyFormat) ModelsActivityPresenter.getModelExtra(data)).getCode();
                    onDataChanged(getStoredModel());
                    return;

                case REQUEST_BALANCE:
                    balance = data.getLongExtra(CalculatorActivity.RESULT_EXTRA_RESULT, 0);
                    onDataChanged(getStoredModel());
                    return;
            }
        }
        super.onActivityResult(activity, requestCode, resultCode, data);
    }

    @Override public void onSaveInstanceState(BaseActivity activity, Bundle outState) {
        super.onSaveInstanceState(activity, outState);
        outState.putString(STATE_TITLE, title);
        outState.putString(STATE_CURRENCY_CODE, currencyCode);
        outState.putLong(STATE_BALANCE, balance == null ? -1 : balance);
        outState.putString(STATE_NOTE, note);
        outState.putInt(STATE_INCLUDE_IN_REPORTS, includeInTotals == null ? -1 : includeInTotals ? 1 : 0);
    }

    @Override protected void onDataChanged(Account model) {
        titleEditText.setText(getTitle());
        currencyButton.setText(getCurrencyCode());
        balanceButton.setText(amountFormatter.format(getCurrencyCode(), getBalance()));
        noteEditText.setText(getNote());
        includeInTotalsCheckBox.setChecked(isIncludeInTotals());
    }

    @Override protected boolean onSave() {
        boolean canSave = true;

        final String title = getTitle();
        if (TextUtils.isEmpty(title)) {
            canSave = false;
            titleEditText.setHintTextColor(ThemeUtils.getColor(titleEditText.getContext(), R.attr.colorError));
        }

        final String currencyCode = getCurrencyCode();
        if (TextUtils.isEmpty(currencyCode) || currencyCode.length() != 3) {
            canSave = false;
            currencyButton.setTextColor(ThemeUtils.getColor(titleEditText.getContext(), R.attr.colorError));
        }

        if (canSave) {
            final Account account = new Account();
            account.setId(getId());
            account.setTitle(title);
            account.setCurrencyCode(currencyCode);
            account.setBalance(getBalance());
            account.setNote(getNote());
            account.setIncludeInTotals(isIncludeInTotals());

            DataStore.insert().model(account).into(getActivity(), AccountsProvider.uriAccounts());
        }

        return canSave;
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Accounts.getQuery().asCursorLoader(context, AccountsProvider.uriAccount(modelId));
    }

    @Override protected Account getModelFrom(Cursor cursor) {
        final Account account = Account.from(cursor);
        if (account.getCurrencyCode() == null) {
            account.setCurrencyCode(currenciesManager.getMainCurrencyCode());
        }
        return account;
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.currencyButton:
                CurrenciesActivity.startSelect(getActivity(), REQUEST_CURRENCY);
                break;

            case R.id.balanceButton:
                CalculatorActivity.start(getActivity(), REQUEST_BALANCE, getBalance());
                break;
        }
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        includeInTotals = isChecked;
        onDataChanged(getStoredModel());
    }

    private String getId() {
        return getStoredModel() != null ? getStoredModel().getId() : null;
    }

    private String getTitle() {
        if (title != null) {
            return title;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getTitle();
        }

        return null;
    }

    private String getCurrencyCode() {
        if (currencyCode != null) {
            return currencyCode;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getCurrencyCode();
        }

        return currenciesManager.getMainCurrencyCode();
    }

    private long getBalance() {
        if (balance != null) {
            return balance;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getBalance();
        }

        return 0;
    }

    private String getNote() {
        if (note != null) {
            return note;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getNote();
        }

        return "";
    }

    private boolean isIncludeInTotals() {
        if (includeInTotals != null) {
            return includeInTotals;
        }

        return getStoredModel() == null || getStoredModel().includeInTotals();
    }
}
