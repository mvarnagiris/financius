package com.code44.finance.ui.accounts;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.ui.CalculatorActivity;
import com.code44.finance.ui.ModelEditFragment;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.currencies.CurrenciesActivity;
import com.code44.finance.utils.MoneyFormatter;

public class AccountEditFragment extends ModelEditFragment<Account> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_CURRENCY = 1;
    private static final int REQUEST_BALANCE = 2;

    private EditText title_ET;
    private Button currency_B;
    private Button balance_B;
    private EditText note_ET;
    private CheckBox includeInTotals_CB;

    public static AccountEditFragment newInstance(String accountServerId) {
        final Bundle args = makeArgs(accountServerId);

        final AccountEditFragment fragment = new AccountEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_edit, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        title_ET = (EditText) view.findViewById(R.id.title_ET);
        currency_B = (Button) view.findViewById(R.id.currency_B);
        balance_B = (Button) view.findViewById(R.id.balance_B);
        note_ET = (EditText) view.findViewById(R.id.note_ET);
        includeInTotals_CB = (CheckBox) view.findViewById(R.id.includeInTotals_CB);

        // Setup
        currency_B.setOnClickListener(this);
        balance_B.setOnClickListener(this);
        includeInTotals_CB.setOnCheckedChangeListener(this);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CURRENCY:
                    ensureModelUpdated(model);
                    model.setCurrency(data.<Currency>getParcelableExtra(ModelListActivity.RESULT_EXTRA_MODEL));
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

    @Override public boolean onSave(Context context, Account model) {
        boolean canSave = true;

        if (TextUtils.isEmpty(model.getTitle())) {
            canSave = false;
            // TODO Show error
        }

        if (canSave) {
            DataStore.insert().values(model.asValues()).into(context, AccountsProvider.uriAccounts());
        }

        return canSave;
    }

    @Override protected void ensureModelUpdated(Account model) {
        model.setTitle(title_ET.getText().toString());
        model.setNote(note_ET.getText().toString());
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelServerId) {
        return Tables.Accounts.getQuery().asCursorLoader(context, AccountsProvider.uriAccount(modelServerId));
    }

    @Override protected Account getModelFrom(Cursor cursor) {
        return Account.from(cursor);
    }

    @Override protected void onModelLoaded(Account model) {
        title_ET.setText(model.getTitle());
        currency_B.setText(model.getCurrency().getCode());
        balance_B.setText(MoneyFormatter.format(model.getCurrency(), model.getBalance()));
        note_ET.setText(model.getNote());
        includeInTotals_CB.setChecked(model.includeInTotals());
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.currency_B:
                CurrenciesActivity.startSelect(this, REQUEST_CURRENCY);
                break;

            case R.id.balance_B:
                CalculatorActivity.start(this, REQUEST_BALANCE, model.getBalance());
                break;
        }
    }

    @Override public void onCheckedChanged(CompoundButton view, boolean checked) {
        model.setIncludeInTotals(checked);
    }
}
