package com.code44.finance.ui.accounts;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.db.model.Account;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.ui.ModelEditFragment;

public class AccountEditFragment extends ModelEditFragment<Account> {
    private EditText title_ET;
    private Button currency_B;
    private Button amount_B;
    private EditText note_ET;

    public static AccountEditFragment newInstance(long accountId) {
        final Bundle args = makeArgs(accountId);

        final AccountEditFragment fragment = new AccountEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        title_ET = (EditText) view.findViewById(R.id.title_ET);
        currency_B = (Button) view.findViewById(R.id.currency_B);
        amount_B = (Button) view.findViewById(R.id.amount_B);
        note_ET = (EditText) view.findViewById(R.id.note_ET);
    }

    @Override
    public boolean onSave() {
        return false;
    }

    @Override
    protected void ensureModelUpdated(Account model) {
        model.setTitle(title_ET.getText().toString());
        model.setNote(note_ET.getText().toString());
    }

    @Override
    protected Uri getUri(long modelId) {
        return AccountsProvider.uriAccount(modelId);
    }

    @Override
    protected Account getModelFrom(Cursor cursor) {
        return Account.from(cursor);
    }

    @Override
    protected void onModelLoaded(Account model) {

    }
}
