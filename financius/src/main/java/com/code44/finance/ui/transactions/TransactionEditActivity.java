package com.code44.finance.ui.transactions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.currencies.ExchangeRateRequest;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.common.utils.Strings;
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
import com.code44.finance.ui.accounts.AccountsActivity;
import com.code44.finance.ui.categories.CategoriesActivity;
import com.code44.finance.ui.common.ModelEditActivity;
import com.code44.finance.ui.common.ModelListActivity;
import com.code44.finance.ui.dialogs.DatePickerDialog;
import com.code44.finance.ui.dialogs.TimePickerDialog;
import com.code44.finance.ui.tags.TagsActivity;
import com.code44.finance.ui.transactions.autocomplete.TransactionAutoCompleteOld;
import com.code44.finance.utils.FieldValidationUtils;
import com.code44.finance.utils.MoneyFormatter;
import com.code44.finance.utils.analytics.Analytics;
import com.squareup.otto.Subscribe;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class TransactionEditActivity extends ModelEditActivity<Transaction> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TransactionAutoCompleteOld.TransactionAutoCompleteListener, View.OnLongClickListener, TagsController.Callbacks {
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
    @Inject TransactionAutoCompleteOld transactionAutoComplete;

    private Button dateButton;
    private Button timeButton;
    private ImageButton transactionTypeImageButton;
    private Button amountButton;
    private Button exchangeRateButton;
    private Button amountToButton;
    private Button accountFromButton;
    private Button accountToButton;
    private ImageView colorImageView;
    private View categoryContainerView;
    private Button categoryButton;
    private View categoryDividerView;
    private CheckBox confirmedCheckBox;
    private CheckBox includeInReportsCheckBox;
    private Button saveButton;
    private TagsController tagsController;
    private NoteController noteController;

    public static void start(Context context, String transactionServerId) {
        startActivity(context, makeIntent(context, TransactionEditActivity.class, transactionServerId));
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_transaction_edit;
    }

    @Override protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        // Get views
        transactionTypeImageButton = (ImageButton) findViewById(R.id.transactionTypeImageButton);
        amountButton = (Button) findViewById(R.id.amountButton);
        exchangeRateButton = (Button) findViewById(R.id.exchangeRateButton);
        amountToButton = (Button) findViewById(R.id.amountToButton);
        dateButton = (Button) findViewById(R.id.dateButton);
        timeButton = (Button) findViewById(R.id.timeButton);
        accountFromButton = (Button) findViewById(R.id.accountFromButton);
        accountToButton = (Button) findViewById(R.id.accountToButton);
        colorImageView = (ImageView) findViewById(R.id.colorImageView);
        categoryContainerView = findViewById(R.id.categoryContainerView);
        categoryButton = (Button) findViewById(R.id.categoryButton);
        categoryDividerView = findViewById(R.id.categoryDividerView);
        confirmedCheckBox = (CheckBox) findViewById(R.id.confirmedCheckBox);
        includeInReportsCheckBox = (CheckBox) findViewById(R.id.includeInReportsCheckBox);
        saveButton = (Button) findViewById(R.id.saveButton);
        final Button tagsButton = (Button) findViewById(R.id.tagsButton);
        final AutoCompleteTextView noteAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.noteAutoCompleteTextView);
        final ImageView dateTimeImageView = (ImageView) findViewById(R.id.dateTimeImageView);
        final ImageView accountImageView = (ImageView) findViewById(R.id.accountImageView);
        final ImageView tagsImageView = (ImageView) findViewById(R.id.tagsImageView);
        final ImageView noteImageView = (ImageView) findViewById(R.id.noteImageView);

        // Setup
        final int imageViewColor = getResources().getColor(R.color.text_secondary);
        dateTimeImageView.setColorFilter(imageViewColor);
        accountImageView.setColorFilter(imageViewColor);
        tagsImageView.setColorFilter(imageViewColor);
        noteImageView.setColorFilter(imageViewColor);
        transactionTypeImageButton.setOnClickListener(this);
        amountButton.setOnClickListener(this);
        amountButton.setOnLongClickListener(this);
        exchangeRateButton.setOnClickListener(this);
        exchangeRateButton.setOnLongClickListener(this);
        amountToButton.setOnClickListener(this);
        amountButton.setOnLongClickListener(this);
        accountFromButton.setOnClickListener(this);
        accountFromButton.setOnLongClickListener(this);
        accountToButton.setOnClickListener(this);
        accountToButton.setOnLongClickListener(this);
        categoryButton.setOnClickListener(this);
        categoryButton.setOnLongClickListener(this);
        dateButton.setOnClickListener(this);
        dateButton.setOnLongClickListener(this);
        timeButton.setOnClickListener(this);
        timeButton.setOnLongClickListener(this);
        confirmedCheckBox.setOnCheckedChangeListener(this);
        includeInReportsCheckBox.setOnCheckedChangeListener(this);
        tagsController = new TagsController(tagsButton, this);
        noteController = new NoteController(noteAutoCompleteTextView);


        final boolean isAutoAmountRequested = savedInstanceState != null;
        if ((Strings.isEmpty(modelId) || modelId.equals("0")) && !isAutoAmountRequested) {
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

            // TODO This is a temporary fix. Need to come up with a solution where this is never null.
            if (model == null) {
                return;
            }

            switch (requestCode) {
                case REQUEST_AMOUNT:
                    model.setAmount(data.getLongExtra(CalculatorActivity.RESULT_EXTRA_RESULT, 0));
                    onModelLoaded(model);
                    transactionAutoComplete.setAmount(model.getAmount());
                    return;
                case REQUEST_ACCOUNT_FROM:
                    model.setAccountFrom(ModelListActivity.<Account>getModelExtra(data));
                    onModelLoaded(model);
                    transactionAutoComplete.setAccountFrom(model.getAccountFrom());
                    refreshExchangeRate();
                    return;
                case REQUEST_ACCOUNT_TO:
                    model.setAccountTo(ModelListActivity.<Account>getModelExtra(data));
                    onModelLoaded(model);
                    transactionAutoComplete.setAccountTo(model.getAccountTo());
                    refreshExchangeRate();
                    return;
                case REQUEST_CATEGORY:
                    model.setCategory(ModelListActivity.<Category>getModelExtra(data));
                    onModelLoaded(model);
                    transactionAutoComplete.setCategory(model.getCategory());
                    return;
                case REQUEST_TAGS:
                    tagsController.handleActivityResult(data);
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
                    if (Double.compare(model.getExchangeRate(), 0) <= 0) {
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

    @Override protected boolean onSave(Transaction model) {
        model.setTransactionState(model.getTransactionState() == TransactionState.Confirmed && canBeConfirmed(model, false) ? TransactionState.Confirmed : TransactionState.Pending);
        DataStore.insert().values(model.asValues()).into(this, TransactionsProvider.uriTransactions());
        return true;
    }

    @Override protected void ensureModelUpdated(Transaction model) {
        model.setNote(noteController.getNote());
    }

    @Override protected CursorLoader getModelCursorLoader(String modelId) {
        return Tables.Transactions.getQuery().asCursorLoader(this, TransactionsProvider.uriTransaction(modelId));
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
                accountFromButton.setVisibility(View.VISIBLE);
                accountToButton.setVisibility(View.GONE);
                colorImageView.setVisibility(View.VISIBLE);
                categoryContainerView.setVisibility(View.VISIBLE);
                categoryDividerView.setVisibility(View.VISIBLE);
                transactionTypeImageButton.setImageResource(R.drawable.ic_category_type_expense);
                exchangeRateButton.setVisibility(View.GONE);
                amountToButton.setVisibility(View.GONE);
                break;
            case Income:
                accountFromButton.setVisibility(View.GONE);
                accountToButton.setVisibility(View.VISIBLE);
                colorImageView.setVisibility(View.VISIBLE);
                categoryContainerView.setVisibility(View.VISIBLE);
                categoryDividerView.setVisibility(View.VISIBLE);
                transactionTypeImageButton.setImageResource(R.drawable.ic_category_type_income);
                exchangeRateButton.setVisibility(View.GONE);
                amountToButton.setVisibility(View.GONE);
                break;
            case Transfer:
                accountFromButton.setVisibility(View.VISIBLE);
                accountToButton.setVisibility(View.VISIBLE);
                colorImageView.setVisibility(View.GONE);
                categoryContainerView.setVisibility(View.GONE);
                categoryDividerView.setVisibility(View.GONE);
                transactionTypeImageButton.setImageResource(R.drawable.ic_category_type_transfer);
                final boolean bothAccountsSet = transaction.getAccountFrom() != null && transaction.getAccountTo() != null;
                final boolean differentCurrencies = bothAccountsSet && !transaction.getAccountFrom().getCurrency().getId().equals(transaction.getAccountTo().getCurrency().getId());
                if (bothAccountsSet && differentCurrencies) {
                    exchangeRateButton.setVisibility(View.VISIBLE);
                    amountToButton.setVisibility(View.VISIBLE);

                    // TODO This is also done in calculator. Do not duplicate.
                    NumberFormat format = DecimalFormat.getInstance(Locale.ENGLISH);
                    format.setGroupingUsed(false);
                    format.setMaximumFractionDigits(20);
                    exchangeRateButton.setText(format.format(model.getExchangeRate()));
                    amountToButton.setText(MoneyFormatter.format(transaction.getAccountTo().getCurrency(), Math.round(transaction.getAmount() * transaction.getExchangeRate())));
                } else {
                    exchangeRateButton.setVisibility(View.GONE);
                    amountToButton.setVisibility(View.GONE);
                }
                break;
        }

        final DateTime dateTime = new DateTime(transaction.getDate());
        dateButton.setText(DateUtils.formatDateTime(this, dateTime, DateUtils.FORMAT_SHOW_DATE));
        timeButton.setText(DateUtils.formatDateTime(this, dateTime, DateUtils.FORMAT_SHOW_TIME));
        amountButton.setText(MoneyFormatter.format(getAmountCurrency(transaction), transaction.getAmount()));
        accountFromButton.setText(transaction.getAccountFrom() == null ? null : transaction.getAccountFrom().getTitle());
        accountToButton.setText(transaction.getAccountTo() == null ? null : transaction.getAccountTo().getTitle());
        colorImageView.setColorFilter(getCategoryColor(transaction));
        categoryButton.setText(transaction.getCategory() == null ? null : transaction.getCategory().getTitle());
        confirmedCheckBox.setChecked(transaction.getTransactionState() == TransactionState.Confirmed && canBeConfirmed(transaction, false));
        includeInReportsCheckBox.setChecked(transaction.includeInReports());
        saveButton.setText(confirmedCheckBox.isChecked() ? R.string.save : R.string.pending);

        tagsController.setTags(transaction.getTags());
        noteController.setNote(transaction.getNote());
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.confirmedCheckBox:
                if (canBeConfirmed(model, true)) {
                    model.setTransactionState(isChecked ? TransactionState.Confirmed : TransactionState.Pending);
                }
                onModelLoaded(model);
                break;
            case R.id.includeInReportsCheckBox:
                model.setIncludeInReports(isChecked);
                break;
        }
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transactionTypeImageButton:
                toggleTransactionType();
                break;
            case R.id.amountButton:
                CalculatorActivity.start(this, REQUEST_AMOUNT, model.getAmount());
                break;
            case R.id.exchangeRateButton:
                CalculatorActivity.start(this, REQUEST_EXCHANGE_RATE, model.getExchangeRate());
                break;
            case R.id.amountToButton:
                CalculatorActivity.start(this, REQUEST_AMOUNT_TO, Math.round(model.getAmount() * model.getExchangeRate()));
                break;
            case R.id.accountFromButton:
                AccountsActivity.startSelect(this, REQUEST_ACCOUNT_FROM);
                break;
            case R.id.accountToButton:
                AccountsActivity.startSelect(this, REQUEST_ACCOUNT_TO);
                break;
            case R.id.categoryButton:
                CategoriesActivity.startSelect(this, REQUEST_CATEGORY, model.getTransactionType());
                break;
            case R.id.dateButton:
                DatePickerDialog.show(getSupportFragmentManager(), REQUEST_DATE, model.getDate());
                break;
            case R.id.timeButton:
                TimePickerDialog.show(getSupportFragmentManager(), REQUEST_TIME, model.getDate());
                break;
        }
    }

    @Override public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.amountButton:
                model.setAmount(0);
                onModelLoaded(model);
                return true;
            case R.id.exchangeRateButton:
                model.setExchangeRate(1.0);
                onModelLoaded(model);
                return true;
            case R.id.amountToButton:
                model.setAmount(0);
                onModelLoaded(model);
                return true;
            case R.id.accountFromButton:
                model.setAccountFrom(null);
                onModelLoaded(model);
                return true;
            case R.id.accountToButton:
                model.setAccountTo(null);
                onModelLoaded(model);
                return true;
            case R.id.categoryButton:
                model.setCategory(null);
                onModelLoaded(model);
                return true;
            case R.id.dateButton:
            case R.id.timeButton:
                model.setDate(System.currentTimeMillis());
                onModelLoaded(model);
                return true;
        }
        return false;
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

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.TransactionEdit;
    }

    @Override public void onRequestTags(List<Tag> tags) {
        TagsActivity.startMultiSelect(this, REQUEST_TAGS, tags);
    }

    @Override public void onTagsUpdated(List<Tag> tags) {
        model.setTags(tags);
        transactionAutoComplete.setTags(tags);
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
                FieldValidationUtils.onError(amountButton);
            }
            return false;
        }
        return true;
    }

    private boolean validateAccountFrom(boolean showError) {
        if (model.getAccountFrom() == null || !model.getAccountFrom().hasId()) {
            if (showError) {
                FieldValidationUtils.onError(accountFromButton);
            }
            return false;
        }
        return true;
    }

    private boolean validateAccountTo(boolean showError) {
        if (model.getAccountTo() == null || !model.getAccountTo().hasId()) {
            if (showError) {
                FieldValidationUtils.onError(accountToButton);
            }
            return false;
        }
        return true;
    }

    private boolean validateAccounts(boolean showError) {
        if (model.getAccountTo() != null && model.getAccountFrom() != null && model.getAccountTo().hasId() && model.getAccountTo().getId().equals(model.getAccountFrom().getId())) {
            if (showError) {
                FieldValidationUtils.onError(accountFromButton);
                FieldValidationUtils.onError(accountToButton);
            }
            return false;
        }
        return true;
    }
}
