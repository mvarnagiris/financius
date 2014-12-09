package com.code44.finance.ui.transactions.autocomplete;

import android.os.Handler;
import android.os.Looper;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;

import java.util.List;
import java.util.concurrent.Executor;

public abstract class TransactionAutoComplete implements Runnable {
    private final Executor executor;
    private final TransactionAutoCompleteListener listener;

    protected TransactionType transactionType;
    protected Long date;
    protected Long amount;
    protected Account accountFrom;
    protected Account accountTo;
    protected Category category;
    protected List<Tag> tags;
    protected String note;

    protected TransactionAutoComplete(Executor executor, TransactionAutoCompleteListener listener) {
        this.executor = executor;
        this.listener = listener;
    }

    @Override public void run() {
        autoComplete();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override public void run() {
                listener.onTransactionAutoComplete(getAmounts(), getAccountsFrom(), getAccountsTo(), getCategories(), getTags());
            }
        });
    }

    public void execute() {
        executor.execute(this);
    }

    protected abstract void autoComplete();

    protected abstract List<Long> getAmounts();

    protected abstract List<Account> getAccountsFrom();

    protected abstract List<Account> getAccountsTo();

    protected abstract List<Category> getCategories();

    protected abstract List<Tag> getTags();

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setAccountFrom(Account account) {
        this.accountFrom = account;
    }

    public void setAccountTo(Account account) {
        this.accountTo = account;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public static interface TransactionAutoCompleteListener {
        public void onTransactionAutoComplete(List<Long> amounts, List<Account> accountsFrom, List<Account> accountsTo, List<Category> categories, List<Tag> tags);
    }
}
