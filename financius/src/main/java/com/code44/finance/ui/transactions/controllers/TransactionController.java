package com.code44.finance.ui.transactions.controllers;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.currencies.ExchangeRateRequest;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
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
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.util.List;
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
    private final CurrenciesApi currenciesApi;
    private final OnTransactionUpdatedListener listener;
    private final TransactionEditData transactionEditData;

    private final TransactionTypeViewController transactionTypeViewController;
    private final AmountViewController amountViewController;
    private final DateTimeViewController dateTimeViewController;
    private final AccountsViewController accountsViewController;
    private final CategoryViewController categoryViewController;
    private final TagsViewController tagsViewController;
    private final NoteViewController noteViewController;
    private final TransactionStateViewController transactionStateViewController;
    private final FlagsViewController flagsViewController;

    private boolean isResumed = false;

    public TransactionController(BaseActivity activity, EventBus eventBus, Executor autoCompleteExecutor, Currency mainCurrency, CurrenciesApi currenciesApi, OnTransactionUpdatedListener listener) {
        this.activity = activity;
        this.eventBus = eventBus;
        this.autoCompleteExecutor = autoCompleteExecutor;
        this.currenciesApi = currenciesApi;
        this.listener = listener;

        transactionEditData = new TransactionEditData();
        transactionTypeViewController = new TransactionTypeViewController(activity, this);
        amountViewController = new AmountViewController(activity, this, this, mainCurrency);
        dateTimeViewController = new DateTimeViewController(activity, this, this);
        accountsViewController = new AccountsViewController(activity, this, this);
        categoryViewController = new CategoryViewController(activity, this, this);
        tagsViewController = new TagsViewController(activity, this, this);
        noteViewController = new NoteViewController(activity, this, this);
        transactionStateViewController = new TransactionStateViewController(activity, this);
        flagsViewController = new FlagsViewController(activity, this);

        CalculatorActivity.start(activity, REQUEST_AMOUNT, 0);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transactionTypeImageButton:
                toggleTransactionType();
                break;
            case R.id.amountButton:
                CalculatorActivity.start(activity, REQUEST_AMOUNT, transactionEditData.getAmount());
                break;
            case R.id.exchangeRateButton:
                CalculatorActivity.start(activity, REQUEST_EXCHANGE_RATE, transactionEditData.getExchangeRate());
                break;
            case R.id.amountToButton:
                CalculatorActivity.start(activity, REQUEST_AMOUNT_TO, Math.round(transactionEditData.getAmount() * transactionEditData.getExchangeRate()));
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
                updateAmount(0);
                return true;
            case R.id.exchangeRateButton:
                updateExchangeRate(1.0);
                return true;
            case R.id.amountToButton:
                updateAmountTo(0);
                return true;
            case R.id.dateButton:
            case R.id.timeButton:
                updateDate(System.currentTimeMillis());
                return true;
            case R.id.accountFromButton:
                updateAccountFrom(null);
                return true;
            case R.id.accountToButton:
                updateAccountTo(null);
                return true;
            case R.id.categoryButton:
                updateCategory(null);
                return true;
            case R.id.tagsButton:
                updateTags(null);
                return true;
            case R.id.noteAutoCompleteTextView:
                updateNote(null);
                return true;
        }
        return false;
    }

    @Override public void onNoteUpdated(String note) {
        updateNote(note);
    }

    @Override public void onTransactionAutoComplete(AutoCompleteResult result) {
        transactionEditData.setAutoCompleteResult(result);
        update();
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.confirmedCheckBox:
                final boolean canBeConfirmed = transactionEditData.validateAmount(amountViewController) && transactionEditData.validateAccountFrom(accountsViewController) && transactionEditData.validateAccountTo(accountsViewController);
                updateTransactionState(canBeConfirmed && isChecked ? TransactionState.Confirmed : TransactionState.Pending);
                break;
            case R.id.includeInReportsCheckBox:
                updateIncludeInReports(isChecked);
                break;
        }
    }

    @Subscribe public void onDateSet(DatePickerDialog.DateSelected dateSelected) {
        final long date = new DateTime(transactionEditData.getDate())
                .withYear(dateSelected.getYear())
                .withMonthOfYear(dateSelected.getMonthOfYear())
                .withDayOfMonth(dateSelected.getDayOfMonth())
                .getMillis();
        updateDate(date);
    }

    @Subscribe public void onTimeSet(TimePickerDialog.TimeSelected timeSelected) {
        final long date = new DateTime(transactionEditData.getDate())
                .withHourOfDay(timeSelected.getHourOfDay())
                .withMinuteOfHour(timeSelected.getMinute())
                .getMillis();
        updateDate(date);
    }

    @Subscribe public void onExchangeRateUpdated(ExchangeRateRequest request) {
        if (!request.isError() && transactionEditData.getAccountFrom() != null && transactionEditData.getAccountTo() != null && transactionEditData.getAccountFrom().getCurrency().getCode().equals(request.getFromCode()) && transactionEditData.getAccountTo().getCurrency().getCode().equals(request.getToCode())) {
            updateExchangeRate(request.getCurrency().getExchangeRate());
        }
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
                    updateAmount(data.getLongExtra(CalculatorActivity.RESULT_EXTRA_RESULT, 0));
                    break;
                case REQUEST_EXCHANGE_RATE:
                    updateExchangeRate(data.getDoubleExtra(CalculatorActivity.RESULT_EXTRA_RAW_RESULT, 1.0));
                    break;
                case REQUEST_AMOUNT_TO:
                    updateAmountTo(data.getLongExtra(CalculatorActivity.RESULT_EXTRA_RESULT, 0));
                    refreshExchangeRate();
                    break;
                case REQUEST_ACCOUNT_FROM:
                    updateAccountFrom(ModelListActivity.<Account>getModelExtra(data));
                    refreshExchangeRate();
                    break;
                case REQUEST_ACCOUNT_TO:
                    updateAccountTo(ModelListActivity.<Account>getModelExtra(data));
                    break;
                case REQUEST_CATEGORY:
                    updateCategory(ModelListActivity.<Category>getModelExtra(data));
                    break;
                case REQUEST_TAGS:
                    updateTags(ModelListActivity.<Tag>getModelsExtra(data));
                    break;
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

        transactionTypeViewController.setTransactionType(transactionEditData.getTransactionType());
        amountViewController.setAmount(transactionEditData.getAmount());
        amountViewController.setExchangeRate(transactionEditData.getExchangeRate());
        dateTimeViewController.setDateTime(transactionEditData.getDate());
        accountsViewController.setTransactionType(transactionEditData.getTransactionType());
        accountsViewController.setAccountFrom(transactionEditData.getAccountFrom());
        accountsViewController.setAccountTo(transactionEditData.getAccountTo());
        categoryViewController.setTransactionType(transactionEditData.getTransactionType());
        categoryViewController.setCategory(transactionEditData.getCategory());
        tagsViewController.setTags(transactionEditData.getTags());
        noteViewController.setNote(transactionEditData.getNote());
        transactionStateViewController.setTransactionState(transactionEditData.getTransactionState());
        flagsViewController.setIncludeInReports(transactionEditData.getIncludeInReports());

        listener.onTransactionUpdated(transactionEditData);
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
        switch (transactionEditData.getTransactionType()) {
            case Expense:
                updateTransactionType(TransactionType.Income);
                break;
            case Income:
                updateTransactionType(TransactionType.Transfer);
                break;
            case Transfer:
                updateTransactionType(TransactionType.Expense);
                break;
        }
    }

    private void refreshExchangeRate() {
        switch (transactionEditData.getTransactionType()) {
            case Expense:
            case Income:
                updateExchangeRate(1);
                break;
            case Transfer:
                if (transactionEditData.getAccountFrom() != null && transactionEditData.getAccountTo() != null) {
                    final Currency currencyFrom = transactionEditData.getAccountFrom().getCurrency();
                    final Currency currencyTo = transactionEditData.getAccountTo().getCurrency();
                    if (currencyFrom.isDefault() || currencyTo.isDefault()) {
                        if (currencyFrom.isDefault()) {
                            updateExchangeRate(1.0 / currencyTo.getExchangeRate());
                        } else {
                            updateExchangeRate(currencyFrom.getExchangeRate());
                        }
                    } else {
                        currenciesApi.getExchangeRate(transactionEditData.getAccountFrom().getCurrency().getCode(), transactionEditData.getAccountTo().getCurrency().getCode());
                    }
                }
                break;
        }
    }

    private void updateTransactionType(TransactionType transactionType) {
        transactionEditData.setTransactionType(transactionType);
        transactionTypeViewController.setTransactionType(transactionType);
        amountViewController.setTransactionType(transactionType);
        accountsViewController.setTransactionType(transactionType);
        categoryViewController.setTransactionType(transactionType);
        listener.onTransactionUpdated(transactionEditData);
        requestAutoComplete();
    }

    private void updateAmount(long amount) {
        transactionEditData.setAmount(amount);
        amountViewController.setAmount(amount);
        listener.onTransactionUpdated(transactionEditData);
        requestAutoComplete();
    }

    private void updateExchangeRate(double exchangeRate) {
        if (Double.compare(exchangeRate, 0) <= 0) {
            exchangeRate = 1.0;
        }
        transactionEditData.setExchangeRate(exchangeRate);
        amountViewController.setExchangeRate(exchangeRate);
        listener.onTransactionUpdated(transactionEditData);
        requestAutoComplete();
    }

    private void updateAmountTo(long amountTo) {
        if (Double.compare(transactionEditData.getExchangeRate(), 0) <= 0) {
            transactionEditData.setExchangeRate(1.0);
        }
        final long newAmount = Math.round(amountTo / transactionEditData.getExchangeRate());
        transactionEditData.setAmount(newAmount);
        amountViewController.setAmount(newAmount);
        listener.onTransactionUpdated(transactionEditData);
        requestAutoComplete();
    }

    private void updateDate(long date) {
        transactionEditData.setDate(date);
        dateTimeViewController.setDateTime(date);
        listener.onTransactionUpdated(transactionEditData);
        requestAutoComplete();
    }

    private void updateAccountFrom(Account account) {
        transactionEditData.setAccountFrom(account);
        accountsViewController.setAccountFrom(account);
        listener.onTransactionUpdated(transactionEditData);
        requestAutoComplete();
    }

    private void updateAccountTo(Account account) {
        transactionEditData.setAccountTo(account);
        accountsViewController.setAccountTo(account);
        listener.onTransactionUpdated(transactionEditData);
        requestAutoComplete();
    }

    private void updateCategory(Category category) {
        transactionEditData.setCategory(category);
        categoryViewController.setCategory(category);
        listener.onTransactionUpdated(transactionEditData);
        requestAutoComplete();
    }

    private void updateTags(List<Tag> tags) {
        transactionEditData.setTags(tags);
        tagsViewController.setTags(tags);
        listener.onTransactionUpdated(transactionEditData);
        requestAutoComplete();
    }

    private void updateNote(String note) {
        transactionEditData.setNote(note);
        noteViewController.setNote(note);
        listener.onTransactionUpdated(transactionEditData);
        requestAutoComplete();
    }

    private void updateTransactionState(TransactionState transactionState) {
        transactionEditData.setTransactionState(transactionState);
        transactionStateViewController.setTransactionState(transactionState);
        listener.onTransactionUpdated(transactionEditData);
    }

    private void updateIncludeInReports(boolean includeInReports) {
        transactionEditData.setIncludeInReports(includeInReports);
        flagsViewController.setIncludeInReports(includeInReports);
        listener.onTransactionUpdated(transactionEditData);
    }

    public static interface OnTransactionUpdatedListener {
        public void onTransactionUpdated(TransactionEditData transactionEditData);
    }
}
