package com.code44.finance.ui.transactions;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.code44.finance.R;
import com.code44.finance.common.utils.StringUtils;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.ui.CalculatorActivity;
import com.code44.finance.ui.ModelEditFragment;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.accounts.AccountsActivity;
import com.code44.finance.ui.categories.CategoriesActivity;
import com.code44.finance.utils.MoneyFormatter;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

public class TransactionEditFragment extends ModelEditFragment<Transaction> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_AMOUNT = 1;
    private static final int REQUEST_ACCOUNT_FROM = 2;
    private static final int REQUEST_ACCOUNT_TO = 3;
    private static final int REQUEST_CATEGORY = 4;
    private static final int REQUEST_DATE = 5;

    private ImageButton categoryType_IB;
    private Button amount_B;
    private Button accountFrom_B;
    private Button accountTo_B;
    private ImageView color_IV;
    private Button category_B;
    private Button date_B;
    private EditText note_ET;

    public static TransactionEditFragment newInstance(String transactionServerId) {
        final Bundle args = makeArgs(transactionServerId);

        final TransactionEditFragment fragment = new TransactionEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_edit, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        categoryType_IB = (ImageButton) view.findViewById(R.id.categoryType_IB);
        amount_B = (Button) view.findViewById(R.id.amount_B);
        accountFrom_B = (Button) view.findViewById(R.id.accountFrom_B);
        accountTo_B = (Button) view.findViewById(R.id.accountTo_B);
        color_IV = (ImageView) view.findViewById(R.id.color_IV);
        category_B = (Button) view.findViewById(R.id.category_B);
        date_B = (Button) view.findViewById(R.id.date_B);
        note_ET = (EditText) view.findViewById(R.id.note_ET);

        // Setup
        categoryType_IB.setOnClickListener(this);
        amount_B.setOnClickListener(this);
        accountFrom_B.setOnClickListener(this);
        accountTo_B.setOnClickListener(this);
        category_B.setOnClickListener(this);
        date_B.setOnClickListener(this);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_AMOUNT:
                    model.setAmount(data.getLongExtra(CalculatorActivity.RESULT_EXTRA_RESULT, 0));
                    onModelLoaded(model);
                    return;
                case REQUEST_ACCOUNT_FROM:
                    model.setAccountFrom(data.<Account>getParcelableExtra(ModelListActivity.RESULT_EXTRA_MODEL));
                    onModelLoaded(model);
                    return;
                case REQUEST_ACCOUNT_TO:
                    model.setAccountTo(data.<Account>getParcelableExtra(ModelListActivity.RESULT_EXTRA_MODEL));
                    onModelLoaded(model);
                    return;
                case REQUEST_CATEGORY:
                    model.setCategory(data.<Category>getParcelableExtra(ModelListActivity.RESULT_EXTRA_MODEL));
                    onModelLoaded(model);
                    return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override public boolean onSave(Context context, Transaction model) {
        boolean canSave = true;

//        if (TextUtils.isEmpty(model.getTitle())) {
//            canSave = false;
//            // TODO Show error
//        }

        if (canSave) {
            DataStore.insert().values(model.asContentValues()).into(context, TransactionsProvider.uriTransactions());
        }

        return canSave;
    }

    @Override protected void ensureModelUpdated(Transaction model) {
        model.setNote(note_ET.getText().toString());
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelServerId) {
        return Tables.Transactions.getQuery().asCursorLoader(context, TransactionsProvider.uriTransaction(modelServerId));
    }

    @Override protected Transaction getModelFrom(Cursor cursor) {
        final Transaction transaction = Transaction.from(cursor);
        if (StringUtils.isEmpty(transaction.getServerId())) {
            // TODO Creating new transaction. Kick off auto-complete.
        }
        return transaction;
    }

    @Override protected void onModelLoaded(Transaction model) {
        switch (model.getCategory().getCategoryType()) {
            case EXPENSE:
                accountFrom_B.setVisibility(View.VISIBLE);
                accountTo_B.setVisibility(View.GONE);
                color_IV.setVisibility(View.VISIBLE);
                category_B.setVisibility(View.VISIBLE);
                categoryType_IB.setImageResource(R.drawable.ic_category_type_expense);
                break;
            case INCOME:
                accountFrom_B.setVisibility(View.GONE);
                accountTo_B.setVisibility(View.VISIBLE);
                color_IV.setVisibility(View.VISIBLE);
                category_B.setVisibility(View.VISIBLE);
                categoryType_IB.setImageResource(R.drawable.ic_category_type_income);
                break;
            case TRANSFER:
                accountFrom_B.setVisibility(View.VISIBLE);
                accountTo_B.setVisibility(View.VISIBLE);
                color_IV.setVisibility(View.GONE);
                category_B.setVisibility(View.GONE);
                categoryType_IB.setImageResource(R.drawable.ic_category_type_transfer);
                break;
        }

        amount_B.setText(MoneyFormatter.format(getAmountCurrency(model), model.getAmount()));
        accountFrom_B.setText(model.getAccountFrom().getTitle());
        accountTo_B.setText(model.getAccountTo().getTitle());
        color_IV.setColorFilter(model.getCategory().getColor());
        category_B.setText(model.getCategory().getTitle());
        date_B.setText(DateUtils.formatDateTime(getActivity(), new DateTime(model.getDate()), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.categoryType_IB:
                toggleCategoryType();
                break;
            case R.id.amount_B:
                CalculatorActivity.start(this, REQUEST_AMOUNT, model.getAmount());
                break;
            case R.id.accountFrom_B:
                AccountsActivity.startSelect(this, REQUEST_ACCOUNT_FROM);
                break;
            case R.id.accountTo_B:
                AccountsActivity.startSelect(this, REQUEST_ACCOUNT_TO);
                break;
            case R.id.category_B:
                CategoriesActivity.startSelect(this, REQUEST_CATEGORY, model.getCategory().getCategoryType());
                break;
        }
    }

    @Override public void onCheckedChanged(CompoundButton view, boolean checked) {
        //model.setIncludeInTotals(checked);
    }

    private void toggleCategoryType() {
        switch (model.getCategory().getCategoryType()) {
            case EXPENSE:
                model.setCategory(Category.getIncome());
                break;
            case INCOME:
                model.setCategory(Category.getTransfer());
                break;
            case TRANSFER:
                model.setCategory(Category.getExpense());
                break;
        }
        onModelLoaded(model);
    }

    private Currency getAmountCurrency(Transaction transaction) {
        Currency transactionCurrency;
        switch (transaction.getCategory().getCategoryType()) {
            case EXPENSE:
                transactionCurrency = transaction.getAccountFrom().getCurrency();
                break;
            case INCOME:
                transactionCurrency = transaction.getAccountTo().getCurrency();
                break;
            case TRANSFER:
                transactionCurrency = transaction.getAccountFrom().getCurrency();
                break;
            default:
                throw new IllegalStateException("Category type " + transaction.getCategory().getCategoryType() + " is not supported.");
        }

        if (transactionCurrency == null || StringUtils.isEmpty(transactionCurrency.getServerId())) {
            transactionCurrency = Currency.getDefault();
        }

        return transactionCurrency;
    }
}
