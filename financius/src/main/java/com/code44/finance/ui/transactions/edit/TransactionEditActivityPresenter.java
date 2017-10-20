package com.code44.finance.ui.transactions.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.CalculatorActivity;
import com.code44.finance.ui.accounts.list.AccountsActivity;
import com.code44.finance.ui.categories.list.CategoriesActivity;
import com.code44.finance.ui.common.ModelListActivity;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.ModelEditActivityPresenter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.ui.dialogs.DatePickerDialog;
import com.code44.finance.ui.dialogs.TimePickerDialog;
import com.code44.finance.ui.tags.list.TagsActivity;
import com.code44.finance.ui.transactions.edit.autocomplete.AutoCompleteAdapter;
import com.code44.finance.ui.transactions.edit.autocomplete.AutoCompleteInput;
import com.code44.finance.ui.transactions.edit.autocomplete.AutoCompleteResult;
import com.code44.finance.ui.transactions.edit.autocomplete.TransactionAutoComplete;
import com.code44.finance.ui.transactions.edit.autocomplete.smart.SmartTransactionAutoComplete;
import com.code44.finance.ui.transactions.edit.presenters.AccountsPresenter;
import com.code44.finance.ui.transactions.edit.presenters.AmountPresenter;
import com.code44.finance.ui.transactions.edit.presenters.CategoryPresenter;
import com.code44.finance.ui.transactions.edit.presenters.DateTimePresenter;
import com.code44.finance.ui.transactions.edit.presenters.FlagsPresenter;
import com.code44.finance.ui.transactions.edit.presenters.MultipleTagsPresenter;
import com.code44.finance.ui.transactions.edit.presenters.NotePresenter;
import com.code44.finance.ui.transactions.edit.presenters.TransactionEditData;
import com.code44.finance.ui.transactions.edit.presenters.TransactionStatePresenter;
import com.code44.finance.ui.transactions.edit.presenters.TransactionTypePresenter;
import com.code44.finance.utils.EventBus;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

