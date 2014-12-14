package com.code44.finance.ui.transactions.controllers;

import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.tags.TagsActivity;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteInput;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;
import com.code44.finance.ui.transactions.autocomplete.TransactionAutoComplete;
import com.code44.finance.ui.transactions.autocomplete.smart.SmartTransactionAutoComplete;

import java.util.List;
import java.util.concurrent.Executor;

public class TransactionController implements TagsViewController.Callbacks, TransactionAutoComplete.TransactionAutoCompleteListener, NoteViewController.Callbacks {
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
    private final TagsViewController tagsViewController;
    private final NoteViewController noteViewController;

    public TransactionController(BaseActivity activity, Executor autoCompleteExecutor) {
        this.activity = activity;
        this.autoCompleteExecutor = autoCompleteExecutor;
        this.transactionEditData = new TransactionEditData();
        tagsViewController = new TagsViewController(activity, this);
        noteViewController = new NoteViewController(activity, this);
    }

    @Override public void onRequestTags(List<Tag> tags) {
        TagsActivity.startMultiSelect(activity, REQUEST_TAGS, tags);
    }

    @Override public void onTagsUpdated(List<Tag> tags) {
        transactionEditData.setTags(tags);
        requestAutoComplete();
    }

    @Override public void onNoteUpdated(String note) {
        transactionEditData.setNote(note);
        requestAutoComplete();
    }

    @Override public void onTransactionAutoComplete(AutoCompleteResult result) {
        transactionEditData.setAutoCompleteResult(result);
        update();
    }

    public void setStoredTransaction(Transaction transaction) {
        transactionEditData.setStoredTransaction(transaction);
        update();
    }

    private void update() {
        // TODO Update all controllers.
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
