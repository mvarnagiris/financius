package com.code44.finance.ui.transactions.controllers;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import com.code44.finance.R;
import com.code44.finance.api.currencies.ExchangeRateRequest;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.ui.CalculatorActivity;
import com.code44.finance.ui.accounts.AccountsActivity;
import com.code44.finance.ui.categories.CategoriesActivity;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.ModelListActivity;
import com.code44.finance.ui.dialogs.DatePickerDialog;
import com.code44.finance.ui.dialogs.TimePickerDialog;
import com.code44.finance.ui.tags.TagsActivity;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteInput;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;
import com.code44.finance.ui.transactions.autocomplete.TransactionAutoComplete;
import com.code44.finance.ui.transactions.autocomplete.smart.SmartTransactionAutoComplete;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.MoneyFormatter;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class TransactionController implements TransactionAutoComplete.TransactionAutoCompleteListener, NoteViewController.Callbacks, View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_AMOUNT = 1;
    private static final int REQUEST_ACCOUNT_FROM = 2;
    private static final int REQUEST_ACCOUNT_TO = 3;
    private static final int REQUEST_CATEGORY = 4;
    private static final int REQUEST_TAGS = 5;
    private static final int REQUEST_DATE = 6;
    private static final int REQUEST_TIME = 7;
    private static final int REQUEST_EXCHANGE_RATE = 8;
    private static final int REQUEST_AMOUNT_TO = 9;

    private final BaseActivity activity;
    private final EventBus eventBus;
    private final Executor autoCompleteExecutor;
    private final TransactionEditData transactionEditData;
    private final DateTimeViewController dateTimeViewController;
    private final AccountsViewController accountsViewController;
    private final CategoryViewController categoryViewController;
    private final TagsViewController tagsViewController;
    private final NoteViewController noteViewController;

    private boolean isResumed = false;

    public TransactionController(BaseActivity activity, EventBus eventBus, Executor autoCompleteExecutor) {
        this.activity = activity;
        this.eventBus = eventBus;
        this.autoCompleteExecutor = autoCompleteExecutor;

        transactionEditData = new TransactionEditData();
        dateTimeViewController = new DateTimeViewController(activity, this, this);
        accountsViewController = new AccountsViewController(activity, this, this);
        categoryViewController = new CategoryViewController(activity, this, this);
        tagsViewController = new TagsViewController(activity, this, this);
        noteViewController = new NoteViewController(activity, this, this);

        // Get views
        transactionTypeImageButton = (ImageButton) findViewById(R.id.transactionTypeImageButton);
        amountButton = (Button) findViewById(R.id.amountButton);
        exchangeRateButton = (Button) findViewById(R.id.exchangeRateButton);
        amountToButton = (Button) findViewById(R.id.amountToButton);
        confirmedCheckBox = (CheckBox) findViewById(R.id.confirmedCheckBox);
        includeInReportsCheckBox = (CheckBox) findViewById(R.id.includeInReportsCheckBox);
        saveButton = (Button) findViewById(R.id.saveButton);

        // Setup
        transactionTypeImageButton.setOnClickListener(this);
        amountButton.setOnClickListener(this);
        amountButton.setOnLongClickListener(this);
        exchangeRateButton.setOnClickListener(this);
        exchangeRateButton.setOnLongClickListener(this);
        amountToButton.setOnClickListener(this);
        amountButton.setOnLongClickListener(this);
        confirmedCheckBox.setOnCheckedChangeListener(this);
        includeInReportsCheckBox.setOnCheckedChangeListener(this);

        final boolean isAutoAmountRequested = savedInstanceState != null;
        if ((Strings.isEmpty(modelId) || modelId.equals("0")) && !isAutoAmountRequested) {
            CalculatorActivity.start(this, REQUEST_AMOUNT, 0);
        }
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transactionTypeImageButton:
//                toggleTransactionType();
                break;
            case R.id.amountButton:
//                CalculatorActivity.start(this, REQUEST_AMOUNT, model.getAmount());
                break;
            case R.id.exchangeRateButton:
//                CalculatorActivity.start(this, REQUEST_EXCHANGE_RATE, model.getExchangeRate());
                break;
            case R.id.amountToButton:
//                CalculatorActivity.start(this, REQUEST_AMOUNT_TO, Math.round(model.getAmount() * model.getExchangeRate()));
                break;
            case R.id.dateButton:
                DatePickerDialog.show(activity.getSupportFragmentManager(), REQUEST_DATE, transactionEditData.getDate());
                break;
            case R.id.timeButton:
                TimePickerDialog.show(activity.getSupportFragmentManager(), REQUEST_TIME, transactionEditData.getDate());
                break;
            case R.id.accountFromButton:
                AccountsActivity.startSelect(activity, REQUEST_ACCOUNT_FROM);
                break;
            case R.id.accountToButton:
                AccountsActivity.startSelect(activity, REQUEST_ACCOUNT_TO);
                break;
            case R.id.categoryButton:
                CategoriesActivity.startSelect(activity, REQUEST_CATEGORY, transactionEditData.getTransactionType());
                break;
            case R.id.tagsButton:
                TagsActivity.startMultiSelect(activity, REQUEST_TAGS, transactionEditData.getTags());
                break;
        }
    }

    @Override public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.amountButton:
