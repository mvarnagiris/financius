package com.code44.finance.ui.transactions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.currencies.ExchangeRateRequest;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.common.utils.StringUtils;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.ui.CalculatorActivity;
import com.code44.finance.ui.ModelEditFragment;
import com.code44.finance.ui.ModelListActivityOld;
import com.code44.finance.ui.accounts.AccountsActivity;
import com.code44.finance.ui.categories.CategoriesActivity;
import com.code44.finance.ui.dialogs.DatePickerDialog;
import com.code44.finance.ui.dialogs.TimePickerDialog;
import com.code44.finance.ui.tags.TagsActivity;
import com.code44.finance.utils.FieldValidationUtils;
import com.code44.finance.utils.MoneyFormatter;
import com.code44.finance.utils.TextBackgroundSpan;
import com.code44.finance.utils.transaction.TransactionAutoComplete;
import com.squareup.otto.Subscribe;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class TransactionEditFragment extends ModelEditFragment<Transaction> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TransactionAutoComplete.TransactionAutoCompleteListener, View.OnLongClickListener {
    private static final int REQUEST_AMOUNT = 1;
    private static final int REQUEST_ACCOUNT_FROM = 2;
    private static final int REQUEST_ACCOUNT_TO = 3;
    private static final int REQUEST_CATEGORY = 4;
    private static final int REQUEST_TAGS = 5;
    private static final int REQUEST_DATE = 6;
    private static final int REQUEST_TIME = 7;
    private static final int REQUEST_EXCHANGE_RATE = 8;
    private static final int REQUEST_AMOUNT_TO = 9;

    @Inject CurrenciesApi currenciesApi;
    @Inject @Main Currency mainCurrency;
    @Inject TransactionAutoComplete transactionAutoComplete;

    private Button date_B;
    private Button time_B;
    private ImageButton transactionType_IB;
    private Button amount_B;
    private Button exchangeRate_B;
    private Button amountTo_B;
    private Button accountFrom_B;
    private Button accountTo_B;
    private ImageView color_IV;
    private Button category_B;
    private Button tags_B;
    private EditText note_ET;
    private CheckBox confirmed_CB;
    private CheckBox includeInReports_CB;
    private Button save_B;

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
        transactionType_IB = (ImageButton) view.findViewById(R.id.transactionType_IB);
        amount_B = (Button) view.findViewById(R.id.amount_B);
        exchangeRate_B = (Button) view.findViewById(R.id.exchangeRate_B);
        amountTo_B = (Button) view.findViewById(R.id.amountTo_B);
        accountFrom_B = (Button) view.findViewById(R.id.accountFrom_B);
        accountTo_B = (Button) view.findViewById(R.id.accountTo_B);
        color_IV = (ImageView) view.findViewById(R.id.colorImageView);
        category_B = (Button) view.findViewById(R.id.category_B);
        tags_B = (Button) view.findViewById(R.id.tags_B);
        date_B = (Button) view.findViewById(R.id.date_B);
        time_B = (Button) view.findViewById(R.id.time_B);
        note_ET = (EditText) view.findViewById(R.id.note_ET);
        confirmed_CB = (CheckBox) view.findViewById(R.id.confirmed_CB);
        includeInReports_CB = (CheckBox) view.findViewById(R.id.includeInReports_CB);
        save_B = (Button) view.findViewById(R.id.saveButton);

        // Setup
        transactionType_IB.setOnClickListener(this);
        amount_B.setOnClickListener(this);
        amount_B.setOnLongClickListener(this);
        exchangeRate_B.setOnClickListener(this);
        exchangeRate_B.setOnLongClickListener(this);
        amountTo_B.setOnClickListener(this);
        amount_B.setOnLongClickListener(this);
        accountFrom_B.setOnClickListener(this);
        accountFrom_B.setOnLongClickListener(this);
        accountTo_B.setOnClickListener(this);
        accountTo_B.setOnLongClickListener(this);
        category_B.setOnClickListener(this);
        category_B.setOnLongClickListener(this);
        tags_B.setOnClickListener(this);
        tags_B.setOnLongClickListener(this);
        date_B.setOnClickListener(this);
        date_B.setOnLongClickListener(this);
        time_B.setOnClickListener(this);
        time_B.setOnLongClickListener(this);
        confirmed_CB.setOnCheckedChangeListener(this);
        includeInReports_CB.setOnCheckedChangeListener(this);
        note_ET.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                model.setNote(note_ET.getText().toString());
            }

            @Override public void afterTextChanged(Editable s) {
            }
        });

        final boolean isAutoAmountRequested = savedInstanceState != null;
        if ((StringUtils.isEmpty(modelId) || modelId.equals("0")) && !isAutoAmountRequested) {
            CalculatorActivity.start(this, REQUEST_AMOUNT, 0);
        }
    }

    @Override public void onResume() {
        super.onResume();
        getEventBus().register(this);
        transactionAutoComplete.setListener(this);
    }

    @Override public void onPause() {
        super.onPause();
        getEventBus().unregister(this);
        transactionAutoComplete.setListener(null);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_AMOUNT:
                    model.setAmount(data.getLongExtra(CalculatorActivity.RESULT_EXTRA_RESULT, 0));
                    onModelLoaded(model);
                    transactionAutoComplete.setAmount(model.getAmount());
                    return;
                case REQUEST_ACCOUNT_FROM:
                    model.setAccountFrom(data.<Account>getParcelableExtra(ModelListActivityOld.RESULT_EXTRA_MODEL));
                    onModelLoaded(model);
                    transactionAutoComplete.setAccountFrom(model.getAccountFrom());
                    refreshExchangeRate();
                    return;
                case REQUEST_ACCOUNT_TO:
                    model.setAccountTo(data.<Account>getParcelableExtra(ModelListActivityOld.RESULT_EXTRA_MODEL));
                    onModelLoaded(model);
                    transactionAutoComplete.setAccountTo(model.getAccountTo());
                    refreshExchangeRate();
                    return;
                case REQUEST_CATEGORY:
                    model.setCategory(data.<Category>getParcelableExtra(ModelListActivityOld.RESULT_EXTRA_MODEL));
                    onModelLoaded(model);
                    transactionAutoComplete.setCategory(model.getCategory());
                    return;
                case REQUEST_TAGS:
                    final Parcelable[] parcelables = data.getParcelableArrayExtra(ModelListActivityOld.RESULT_EXTRA_MODELS);
                    final List<Tag> tags = new ArrayList<>();
                    for (Parcelable parcelable : parcelables) {
                        tags.add((Tag) parcelable);
                    }
                    model.setTags(tags);
                    onModelLoaded(model);
                    transactionAutoComplete.setTags(tags);
                    return;
                case REQUEST_EXCHANGE_RATE:
                    model.setExchangeRate(data.getDoubleExtra(CalculatorActivity.RESULT_EXTRA_RAW_RESULT, 1.0));
                    if (Double.compare(model.getExchangeRate(), 0) <= 0) {
                        model.setExchangeRate(1.0);
                    }
                    onModelLoaded(model);
                    return;
                case REQUEST_AMOUNT_TO:
                    final long amountTo = data.getLongExtra(CalculatorActivity.RESULT_EXTRA_RESULT, 0);
                    if (Double.compare(model.getExchangeRate(), 0) == 0) {
                        model.setExchangeRate(1.0);
                    }
                    final long amount = Math.round(amountTo / model.getExchangeRate());
                    model.setAmount(amount);
                    onModelLoaded(model);
                    transactionAutoComplete.setAmount(model.getAmount());
                    return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override public boolean onSave(Context context, Transaction model) {
        model.setTransactionState(model.getTransactionState() == TransactionState.Confirmed && canBeConfirmed(model, false) ? TransactionState.Confirmed : TransactionState.Pending);
        DataStore.insert().values(model.asValues()).into(context, TransactionsProvider.uriTransactions());
        return true;
    }

    @Override protected void ensureModelUpdated(Transaction model) {
        model.setNote(note_ET.getText().toString());
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelServerId) {
        return Tables.Transactions.getQuery().asCursorLoader(context, TransactionsProvider.uriTransaction(modelServerId));
    }

    @Override protected Transaction getModelFrom(Cursor cursor) {
        final Transaction transaction = Transaction.from(cursor);

        if (!transaction.hasId()) {
            transactionAutoComplete.setListener(this);
            transactionAutoComplete.setTransaction(transaction);
        }

        return transaction;
    }

    @Override protected void onModelLoaded(Transaction transaction) {
        switch (transaction.getTransactionType()) {
            case Expense:
                accountFrom_B.setVisibility(View.VISIBLE);
                accountTo_B.setVisibility(View.GONE);
                color_IV.setVisibility(View.VISIBLE);
                category_B.setVisibility(View.VISIBLE);
                transactionType_IB.setImageResource(R.drawable.ic_category_type_expense);
                exchangeRate_B.setVisibility(View.GONE);
                amountTo_B.setVisibility(View.GONE);
                break;
            case Income:
                accountFrom_B.setVisibility(View.GONE);
                accountTo_B.setVisibility(View.VISIBLE);
                color_IV.setVisibility(View.VISIBLE);
                category_B.setVisibility(View.VISIBLE);
                transactionType_IB.setImageResource(R.drawable.ic_category_type_income);
                exchangeRate_B.setVisibility(View.GONE);
                amountTo_B.setVisibility(View.GONE);
                break;
            case Transfer:
                accountFrom_B.setVisibility(View.VISIBLE);
                accountTo_B.setVisibility(View.VISIBLE);
                color_IV.setVisibility(View.GONE);
                category_B.setVisibility(View.GONE);
                transactionType_IB.setImageResource(R.drawable.ic_category_type_transfer);
                final boolean bothAccountsSet = transaction.getAccountFrom() != null && transaction.getAccountTo() != null;
                final boolean differentCurrencies = bothAccountsSet && !transaction.getAccountFrom().getCurrency().getId().equals(transaction.getAccountTo().getCurrency().getId());
                if (bothAccountsSet && differentCurrencies) {
                    exchangeRate_B.setVisibility(View.VISIBLE);
                    amountTo_B.setVisibility(View.VISIBLE);

                    NumberFormat format = DecimalFormat.getInstance(Locale.ENGLISH);
                    format.setGroupingUsed(false);
                    exchangeRate_B.setText(format.format(model.getExchangeRate()));
                    amountTo_B.setText(MoneyFormatter.format(transaction.getAccountTo().getCurrency(), Math.round(transaction.getAmount() * transaction.getExchangeRate())));
                } else {
                    exchangeRate_B.setVisibility(View.GONE);
                    amountTo_B.setVisibility(View.GONE);
                }
                break;
        }

        final DateTime dateTime = new DateTime(transaction.getDate());
        date_B.setText(DateUtils.formatDateTime(getActivity(), dateTime, DateUtils.FORMAT_SHOW_DATE));
        time_B.setText(DateUtils.formatDateTime(getActivity(), dateTime, DateUtils.FORMAT_SHOW_TIME));
        amount_B.setText(MoneyFormatter.format(getAmountCurrency(transaction), transaction.getAmount()));
        accountFrom_B.setText(transaction.getAccountFrom() == null ? null : transaction.getAccountFrom().getTitle());
        accountTo_B.setText(transaction.getAccountTo() == null ? null : transaction.getAccountTo().getTitle());
        color_IV.setColorFilter(getCategoryColor(transaction));
        category_B.setText(transaction.getCategory() == null ? null : transaction.getCategory().getTitle());
        note_ET.setText(transaction.getNote());
        confirmed_CB.setChecked(transaction.getTransactionState() == TransactionState.Confirmed && canBeConfirmed(transaction, false));
        includeInReports_CB.setChecked(transaction.includeInReports());
        save_B.setText(confirmed_CB.isChecked() ? R.string.save : R.string.pending);

        final SpannableStringBuilder subtitle = new SpannableStringBuilder();
        for (Tag tag : transaction.getTags()) {
            subtitle.append(tag.getTitle());
            subtitle.setSpan(new TextBackgroundSpan(getResources().getColor(R.color.bg_secondary), getResources().getDimension(R.dimen.tag_radius)), subtitle.length() - tag.getTitle().length(), subtitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            subtitle.append(" ");
        }
        tags_B.setText(subtitle);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transactionType_IB:
                toggleTransactionType();
                break;
            case R.id.amount_B:
                CalculatorActivity.start(this, REQUEST_AMOUNT, model.getAmount());
                break;
            case R.id.exchangeRate_B:
                CalculatorActivity.start(this, REQUEST_EXCHANGE_RATE, model.getExchangeRate());
                break;
            case R.id.amountTo_B:
                CalculatorActivity.start(this, REQUEST_AMOUNT_TO, Math.round(model.getAmount() * model.getExchangeRate()));
                break;
            case R.id.accountFrom_B:
                AccountsActivity.startSelect(this, REQUEST_ACCOUNT_FROM);
                break;
            case R.id.accountTo_B:
                AccountsActivity.startSelect(this, REQUEST_ACCOUNT_TO);
                break;
            case R.id.category_B:
                CategoriesActivity.startSelect(this, REQUEST_CATEGORY, model.getTransactionType());
                break;
            case R.id.tags_B:
                TagsActivity.startMultiSelect(this, REQUEST_TAGS, model.getTags());
                break;
            case R.id.date_B:
                DatePickerDialog.show(getChildFragmentManager(), REQUEST_DATE, model.getDate());
                break;
            case R.id.time_B:
                TimePickerDialog.show(getChildFragmentManager(), REQUEST_TIME, model.getDate());
                break;
        }
    }

    @Override public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.amount_B:
                model.setAmount(0);
                onModelLoaded(model);
                return true;
            case R.id.exchangeRate_B:
                model.setExchangeRate(1.0);
                onModelLoaded(model);
                return true;
            case R.id.amountTo_B:
                model.setAmount(0);
                onModelLoaded(model);
                return true;
            case R.id.accountFrom_B:
                model.setAccountFrom(null);
                onModelLoaded(model);
                return true;
            case R.id.accountTo_B:
                model.setAccountTo(null);
                onModelLoaded(model);
                return true;
            case R.id.category_B:
                model.setCategory(null);
                onModelLoaded(model);
                return true;
            case R.id.tags_B:
                model.setTags(null);
                onModelLoaded(model);
                return true;
            case R.id.date_B:
            case R.id.time_B:
                model.setDate(System.currentTimeMillis());
                onModelLoaded(model);
                return true;
        }
        return false;
    }

    @Override public void onCheckedChanged(CompoundButton view, boolean checked) {
        switch (view.getId()) {
            case R.id.confirmed_CB:
                if (canBeConfirmed(model, true)) {
                    model.setTransactionState(checked ? TransactionState.Confirmed : TransactionState.Pending);
                }
                onModelLoaded(model);
                break;
            case R.id.includeInReports_CB:
                model.setIncludeInReports(checked);
                break;
        }
    }

    @Override public void onTransactionAutoCompleteAmounts(List<Long> amounts) {

    }

    @Override public void onTransactionAutoCompleteAccountsFrom(List<Account> accounts) {
        final Account newAccount = accounts.get(0);
        if (!newAccount.equals(model.getAccountFrom())) {
            model.setAccountFrom(accounts.get(0));
            onModelLoaded(model);
            refreshExchangeRate();
        }
    }

    @Override public void onTransactionAutoCompleteAccountsTo(List<Account> accounts) {
        final Account newAccount = accounts.get(0);
        if (!newAccount.equals(model.getAccountTo())) {
            model.setAccountTo(accounts.get(0));
            onModelLoaded(model);
            refreshExchangeRate();
        }
    }

    @Override public void onTransactionAutoCompleteCategories(List<Category> categories) {
        model.setCategory(categories.get(0));
        onModelLoaded(model);
    }

    @Override public void onTransactionAutoCompleteTags(List<Tag> tags) {

    }

    @Subscribe public void onDateSet(DatePickerDialog.DateSelected dateSelected) {
        final DateTime date = new DateTime(model.getDate())
                .withYear(dateSelected.getYear())
                .withMonthOfYear(dateSelected.getMonthOfYear())
                .withDayOfMonth(dateSelected.getDayOfMonth());
        model.setDate(date.getMillis());
        onModelLoaded(model);
        transactionAutoComplete.setDate(model.getDate());
    }

    @Subscribe public void onTimeSet(TimePickerDialog.TimeSelected timeSelected) {
        final DateTime date = new DateTime(model.getDate())
                .withHourOfDay(timeSelected.getHourOfDay())
                .withMinuteOfHour(timeSelected.getMinute());
        model.setDate(date.getMillis());
        onModelLoaded(model);
        transactionAutoComplete.setDate(model.getDate());
    }

    @Subscribe public void onExchangeRateUpdated(ExchangeRateRequest request) {
        if (!request.isError() && model.getAccountFrom() != null && model.getAccountTo() != null && model.getAccountFrom().getCurrency().getCode().equals(request.getFromCode()) && model.getAccountTo().getCurrency().getCode().equals(request.getToCode())) {
            setExchangeRate(request.getCurrency().getExchangeRate());
        }
    }

    private void toggleTransactionType() {
        switch (model.getTransactionType()) {
            case Expense:
                model.setTransactionType(TransactionType.Income);
                model.setAccountFrom(null);
                break;
            case Income:
                model.setTransactionType(TransactionType.Transfer);
                model.setAccountTo(null);
                break;
            case Transfer:
                model.setTransactionType(TransactionType.Expense);
                model.setCategory(null);
                break;
        }
        model.setCategory(null);
        onModelLoaded(model);
        transactionAutoComplete.setTransactionType(model.getTransactionType());
    }

    private Currency getAmountCurrency(Transaction transaction) {
        Currency transactionCurrency;
        switch (transaction.getTransactionType()) {
            case Expense:
                transactionCurrency = transaction.getAccountFrom() == null ? null : transaction.getAccountFrom().getCurrency();
                break;
            case Income:
                transactionCurrency = transaction.getAccountTo() == null ? null : transaction.getAccountTo().getCurrency();
                break;
            case Transfer:
                transactionCurrency = transaction.getAccountFrom() == null ? null : transaction.getAccountFrom().getCurrency();
                break;
            default:
                throw new IllegalStateException("Category type " + transaction.getTransactionType() + " is not supported.");
        }

        if (transactionCurrency == null || !transactionCurrency.hasId()) {
            // When account is not selected yet, we use main currency.
            transactionCurrency = mainCurrency;
        }

        return transactionCurrency;
    }

    private int getCategoryColor(Transaction transaction) {
        if (transaction.getCategory() == null) {
            switch (transaction.getTransactionType()) {
                case Expense:
                    return getResources().getColor(R.color.text_negative);
                case Income:
                    return getResources().getColor(R.color.text_positive);
                case Transfer:
                    return getResources().getColor(R.color.text_neutral);
                default:
                    throw new IllegalArgumentException("Transaction type " + transaction.getTransactionType() + " is not supported.");
            }
        } else {
            return transaction.getCategory().getColor();
        }
    }

    private void refreshExchangeRate() {
        switch (model.getTransactionType()) {
            case Expense:
                model.setExchangeRate(1);
                break;
            case Income:
                model.setExchangeRate(1);
                break;
            case Transfer:
                if (model.getAccountFrom() != null && model.getAccountTo() != null) {
                    final Currency currencyFrom = model.getAccountFrom().getCurrency();
                    final Currency currencyTo = model.getAccountTo().getCurrency();
                    if (currencyFrom.isDefault() || currencyTo.isDefault()) {
                        if (currencyFrom.isDefault()) {
                            setExchangeRate(1.0 / currencyTo.getExchangeRate());
                        } else {
                            setExchangeRate(currencyFrom.getExchangeRate());
                        }
                    } else {
                        currenciesApi.getExchangeRate(model.getAccountFrom().getCurrency().getCode(), model.getAccountTo().getCurrency().getCode());
                    }
                }
                break;
        }
    }

    private void setExchangeRate(double exchangeRate) {
        model.setExchangeRate(exchangeRate);
        if (Double.compare(model.getExchangeRate(), 0) <= 0) {
            model.setExchangeRate(1.0);
        }
        onModelLoaded(model);
    }

    private boolean canBeConfirmed(Transaction model, boolean showErrors) {
        boolean canBeConfirmed = validateAmount(showErrors);

        switch (model.getTransactionType()) {
            case Expense:
                canBeConfirmed = validateAccountFrom(showErrors) && canBeConfirmed;
                break;
            case Income:
                canBeConfirmed = validateAccountTo(showErrors) && canBeConfirmed;
                break;
            case Transfer:
                canBeConfirmed = validateAccountFrom(showErrors) && canBeConfirmed;
                canBeConfirmed = validateAccountTo(showErrors) && canBeConfirmed;
                canBeConfirmed = validateAccounts(showErrors) && canBeConfirmed;
                break;
        }

        return canBeConfirmed;
    }

    private boolean validateAmount(boolean showError) {
        if (model.getAmount() <= 0) {
            if (showError) {
                FieldValidationUtils.onError(amount_B);
            }
            return false;
        }
        return true;
    }

    private boolean validateAccountFrom(boolean showError) {
        if (model.getAccountFrom() == null || !model.getAccountFrom().hasId()) {
            if (showError) {
                FieldValidationUtils.onError(accountFrom_B);
            }
            return false;
        }
        return true;
    }

    private boolean validateAccountTo(boolean showError) {
        if (model.getAccountTo() == null || !model.getAccountTo().hasId()) {
            if (showError) {
                FieldValidationUtils.onError(accountTo_B);
            }
            return false;
        }
        return true;
    }

    private boolean validateAccounts(boolean showError) {
        if (model.getAccountTo() != null && model.getAccountFrom() != null && model.getAccountTo().hasId() && model.getAccountTo().getId().equals(model.getAccountFrom().getId())) {
            if (showError) {
                FieldValidationUtils.onError(accountFrom_B);
                FieldValidationUtils.onError(accountTo_B);
            }
            return false;
        }
        return true;
    }
}
