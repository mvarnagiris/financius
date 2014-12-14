package com.code44.finance.ui.transactions.controllers;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.ui.categories.CategoriesActivity;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.ModelListActivity;
import com.code44.finance.ui.tags.TagsActivity;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteInput;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;
import com.code44.finance.ui.transactions.autocomplete.TransactionAutoComplete;
import com.code44.finance.ui.transactions.autocomplete.smart.SmartTransactionAutoComplete;

import java.util.List;
import java.util.concurrent.Executor;

public class TransactionController implements TransactionAutoComplete.TransactionAutoCompleteListener, NoteViewController.Callbacks, View.OnClickListener, View.OnLongClickListener {
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
    private final Executor autoCompleteExecutor;
    private final TransactionEditData transactionEditData;
    private final CategoryViewController categoryViewController;
    private final TagsViewController tagsViewController;
    private final NoteViewController noteViewController;

    public TransactionController(BaseActivity activity, Executor autoCompleteExecutor) {
        this.activity = activity;
        this.autoCompleteExecutor = autoCompleteExecutor;
        this.transactionEditData = new TransactionEditData();
        categoryViewController = new CategoryViewController(activity, this, this);
        tagsViewController = new TagsViewController(activity, this, this);
        noteViewController = new NoteViewController(activity, this, this);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.categoryButton:
                categoryViewController.setCategoryAndTransactionType(null, transactionEditData.getTransactionType());
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

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_AMOUNT:
//                    model.setAmount(data.getLongExtra(CalculatorActivity.RESULT_EXTRA_RESULT, 0));
//                    onModelLoaded(model);
//                    transactionAutoComplete.setAmount(model.getAmount());
                    return;
                case REQUEST_ACCOUNT_FROM:
//                    model.setAccountFrom(ModelListActivity.<Account>getModelExtra(data));
//                    onModelLoaded(model);
//                    transactionAutoComplete.setAccountFrom(model.getAccountFrom());
//                    refreshExchangeRate();
                    return;
                case REQUEST_ACCOUNT_TO:
//                    model.setAccountTo(ModelListActivity.<Account>getModelExtra(data));
//                    onModelLoaded(model);
//                    transactionAutoComplete.setAccountTo(model.getAccountTo());
//                    refreshExchangeRate();
                    return;
                case REQUEST_CATEGORY:
                    final Category category = ModelListActivity.getModelExtra(data);
                    categoryViewController.setCategoryAndTransactionType(category, transactionEditData.getTransactionType());
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

    public void setStoredTransaction(Transaction transaction) {
        transactionEditData.setStoredTransaction(transaction);
        update();
    }

    private void update() {
        // TODO Update all controllers.
        categoryViewController.setCategoryAndTransactionType(transactionEditData.getCategory(), transactionEditData.getTransactionType());
        tagsViewController.setTags(transactionEditData.getTags());
        noteViewController.setNote(transactionEditData.getNote());
    }

    private void requestAutoComplete() {
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
}