//                model.setAmount(0);
//                onModelLoaded(model);
                return true;
            case R.id.exchangeRateButton:
//                model.setExchangeRate(1.0);
//                onModelLoaded(model);
                return true;
            case R.id.amountToButton:
//                model.setAmount(0);
//                onModelLoaded(model);
                return true;
            case R.id.dateButton:
            case R.id.timeButton:
                final long date = System.currentTimeMillis();
                dateTimeViewController.setDateTime(date);
                transactionEditData.setDate(date);
                requestAutoComplete();
                return true;
            case R.id.accountFromButton:
                accountsViewController.setAccountFrom(null);
                transactionEditData.setAccountFrom(null);
                requestAutoComplete();
                return true;
            case R.id.accountToButton:
                accountsViewController.setAccountTo(null);
                transactionEditData.setAccountTo(null);
                requestAutoComplete();
                return true;
            case R.id.categoryButton:
                categoryViewController.setCategory(null);
                transactionEditData.setCategory(null);
                requestAutoComplete();
                return true;
            case R.id.tagsButton:
                tagsViewController.setTags(null);
                transactionEditData.setTags(null);
                requestAutoComplete();
                return true;
            case R.id.noteAutoCompleteTextView:
                noteViewController.setNote(null);
                transactionEditData.setNote(null);
                requestAutoComplete();
                return true;
        }
        return false;
    }

    @Override public void onNoteUpdated(String note) {
        transactionEditData.setNote(note);
        requestAutoComplete();
    }

    @Override public void onTransactionAutoComplete(AutoCompleteResult result) {
        transactionEditData.setAutoCompleteResult(result);
        update();
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.confirmedCheckBox:
//                if (canBeConfirmed(model, true)) {
//                    model.setTransactionState(isChecked ? TransactionState.Confirmed : TransactionState.Pending);
//                }
//                onModelLoaded(model);
                break;
            case R.id.includeInReportsCheckBox:
//                model.setIncludeInReports(isChecked);
                break;
        }
    }

    @Subscribe public void onDateSet(DatePickerDialog.DateSelected dateSelected) {
        final long date = new DateTime(transactionEditData.getDate())
                .withYear(dateSelected.getYear())
                .withMonthOfYear(dateSelected.getMonthOfYear())
                .withDayOfMonth(dateSelected.getDayOfMonth())
                .getMillis();
        dateTimeViewController.setDateTime(date);
        transactionEditData.setDate(date);
        requestAutoComplete();
    }

    @Subscribe public void onTimeSet(TimePickerDialog.TimeSelected timeSelected) {
        final long date = new DateTime(transactionEditData.getDate())
                .withHourOfDay(timeSelected.getHourOfDay())
                .withMinuteOfHour(timeSelected.getMinute())
                .getMillis();
        dateTimeViewController.setDateTime(date);
        transactionEditData.setDate(date);
        requestAutoComplete();
    }

    @Subscribe public void onExchangeRateUpdated(ExchangeRateRequest request) {
//        if (!request.isError() && model.getAccountFrom() != null && model.getAccountTo() != null && model.getAccountFrom().getCurrency().getCode().equals(request.getFromCode()) && model.getAccountTo().getCurrency().getCode().equals(request.getToCode())) {
//            setExchangeRate(request.getCurrency().getExchangeRate());
//        }
    }

    public void onResume() {
        isResumed = true;
        eventBus.register(this);
        update();
    }

    public void onPause() {
        isResumed = false;
        eventBus.unregister(this);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_AMOUNT:
//                    model.setAmount(data.getLongExtra(CalculatorActivity.RESULT_EXTRA_RESULT, 0));
//                    onModelLoaded(model);
//                    transactionAutoComplete.setAmount(model.getAmount());
                    return;
                case REQUEST_ACCOUNT_FROM:
                    final Account accountFrom = ModelListActivity.getModelExtra(data);
                    accountsViewController.setAccountFrom(accountFrom);
                    transactionEditData.setAccountFrom(accountFrom);
                    requestAutoComplete();
                    return;
                case REQUEST_ACCOUNT_TO:
                    final Account accountTo = ModelListActivity.getModelExtra(data);
                    accountsViewController.setAccountTo(accountTo);
                    transactionEditData.setAccountTo(accountTo);
                    requestAutoComplete();
                    return;
                case REQUEST_CATEGORY:
                    final Category category = ModelListActivity.getModelExtra(data);
                    categoryViewController.setCategory(category);
                    transactionEditData.setCategory(category);
                    requestAutoComplete();
                    return;
                case REQUEST_TAGS:
                    final List<Tag> tags = ModelListActivity.getModelsExtra(data);
                    tagsViewController.setTags(tags);
                    transactionEditData.setTags(tags);
                    requestAutoComplete();
                    return;
                case REQUEST_EXCHANGE_RATE:
//                    model.setExchangeRate(data.getDoubleExtra(CalculatorActivity.RESULT_EXTRA_RAW_RESULT, 1.0));
//                    if (Double.compare(model.getExchangeRate(), 0) <= 0) {
//                        model.setExchangeRate(1.0);
//                    }
//                    onModelLoaded(model);
                    return;
                case REQUEST_AMOUNT_TO:
//                    final long amountTo = data.getLongExtra(CalculatorActivity.RESULT_EXTRA_RESULT, 0);
//                    if (Double.compare(model.getExchangeRate(), 0) <= 0) {
//                        model.setExchangeRate(1.0);
//                    }
//                    final long amount = Math.round(amountTo / model.getExchangeRate());
//                    model.setAmount(amount);
//                    onModelLoaded(model);
//                    transactionAutoComplete.setAmount(model.getAmount());
                    return;
            }
        }
    }

    public boolean save() {
        DataStore.insert().values(transactionEditData.getModel().asValues()).into(activity, TransactionsProvider.uriTransactions());
        return true;
    }

    public void setStoredTransaction(Transaction transaction) {
        transactionEditData.setStoredTransaction(transaction);
        update();
    }

    private void update() {
        if (!isResumed) {
            return;
        }

        // TODO Update all controllers.
        dateTimeViewController.setDateTime(transactionEditData.getDate());
        accountsViewController.setTransactionType(transactionEditData.getTransactionType());
        accountsViewController.setAccountFrom(transactionEditData.getAccountFrom());
        accountsViewController.setAccountTo(transactionEditData.getAccountTo());
        categoryViewController.setTransactionType(transactionEditData.getTransactionType());
        categoryViewController.setCategory(transactionEditData.getCategory());
        tagsViewController.setTags(transactionEditData.getTags());
        noteViewController.setNote(transactionEditData.getNote());

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

        amountButton.setText(MoneyFormatter.format(getAmountCurrency(transaction), transaction.getAmount()));
        confirmedCheckBox.setChecked(transaction.getTransactionState() == TransactionState.Confirmed && canBeConfirmed(transaction, false));
        includeInReportsCheckBox.setChecked(transaction.includeInReports());
        saveButton.setText(confirmedCheckBox.isChecked() ? R.string.save : R.string.pending);
    }

    private void requestAutoComplete() {
        if (transactionEditData.getStoredTransaction() != null) {
            return;
        }

        final AutoCompleteInput input = AutoCompleteInput.build(transactionEditData.getTransactionType())
                .setDate(transactionEditData.getDate())
                .setAmount(transactionEditData.getAmount())
                .setAccountFrom(transactionEditData.getAccountFrom())
                .setAccountTo(transactionEditData.getAccountTo())
                .setCategory(transactionEditData.getCategory())
                .setTags(transactionEditData.getTags())
                .setNote(transactionEditData.getNote())
                .build();
        new SmartTransactionAutoComplete(activity, autoCompleteExecutor, this, input);
    }

    private void toggleTransactionType() {
//        switch (model.getTransactionType()) {
//            case Expense:
//                model.setTransactionType(TransactionType.Income);
//                model.setAccountFrom(null);
//                break;
//            case Income:
//                model.setTransactionType(TransactionType.Transfer);
//                model.setAccountTo(null);
//                break;
//            case Transfer:
//                model.setTransactionType(TransactionType.Expense);
//                model.setCategory(null);
//                break;
//        }
//        model.setCategory(null);
//        onModelLoaded(model);
//        transactionAutoComplete.setTransactionType(model.getTransactionType());
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
}