class TransactionEditActivityPresenter extends ModelEditActivityPresenter<Transaction> implements TransactionAutoComplete.TransactionAutoCompleteListener, NotePresenter.Callbacks, View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_AMOUNT = 1;
    private static final int REQUEST_ACCOUNT_FROM = 2;
    private static final int REQUEST_ACCOUNT_TO = 3;
    private static final int REQUEST_CATEGORY = 4;
    private static final int REQUEST_TAGS = 5;
    private static final int REQUEST_DATE = 6;
    private static final int REQUEST_TIME = 7;
    private static final int REQUEST_EXCHANGE_RATE = 8;
    private static final int REQUEST_AMOUNT_TO = 9;

    private static final String STATE_TRANSACTION_EDIT_DATA = "STATE_TRANSACTION_EDIT_DATA";

    private static final boolean LOG_AUTO_COMPLETE = true;

    private final CurrenciesManager currenciesManager;
    private final AmountFormatter amountFormatter;
    private final Executor autoCompleteExecutor;

    private Button saveButton;
    private TransactionTypePresenter transactionTypeViewController;
    private AmountPresenter amountViewController;
    private DateTimePresenter dateTimeViewController;
    private AccountsPresenter accountsViewController;
    private CategoryPresenter categoryViewController;
    private MultipleTagsPresenter tagsViewController;
    private NotePresenter noteViewController;
    private TransactionStatePresenter transactionStateViewController;
    private FlagsPresenter flagsViewController;

    private TransactionEditData transactionEditData;
    private AutoCompleteAdapter<?> currentAutoCompleteAdapter;
    private boolean isUpdated = false;
    private boolean isAutoCompleteUpdateQueued = false;
    private boolean isResumed = false;

    public TransactionEditActivityPresenter(EventBus eventBus, Executor autoCompleteExecutor, CurrenciesManager currenciesManager, AmountFormatter amountFormatter) {
        super(eventBus);
        this.currenciesManager = currenciesManager;
        this.amountFormatter = amountFormatter;
        this.autoCompleteExecutor = autoCompleteExecutor;
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);

        if (savedInstanceState == null) {
            transactionEditData = new TransactionEditData();
        } else {
            transactionEditData = savedInstanceState.getParcelable(STATE_TRANSACTION_EDIT_DATA);
        }

        saveButton = findView(activity, R.id.saveButton);
        transactionTypeViewController = new TransactionTypePresenter(activity, this);
        amountViewController = new AmountPresenter(activity, this, this, currenciesManager, amountFormatter);
        dateTimeViewController = new DateTimePresenter(activity, this, this);
        accountsViewController = new AccountsPresenter(activity, this, this);
        categoryViewController = new CategoryPresenter(activity, this, this);
        tagsViewController = new MultipleTagsPresenter(activity, this, this);
        noteViewController = new NotePresenter(activity, this, this);
        transactionStateViewController = new TransactionStatePresenter(activity, this);
        flagsViewController = new FlagsPresenter(activity, this);

        if (isNewModel()) {
            if (savedInstanceState == null) {
                CalculatorActivity.start(activity, REQUEST_AMOUNT, 0);
            }

            requestAutoComplete();
        }
    }

    @Override public void onResume(BaseActivity activity) {
        super.onResume(activity);
        isResumed = true;
        getEventBus().register(this);
        if (!isUpdated) {
            update(false);
        } else if (isAutoCompleteUpdateQueued) {
            update(true);
        }
    }

    @Override public void onPause(BaseActivity activity) {
        super.onPause(activity);
        isResumed = false;
        getEventBus().unregister(this);
    }

    @Override protected void onDataChanged(Transaction storedModel) {
        transactionEditData.setStoredTransaction(storedModel);

        updateTransactionType(transactionEditData.getTransactionType());
        updateAmount(transactionEditData.getAmount());
        updateExchangeRate(transactionEditData.getExchangeRate());
        updateDate(transactionEditData.getDate());
        updateAccountFrom(transactionEditData.getAccountFrom());
        updateAccountTo(transactionEditData.getAccountTo());
        updateCategory(transactionEditData.getCategory());
        updateTags(transactionEditData.getTags());
        updateTransactionState(transactionEditData.getTransactionState());
        updateIncludeInReports(transactionEditData.getIncludeInReports());

        if (!noteViewController.hasFocus()) {
            updateNote(transactionEditData.getNote());
        }

        saveButton.setText(transactionEditData.getTransactionState() == TransactionState.Confirmed ? R.string.save : R.string.pending);
    }

    @Override protected boolean onSave() {
        DataStore.insert().values(transactionEditData.getModel().asContentValues()).into(getActivity(), TransactionsProvider.uriTransactions());
        return true;
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Transactions.getQuery().asCursorLoader(context, TransactionsProvider.uriTransaction(modelId));
    }

    @Override protected Transaction getModelFrom(Cursor cursor) {
        return Transaction.from(cursor);
    }

    @Override public void onActivityResult(BaseActivity activity, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(activity, requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_AMOUNT:
                    transactionEditData.setAmount(data.getLongExtra(CalculatorActivity.RESULT_EXTRA_RESULT, 0));
                    requestAutoComplete();
                    break;
                case REQUEST_EXCHANGE_RATE:
                    transactionEditData.setExchangeRate(data.getDoubleExtra(CalculatorActivity.RESULT_EXTRA_RAW_RESULT, 1.0));
                    requestAutoComplete();
                    break;
                case REQUEST_AMOUNT_TO:
                    final long newAmount = Math.round(data.getLongExtra(CalculatorActivity.RESULT_EXTRA_RESULT, 0) / transactionEditData.getExchangeRate());
                    transactionEditData.setAmount(newAmount);
                    refreshExchangeRate();
                    requestAutoComplete();
                    break;
                case REQUEST_ACCOUNT_FROM:
                    transactionEditData.setAccountFrom(ModelListActivity.<Account>getModelExtra(data));
                    refreshExchangeRate();
                    requestAutoComplete();
                    break;
                case REQUEST_ACCOUNT_TO:
                    transactionEditData.setAccountTo(ModelListActivity.<Account>getModelExtra(data));
                    refreshExchangeRate();
                    requestAutoComplete();
                    break;
                case REQUEST_CATEGORY:
                    transactionEditData.setCategory(ModelsActivityPresenter.<Category>getModelExtra(data));
                    requestAutoComplete();
                    break;
                case REQUEST_TAGS:
                    transactionEditData.setTags(ModelsActivityPresenter.<Tag>getModelsExtra(data));
                    requestAutoComplete();
                    break;
            }
        }
    }

    @Override public void onSaveInstanceState(BaseActivity activity, Bundle outState) {
        super.onSaveInstanceState(activity, outState);
        outState.putParcelable(STATE_TRANSACTION_EDIT_DATA, transactionEditData);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transactionTypeContainerView:
                toggleTransactionType();
                break;
            case R.id.amountButton:
                CalculatorActivity.start(getActivity(), REQUEST_AMOUNT, transactionEditData.getAmount());
                break;
            case R.id.exchangeRateButton:
                CalculatorActivity.start(getActivity(), REQUEST_EXCHANGE_RATE, transactionEditData.getExchangeRate());
                break;
            case R.id.amountToButton:
                CalculatorActivity.start(getActivity(), REQUEST_AMOUNT_TO, Math.round(transactionEditData.getAmount() * transactionEditData.getExchangeRate()));
                break;
            case R.id.dateButton:
                DatePickerDialog.show(getActivity().getSupportFragmentManager(), REQUEST_DATE, transactionEditData.getDate());
                break;
            case R.id.timeButton:
                TimePickerDialog.show(getActivity().getSupportFragmentManager(), REQUEST_TIME, transactionEditData.getDate());
                break;
            case R.id.accountFromButton: {
                final boolean showPopup = !transactionEditData.isAccountFromSet();
                if (showPopup) {
                    currentAutoCompleteAdapter = accountsViewController.showAutoComplete(currentAutoCompleteAdapter, transactionEditData, new AutoCompleteAdapter.OnAutoCompleteItemClickListener<Account>() {
                        @Override public void onAutoCompleteItemClick(Account item) {
                            transactionEditData.setAccountFrom(item);
                            requestAutoComplete();
                        }
                    }, v);
                }

                if (currentAutoCompleteAdapter == null) {
                    AccountsActivity.startSelect(getActivity(), REQUEST_ACCOUNT_FROM);
                }
                break;
            }

            case R.id.accountToButton: {
                final boolean showPopup = !transactionEditData.isAccountFromSet();
                if (showPopup) {
                    currentAutoCompleteAdapter = accountsViewController.showAutoComplete(currentAutoCompleteAdapter, transactionEditData, new AutoCompleteAdapter.OnAutoCompleteItemClickListener<Account>() {
                        @Override public void onAutoCompleteItemClick(Account item) {
                            transactionEditData.setAccountTo(item);
                            requestAutoComplete();
                        }
                    }, v);
                }

                if (currentAutoCompleteAdapter == null) {
                    AccountsActivity.startSelect(getActivity(), REQUEST_ACCOUNT_TO);
                }
                break;
            }

            case R.id.categoryButton: {
                final boolean showPopup = !transactionEditData.isCategorySet();
                if (showPopup) {
                    currentAutoCompleteAdapter = categoryViewController.showAutoComplete(currentAutoCompleteAdapter, transactionEditData, new AutoCompleteAdapter.OnAutoCompleteItemClickListener<Category>() {
                        @Override public void onAutoCompleteItemClick(Category item) {
                            transactionEditData.setCategory(item);
                            requestAutoComplete();
                        }
                    }, v);
                }

                if (currentAutoCompleteAdapter == null) {
                    CategoriesActivity.startSelect(getActivity(), REQUEST_CATEGORY, transactionEditData.getTransactionType());
                }
                break;
            }

            case R.id.tagsButton: {
                final boolean showPopup = !transactionEditData.isTagsSet();
                if (showPopup) {
                    currentAutoCompleteAdapter = tagsViewController.showAutoComplete(currentAutoCompleteAdapter, transactionEditData, new AutoCompleteAdapter.OnAutoCompleteItemClickListener<List<Tag>>() {
                        @Override public void onAutoCompleteItemClick(List<Tag> item) {
                            transactionEditData.setTags(item);
                            requestAutoComplete();
                        }
                    }, v);
                }

                if (currentAutoCompleteAdapter == null) {
                    TagsActivity.startMultiSelect(getActivity(), REQUEST_TAGS, transactionEditData.getTags() != null ? transactionEditData.getTags() : Collections.<Tag>emptyList());
                }
                break;
            }
        }
    }

    @Override public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.amountButton:
                transactionEditData.setAmount(0L);
                requestAutoComplete();
                return true;
            case R.id.exchangeRateButton:
                transactionEditData.setExchangeRate(1.0);
                requestAutoComplete();
                return true;
            case R.id.amountToButton:
                transactionEditData.setAmount(0L);
                requestAutoComplete();
                return true;
            case R.id.dateButton:
            case R.id.timeButton:
                transactionEditData.setDate(System.currentTimeMillis());
                requestAutoComplete();
                return true;
            case R.id.accountFromButton:
                transactionEditData.setAccountFrom(null);
                requestAutoComplete();
                return true;
            case R.id.accountToButton:
                transactionEditData.setAccountTo(null);
                requestAutoComplete();
                return true;
            case R.id.categoryButton:
                transactionEditData.setCategory(null);
                requestAutoComplete();
                return true;
            case R.id.tagsButton:
                transactionEditData.setTags(null);
                requestAutoComplete();
                return true;
        }
        return false;
    }

    @Override public void onNoteUpdated(String note) {
        transactionEditData.setNote(note);
        requestAutoComplete();
    }

    @Override public void onNoteFocusFained() {
        if (!transactionEditData.isNoteSet()) {
            transactionEditData.setNote(null);
            updateNote(transactionEditData.getNote());
            requestAutoComplete();
        }
    }

    @Override public void onTransactionAutoComplete(AutoCompleteResult result) {
        transactionEditData.setAutoCompleteResult(result);
        update(true);
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.confirmedCheckBox:
                boolean canBeConfirmed = true;
                if (!transactionEditData.validateAmount(amountViewController)) {
                    canBeConfirmed = false;
                }

                if (!transactionEditData.validateAccountFrom(accountsViewController)) {
                    canBeConfirmed = false;
                }

                if (!transactionEditData.validateAccountTo(accountsViewController)) {
                    canBeConfirmed = false;
                }

                transactionEditData.setTransactionState(canBeConfirmed && isChecked ? TransactionState.Confirmed : TransactionState.Pending);
                requestAutoComplete();
                break;
            case R.id.includeInReportsCheckBox:
                transactionEditData.setIncludeInReports(isChecked);
                requestAutoComplete();
                break;
        }
    }

    @Subscribe public void onDateSet(DatePickerDialog.DateSelected dateSelected) {
        final long date = new DateTime(transactionEditData.getDate())
                .withYear(dateSelected.getYear())
                .withMonthOfYear(dateSelected.getMonthOfYear())
                .withDayOfMonth(dateSelected.getDayOfMonth())
                .getMillis();
        transactionEditData.setDate(date);
        requestAutoComplete();
    }

    @Subscribe public void onTimeSet(TimePickerDialog.TimeSelected timeSelected) {
        final long date = new DateTime(transactionEditData.getDate())
                .withHourOfDay(timeSelected.getHourOfDay())
                .withMinuteOfHour(timeSelected.getMinute())
                .getMillis();
        transactionEditData.setDate(date);
        requestAutoComplete();
    }

    private void update(boolean isAutoComplete) {
        if (!isResumed) {
            if (isAutoComplete) {
                isAutoCompleteUpdateQueued = true;
            }
            return;
        }
        isUpdated = true;
        isAutoCompleteUpdateQueued = false;

        if (isAutoComplete && transactionEditData.getStoredTransaction() == null &&
                transactionEditData.getAccountFrom() != null && transactionEditData.getAccountTo() != null) {
            transactionEditData.setExchangeRate(currenciesManager.getExchangeRate(transactionEditData.getAccountFrom().getCurrencyCode(), transactionEditData.getAccountTo().getCurrencyCode()));
        }
        onDataChanged(getStoredModel());
    }

    private void requestAutoComplete() {
        hideAutoCompleteItems();

        if (transactionEditData.getStoredTransaction() != null) {
            update(true);
            return;
        }

        final AutoCompleteInput.Builder input = AutoCompleteInput.build(transactionEditData.getTransactionType());
        input.setDate(transactionEditData.getDate());

        if (transactionEditData.isAmountSet()) {
            input.setAmount(transactionEditData.getAmount());
        }

        if (transactionEditData.isAccountFromSet()) {
            input.setAccountFrom(transactionEditData.getAccountFrom());
        }

        if (transactionEditData.isAccountToSet()) {
            input.setAccountTo(transactionEditData.getAccountTo());
        }

        if (transactionEditData.isCategorySet()) {
            input.setCategory(transactionEditData.getCategory());
        }

        if (transactionEditData.isTagsSet()) {
            input.setTags(transactionEditData.getTags());
        }

        if (transactionEditData.isNoteSet()) {
            input.setNote(transactionEditData.getNote());
        }

        new SmartTransactionAutoComplete(getActivity(), autoCompleteExecutor, this, input.build(), LOG_AUTO_COMPLETE).execute();
    }

    private void toggleTransactionType() {
        TransactionType transactionType;
        switch (transactionEditData.getTransactionType()) {
            case Expense:
                transactionType = TransactionType.Income;
                break;
            case Income:
                transactionType = TransactionType.Transfer;
                break;
            case Transfer:
                transactionType = TransactionType.Expense;
                break;
            default:
                throw new IllegalArgumentException("TransactionType " + transactionEditData.getTransactionType() + " is not supported.");
        }

        if (currentAutoCompleteAdapter != null) {
            currentAutoCompleteAdapter.hide();
            currentAutoCompleteAdapter = null;
        }

        transactionEditData.setTransactionType(transactionType);
        requestAutoComplete();
    }

    private void refreshExchangeRate() {
        switch (transactionEditData.getTransactionType()) {
            case Expense:
            case Income:
                transactionEditData.setExchangeRate(1.0);
                requestAutoComplete();
                break;
            case Transfer:
                if (transactionEditData.getAccountFrom() != null && transactionEditData.getAccountTo() != null) {
                    transactionEditData.setExchangeRate(currenciesManager.getExchangeRate(transactionEditData.getAccountFrom().getCurrencyCode(), transactionEditData.getAccountTo().getCurrencyCode()));
                    requestAutoComplete();
                }
                break;
        }
    }

    private void updateTransactionType(TransactionType transactionType) {
        transactionTypeViewController.setTransactionType(transactionType);
        amountViewController.setTransactionType(transactionType);
        accountsViewController.setTransactionType(transactionType);
        categoryViewController.setTransactionType(transactionType);
        noteViewController.setTransactionType(transactionType);
    }

    private void updateAmount(long amount) {
        amountViewController.setAmount(amount);
    }

    private void updateExchangeRate(double exchangeRate) {
        amountViewController.setExchangeRate(exchangeRate);
    }

    private void updateDate(long date) {
        dateTimeViewController.setDateTime(date);
        dateTimeViewController.isSetByUser(transactionEditData.isDateSet());
    }

    private void updateAccountFrom(Account account) {
        accountsViewController.setAccountFrom(account);
        amountViewController.setAccountFrom(account);
    }

    private void updateAccountTo(Account account) {
        accountsViewController.setAccountTo(account);
        amountViewController.setAccountTo(account);
    }

    private void updateCategory(Category category) {
        categoryViewController.setCategory(category);
    }

    private void updateTags(List<Tag> tags) {
        tagsViewController.setTags(tags);
    }

    private void updateNote(String note) {
        noteViewController.setNote(note);
        noteViewController.setIsSetByUser(transactionEditData.isNoteSet());
    }

    private void updateTransactionState(TransactionState transactionState) {
        transactionStateViewController.setTransactionState(transactionState);
    }

    private void updateIncludeInReports(boolean includeInReports) {
        flagsViewController.setIncludeInReports(includeInReports);
    }

    private void hideAutoCompleteItems() {
        if (currentAutoCompleteAdapter != null) {
            currentAutoCompleteAdapter.hide();
            currentAutoCompleteAdapter = null;
        }
    }
}
