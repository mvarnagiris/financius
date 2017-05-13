package com.code44.finance.ui.transactions.edit;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.AmountRetriever;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.accounts.list.AccountsActivity;
import com.code44.finance.ui.categories.list.CategoriesActivity;
import com.code44.finance.ui.common.activities.CalculatorActivity;
import com.code44.finance.ui.common.activities.ModelEditActivity;
import com.code44.finance.ui.dialogs.DatePickerDialog;
import com.code44.finance.ui.dialogs.TimePickerDialog;
import com.code44.finance.ui.tags.list.TagsActivity;
import com.code44.finance.utils.CategoryUtils;
import com.code44.finance.utils.TextBackgroundSpan;
import com.code44.finance.utils.ThemeUtils;
import com.code44.finance.utils.analytics.Screens;
import com.squareup.otto.Subscribe;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class TransactionEditActivity extends ModelEditActivity<Transaction, AutocompleteTransactionEditData> implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_AMOUNT = 1;
    private static final int REQUEST_ACCOUNT_FROM = 2;
    private static final int REQUEST_ACCOUNT_TO = 3;
    private static final int REQUEST_CATEGORY = 4;
    private static final int REQUEST_TAGS = 5;
    private static final int REQUEST_DATE = 6;
    private static final int REQUEST_TIME = 7;
    private static final int REQUEST_EXCHANGE_RATE = 8;
    private static final int REQUEST_AMOUNT_TO = 9;

    private static final int LOADER_AUTO_COMPLETE = 24;

    private final AutoCompleteLoaderCallbacks autoCompleteLoaderCallbacks = new AutoCompleteLoaderCallbacks();
    private final TextWatcher noteTextWatcher = new NoteTextWatcher();

    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    private ImageView transactionTypeImageView;
    private Button amountButton;
    private Button exchangeRateButton;
    private Button amountToButton;
    private Button dateButton;
    private Button timeButton;
    private Button accountFromButton;
    private ImageButton accountFromDropDownButton;
    private Button accountToButton;
    private ImageButton accountToDropDownButton;
    private ImageView colorImageView;
    private View categoryContainerView;
    private Button categoryButton;
    private ImageButton categoryDropDownButton;
    private View categoryDividerView;
    private Button tagsButton;
    private NoteEditText noteEditText;
    private CheckBox confirmedCheckBox;
    private CheckBox includeInReportsCheckBox;
    private Button saveButton;

    public static void start(Context context, String transactionId) {
        makeActivityStarter(context, TransactionEditActivity.class, transactionId).start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_edit);

        // Get views
        final View transactionTypeContainerView = findViewById(R.id.transactionTypeContainerView);
        final ImageButton tagsDropDownButton = (ImageButton) findViewById(R.id.tagsDropDownButton);
        transactionTypeImageView = (ImageView) findViewById(R.id.transactionTypeImageView);
        amountButton = (Button) findViewById(R.id.amountButton);
        exchangeRateButton = (Button) findViewById(R.id.exchangeRateButton);
        amountToButton = (Button) findViewById(R.id.amountToButton);
        dateButton = (Button) findViewById(R.id.dateButton);
        timeButton = (Button) findViewById(R.id.timeButton);
        accountFromButton = (Button) findViewById(R.id.accountFromButton);
        accountFromDropDownButton = (ImageButton) findViewById(R.id.accountFromDropDownButton);
        accountToButton = (Button) findViewById(R.id.accountToButton);
        accountToDropDownButton = (ImageButton) findViewById(R.id.accountToDropDownButton);
        colorImageView = (ImageView) findViewById(R.id.colorImageView);
        categoryContainerView = findViewById(R.id.categoryContainerView);
        categoryButton = (Button) findViewById(R.id.categoryButton);
        categoryDropDownButton = (ImageButton) findViewById(R.id.categoryDropDownButton);
        categoryDividerView = findViewById(R.id.categoryDividerView);
        tagsButton = (Button) findViewById(R.id.tagsButton);
        noteEditText = (NoteEditText) findViewById(R.id.noteEditText);
        confirmedCheckBox = (CheckBox) findViewById(R.id.confirmedCheckBox);
        includeInReportsCheckBox = (CheckBox) findViewById(R.id.includeInReportsCheckBox);
        saveButton = (Button) findViewById(R.id.saveButton);

        // Setup
        transactionTypeContainerView.setOnClickListener(this);
        tagsDropDownButton.setOnClickListener(this);
        amountButton.setOnClickListener(this);
        amountButton.setOnLongClickListener(this);
        exchangeRateButton.setOnClickListener(this);
        exchangeRateButton.setOnLongClickListener(this);
        amountToButton.setOnClickListener(this);
        amountButton.setOnLongClickListener(this);
        dateButton.setOnClickListener(this);
        dateButton.setOnLongClickListener(this);
        timeButton.setOnClickListener(this);
        timeButton.setOnLongClickListener(this);
        accountFromButton.setOnClickListener(this);
        accountFromButton.setOnLongClickListener(this);
        accountFromDropDownButton.setOnClickListener(this);
        accountToButton.setOnClickListener(this);
        accountToButton.setOnLongClickListener(this);
        accountToDropDownButton.setOnClickListener(this);
        categoryButton.setOnClickListener(this);
        categoryButton.setOnLongClickListener(this);
        categoryDropDownButton.setOnClickListener(this);
        tagsButton.setOnClickListener(this);
        tagsButton.setOnLongClickListener(this);
        noteEditText.addTextChangedListener(noteTextWatcher);
        confirmedCheckBox.setOnCheckedChangeListener(this);
        includeInReportsCheckBox.setOnCheckedChangeListener(this);

        if (isNewModel()) {
            if (savedInstanceState == null) {
                CalculatorActivity.start(this, REQUEST_AMOUNT, 0);
            }
        }

        getSupportLoaderManager().initLoader(LOADER_AUTO_COMPLETE, null, autoCompleteLoaderCallbacks);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_AMOUNT:
                    setAmount(CalculatorActivity.getResultValue(data));
                    break;
                case REQUEST_EXCHANGE_RATE:
                    setExchangeRate(CalculatorActivity.getResultRawValue(data, 1.0));
                    break;
                case REQUEST_AMOUNT_TO:
                    final long newAmount = Math.round(CalculatorActivity.getResultValue(data) / getModelEditData().getExchangeRate());
                    setAmount(newAmount);
                    refreshExchangeRate();
                    break;
                case REQUEST_ACCOUNT_FROM:
                    setAccountFrom(AccountsActivity.<Account>getModelExtra(data));
                    break;
                case REQUEST_ACCOUNT_TO:
                    setAccountTo(AccountsActivity.<Account>getModelExtra(data));
                    break;
                case REQUEST_CATEGORY:
                    setCategory(CategoriesActivity.<Category>getModelExtra(data));
                    break;
                case REQUEST_TAGS:
                    setTags(TagsActivity.<Tag>getModelsExtra(data));
                    break;
            }
        }
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getModelEditData().transactionEditValidator = (TransactionEditValidator) getModelEditValidator();
    }

    @Override protected void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override protected void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @NonNull @Override protected CursorLoader getModelCursorLoader(@NonNull String modelId) {
        return Tables.Transactions.getQuery().asCursorLoader(this, TransactionsProvider.uriTransaction(modelId));
    }

    @NonNull @Override protected Transaction getModelFrom(@NonNull Cursor cursor) {
        return Transaction.from(cursor);
    }

    @NonNull @Override protected AutocompleteTransactionEditData createModelEditData() {
        return new AutocompleteTransactionEditData();
    }

    @NonNull @Override protected ModelEditValidator<AutocompleteTransactionEditData> createModelEditValidator() {
        return new TransactionEditValidator(amountButton, accountFromButton, accountToButton);
    }

    @Override protected void onDataChanged(@NonNull AutocompleteTransactionEditData modelEditData) {
        updateTransactionType();
        updateAmount();
        updateDate();
        updateAccounts();
        updateCategory();
        updateTags();
        updateTransactionState();
        updateIncludeInReports();
        updateNote();
    }

    @NonNull @Override protected Uri getSaveUri() {
        return TransactionsProvider.uriTransactions();
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.TransactionEdit;
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transactionTypeContainerView:
                toggleTransactionType();
                break;
            case R.id.amountButton:
                CalculatorActivity.start(this, REQUEST_AMOUNT, getModelEditData().getAmount());
                break;
            case R.id.exchangeRateButton:
                CalculatorActivity.start(this, REQUEST_EXCHANGE_RATE, getModelEditData().getExchangeRate());
                break;
            case R.id.amountToButton:
                CalculatorActivity.start(this, REQUEST_AMOUNT_TO, Math.round(getModelEditData().getAmount() * getModelEditData().getExchangeRate()));
                break;
            case R.id.dateButton:
                DatePickerDialog.show(this.getSupportFragmentManager(), REQUEST_DATE, getModelEditData().getDate());
                break;
            case R.id.timeButton:
                TimePickerDialog.show(this.getSupportFragmentManager(), REQUEST_TIME, getModelEditData().getDate());
                break;
            case R.id.accountFromButton:
                AccountsActivity.startSelect(this, REQUEST_ACCOUNT_FROM);
                break;
            case R.id.accountFromDropDownButton:
                final List<Account> accountsFrom = getModelEditData().getAutoCompleteResult().getAccountsFrom();
                new PopupAutoCompleteAccount(accountsFrom, new PopupAutoComplete.OnPopupAutoCompleteListener<Account>() {
                    @Override public void onAutoCompleteSelected(Account selectedItem) {
                        setAccountFrom(selectedItem);
                    }

                    @Override public void onAutoCompleteShowAll() {
                        accountFromButton.performClick();
                    }
                }, accountFromButton).show();
                break;
            case R.id.accountToButton:
                AccountsActivity.startSelect(this, REQUEST_ACCOUNT_TO);
                break;
            case R.id.accountToDropDownButton:
                final List<Account> accountsTo = getModelEditData().getAutoCompleteResult().getAccountsTo();
                new PopupAutoCompleteAccount(accountsTo, new PopupAutoComplete.OnPopupAutoCompleteListener<Account>() {
                    @Override public void onAutoCompleteSelected(Account selectedItem) {
                        setAccountTo(selectedItem);
                    }

                    @Override public void onAutoCompleteShowAll() {
                        accountToButton.performClick();
                    }
                }, accountToButton).show();
                break;
            case R.id.categoryButton:
                CategoriesActivity.startSelect(this, REQUEST_CATEGORY, getModelEditData().getTransactionType());
                break;
            case R.id.categoryDropDownButton:
                final List<Category> categories = getModelEditData().getAutoCompleteResult().getCategories();
                new PopupAutoCompleteCategory(categories, new PopupAutoComplete.OnPopupAutoCompleteListener<Category>() {
                    @Override public void onAutoCompleteSelected(Category selectedItem) {
                        setCategory(selectedItem);
                    }

                    @Override public void onAutoCompleteShowAll() {
                        categoryButton.performClick();
                    }
                }, categoryButton).show();
                break;
            case R.id.tagsButton:
                final Collection<Tag> selectedTags = getModelEditData().getTags() != null ? getModelEditData().getTags() : Collections.<Tag>emptyList();
                TagsActivity.startMultiSelect(this, REQUEST_TAGS, selectedTags, getModelEditData().getCategory());
                break;
            case R.id.tagsDropDownButton:
                final List<List<Tag>> tags = getModelEditData().getAutoCompleteResult().getTags();
                new PopupAutoCompleteTags(tags, new PopupAutoComplete.OnPopupAutoCompleteListener<List<Tag>>() {
                    @Override public void onAutoCompleteSelected(List<Tag> selectedItem) {
                        setTags(selectedItem);
                    }

                    @Override public void onAutoCompleteShowAll() {
                        tagsButton.performClick();
                    }
                }, tagsButton).show();
                break;
        }
    }

    @Override public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.amountButton:
                setAmount(0);
                return true;
            case R.id.exchangeRateButton:
                setExchangeRate(1.0);
                return true;
            case R.id.amountToButton:
                setAmount(0);
                return true;
            case R.id.dateButton:
            case R.id.timeButton:
                setDateTime(System.currentTimeMillis());
                return true;
            case R.id.accountFromButton:
                setAccountFrom(null);
                return true;
            case R.id.accountToButton:
                setAccountTo(null);
                return true;
            case R.id.categoryButton:
                setCategory(null);
                return true;
            case R.id.tagsButton:
                setTags(null);
                return true;
        }
        return false;
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.confirmedCheckBox:
                setConfirmed(isChecked);
                break;
            case R.id.includeInReportsCheckBox:
                setIncludeInReports(isChecked);
                break;
        }
    }

    @Override protected void onAfterSave(@NonNull Transaction model) {
        super.onAfterSave(model);
        getAnalytics().event().createTransaction(this, model);
    }

    @Subscribe public void onDateSet(DatePickerDialog.DateSelected dateSelected) {
        final long date = new DateTime(getModelEditData().getDate()).withYear(dateSelected.getYear())
                .withMonthOfYear(dateSelected.getMonthOfYear())
                .withDayOfMonth(dateSelected.getDayOfMonth())
                .getMillis();
        setDateTime(date);
    }

    @Subscribe public void onTimeSet(TimePickerDialog.TimeSelected timeSelected) {
        final long date = new DateTime(getModelEditData().getDate()).withHourOfDay(timeSelected.getHourOfDay())
                .withMinuteOfHour(timeSelected.getMinute())
                .getMillis();
        setDateTime(date);
    }

    private void toggleTransactionType() {
        TransactionType newTransactionType;
        switch (getModelEditData().getTransactionType()) {
            case Expense:
                newTransactionType = TransactionType.Income;
                break;
            case Income:
                newTransactionType = TransactionType.Transfer;
                break;
            case Transfer:
                newTransactionType = TransactionType.Expense;
                break;
            default:
                throw new IllegalArgumentException("TransactionType " + getModelEditData().getTransactionType() + " is not supported.");
        }

        getModelEditData().setTransactionType(newTransactionType);
        onDataChanged(getModelEditData());
        restartAutoCompleteLoader();
    }

    private void setAmount(long amount) {
        getModelEditData().setAmount(amount);
        updateAmount();
        restartAutoCompleteLoader();
    }

    private void setExchangeRate(double exchangeRate) {
        getModelEditData().setExchangeRate(exchangeRate);
        updateAmount();
        restartAutoCompleteLoader();
    }

    private void setDateTime(long dateTime) {
        getModelEditData().setDate(dateTime);
        updateDate();
        restartAutoCompleteLoader();
    }

    private void setAccountFrom(Account account) {
        getModelEditData().setAccountFrom(account);
        refreshExchangeRate();
        updateAccounts();
        restartAutoCompleteLoader();
    }

    private void setAccountTo(Account account) {
        getModelEditData().setAccountTo(account);
        refreshExchangeRate();
        updateAccounts();
        restartAutoCompleteLoader();
    }

    private void setCategory(Category category) {
        getModelEditData().setCategory(category);
        updateCategory();
        restartAutoCompleteLoader();
    }

    private void setTags(List<Tag> tags) {
        getModelEditData().setTags(tags);
        updateTags();
        restartAutoCompleteLoader();
    }

    private void setNote(String note) {
        getModelEditData().setNote(note);
        restartAutoCompleteLoader();
    }

    private void setConfirmed(boolean confirmed) {
        final TransactionEditValidator validator = (TransactionEditValidator) getModelEditValidator();

        if (!validator.validateAmount(getModelEditData())) {
            shakeView(amountButton);
        }

        if (!validator.validateAccountFrom(getModelEditData())) {
            shakeView(accountFromButton);
        }

        if (!validator.validateAccountTo(getModelEditData())) {
            shakeView(accountToButton);
        }

        getModelEditData().setTransactionState(confirmed ? TransactionState.Confirmed : TransactionState.Pending);
        updateTransactionState();
    }

    private void setIncludeInReports(boolean includeInReports) {
        getModelEditData().setIncludeInReports(includeInReports);
        updateIncludeInReports();
    }

    private void updateTransactionType() {
        final TransactionType transactionType = getModelEditData().getTransactionType();
        final int color;
        switch (transactionType) {
            case Expense:
                color = ThemeUtils.getColor(transactionTypeImageView.getContext(), R.attr.textColorNegative);
                break;
            case Income:
                color = ThemeUtils.getColor(transactionTypeImageView.getContext(), R.attr.textColorPositive);
                break;
            case Transfer:
                color = ThemeUtils.getColor(transactionTypeImageView.getContext(), R.attr.textColorNeutral);
                break;
            default:
                throw new IllegalArgumentException("Transaction type " + transactionType + " is not supported.");
        }
        transactionTypeImageView.setColorFilter(color);
    }

    private void updateAmount() {
        final long amount = getModelEditData().getAmount();
        final TransactionType transactionType = getModelEditData().getTransactionType();
        final Account accountFrom = getModelEditData().getAccountFrom();
        final Account accountTo = getModelEditData().getAccountTo();
        final double exchangeRate = getModelEditData().getExchangeRate();

        if (amount > 0) {
            amountButton.setTextColor(ThemeUtils.getColor(amountButton.getContext(), android.R.attr.textColorPrimaryInverse));
        }

        switch (transactionType) {
            case Expense:
                exchangeRateButton.setVisibility(View.GONE);
                amountToButton.setVisibility(View.GONE);
                break;
            case Income:
                exchangeRateButton.setVisibility(View.GONE);
                amountToButton.setVisibility(View.GONE);
                break;
            case Transfer:
                final boolean bothAccountsSet = accountFrom != null && accountTo != null;
                final boolean differentCurrencies = bothAccountsSet && !accountFrom.getCurrencyCode().equals(accountTo.getCurrencyCode());
                if (bothAccountsSet && differentCurrencies) {
                    exchangeRateButton.setVisibility(View.VISIBLE);
                    amountToButton.setVisibility(View.VISIBLE);

                    // TODO This is also done in calculator. Do not duplicate.
                    final NumberFormat format = DecimalFormat.getInstance(Locale.ENGLISH);
                    format.setGroupingUsed(false);
                    format.setMaximumFractionDigits(20);
                    exchangeRateButton.setText(format.format(exchangeRate));
                    amountToButton.setText(amountFormatter.format(accountTo.getCurrencyCode(), Math.round(amount * exchangeRate)));
                } else {
                    exchangeRateButton.setVisibility(View.GONE);
                    amountToButton.setVisibility(View.GONE);
                }
                break;
        }

        amountButton.setText(amountFormatter.format(AmountRetriever.getAmountCurrency(transactionType, accountFrom, accountTo, currenciesManager), amount));
    }

    private void updateDate() {
        final DateTime dateTime = new DateTime(getModelEditData().getDate());
        dateButton.setText(DateUtils.formatDateTime(dateButton.getContext(), dateTime, DateUtils.FORMAT_SHOW_DATE));
        timeButton.setText(DateUtils.formatDateTime(timeButton.getContext(), dateTime, DateUtils.FORMAT_SHOW_TIME));
    }

    private void updateAccounts() {
        final TransactionType transactionType = getModelEditData().getTransactionType();
        switch (transactionType) {
            case Expense:
                accountFromButton.setVisibility(View.VISIBLE);
                accountFromDropDownButton.setVisibility(View.VISIBLE);
                accountToButton.setVisibility(View.GONE);
                accountToDropDownButton.setVisibility(View.GONE);
                break;
            case Income:
                accountFromButton.setVisibility(View.GONE);
                accountFromDropDownButton.setVisibility(View.GONE);
                accountToButton.setVisibility(View.VISIBLE);
                accountToDropDownButton.setVisibility(View.VISIBLE);
                break;
            case Transfer:
                accountFromButton.setVisibility(View.VISIBLE);
                accountFromDropDownButton.setVisibility(View.VISIBLE);
                accountToButton.setVisibility(View.VISIBLE);
                accountToDropDownButton.setVisibility(View.VISIBLE);
                break;
        }

        final Account accountFrom = getModelEditData().getAccountFrom();
        final Account accountTo = getModelEditData().getAccountTo();
        accountFromButton.setText(accountFrom == null ? null : accountFrom.getTitle());
        accountToButton.setText(accountTo == null ? null : accountTo.getTitle());
    }

    private void updateCategory() {
        final TransactionType transactionType = getModelEditData().getTransactionType();
        switch (transactionType) {
            case Expense:
            case Income:
                colorImageView.setVisibility(View.VISIBLE);
                categoryContainerView.setVisibility(View.VISIBLE);
                categoryDividerView.setVisibility(View.VISIBLE);
                categoryDropDownButton.setVisibility(View.VISIBLE);
                break;
            case Transfer:
                colorImageView.setVisibility(View.GONE);
                categoryContainerView.setVisibility(View.GONE);
                categoryDividerView.setVisibility(View.GONE);
                categoryDropDownButton.setVisibility(View.GONE);
                break;
        }

        final Category category = getModelEditData().getCategory();
        colorImageView.setColorFilter(CategoryUtils.getColor(this, category, transactionType));
        categoryButton.setText(category == null ? null : category.getTitle());
    }

    private void updateTags() {
        List<Tag> tags = getModelEditData().getTags();
        if (tags == null) {
            tags = Collections.emptyList();
        }

        final SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (Tag tag : tags) {
            ssb.append(tag.getTitle());
            ssb.setSpan(new TextBackgroundSpan(ThemeUtils.getColor(tagsButton.getContext(), R.attr.backgroundColorSecondary), tagsButton.getResources()
                    .getDimension(R.dimen.tag_radius)), ssb.length() - tag.getTitle()
                    .length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(" ");
        }
        tagsButton.setText(ssb);
    }

    private void updateNote() {
        if (!getModelEditData().isNoteSet()) {
            noteEditText.removeTextChangedListener(noteTextWatcher);
            noteEditText.setText(getModelEditData().getNote());
            noteEditText.addTextChangedListener(noteTextWatcher);
        }
    }

    private void updateTransactionState() {
        final boolean isConfirmed = getModelEditData().getTransactionState() == TransactionState.Confirmed;
        confirmedCheckBox.setOnCheckedChangeListener(null);
        confirmedCheckBox.setChecked(isConfirmed);
        confirmedCheckBox.setOnCheckedChangeListener(this);
        saveButton.setText(isConfirmed ? R.string.save : R.string.pending);
    }

    private void updateIncludeInReports() {
        includeInReportsCheckBox.setOnCheckedChangeListener(null);
        includeInReportsCheckBox.setChecked(getModelEditData().getIncludeInReports());
        includeInReportsCheckBox.setOnCheckedChangeListener(this);
    }

    private void refreshExchangeRate() {
        switch (getModelEditData().getTransactionType()) {
            case Expense:
            case Income:
                setExchangeRate(1.0);
                break;
            case Transfer:
                if (getModelEditData().getAccountFrom() != null && getModelEditData().getAccountTo() != null) {
                    final String fromCurrencyCode = getModelEditData().getAccountFrom().getCurrencyCode();
                    final String toCurrencyCode = getModelEditData().getAccountTo().getCurrencyCode();
                    setExchangeRate(currenciesManager.getExchangeRate(fromCurrencyCode, toCurrencyCode));
                    updateAmount();
                }
                break;
        }
    }

    private void shakeView(View view) {
        final float translateSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        final ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, translateSize, 0, -translateSize, 0);
        animator.setRepeatCount(3);
        animator.setDuration(100);
        animator.start();
    }

    private void restartAutoCompleteLoader() {
        getSupportLoaderManager().restartLoader(LOADER_AUTO_COMPLETE, null, autoCompleteLoaderCallbacks);
    }

    private class AutoCompleteLoaderCallbacks implements LoaderManager.LoaderCallbacks<AutoCompleteResult> {
        @Override public Loader<AutoCompleteResult> onCreateLoader(int id, Bundle args) {
            return new AutoCompleteLoader(TransactionEditActivity.this, new AutoCompleteInput(getModelEditData()));
        }

        @Override public void onLoadFinished(Loader<AutoCompleteResult> loader, AutoCompleteResult data) {
            getModelEditData().setAutoCompleteResult(data);
            onDataChanged(getModelEditData());
        }

        @Override public void onLoaderReset(Loader<AutoCompleteResult> loader) {
        }
    }

    private class NoteTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override public void afterTextChanged(Editable s) {
            noteEditText.removeTextChangedListener(this);
            setNote(noteEditText.getText().toString());
            noteEditText.addTextChangedListener(this);
        }
    }
}
