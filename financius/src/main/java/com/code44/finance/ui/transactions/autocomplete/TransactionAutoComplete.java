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

    private TransactionType transactionType;
    private Long date;
    private Long amount;
    private Account accountFrom;
    private Account accountTo;
    private Category category;
    private List<Tag> tags;
    private String note;

    protected TransactionAutoComplete(Executor executor, TransactionAutoCompleteListener listener) {
        this.executor = executor;
        this.listener = listener;
    }

    @Override public void run() {
        autoComplete();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override public void run() {
                listener.onTransactionAutoComplete(getResultAmounts(), getResultAccountsFrom(), getResultAccountsTo(), getResultCategories(), getResultTags());
            }
        });
    }

    public void execute() {
        executor.execute(this);
    }

    protected abstract void autoComplete();

    protected abstract List<Long> getResultAmounts();

    protected abstract List<Account> getResultAccountsFrom();

    protected abstract List<Account> getResultAccountsTo();

    protected abstract List<Category> getResultCategories();

    protected abstract List<Tag> getResultTags();

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

    protected TransactionType getTransactionType() {
        return transactionType;
    }

    protected Long getDate() {
        return date;
    }

    protected Long getAmount() {
        return amount;
    }

    protected Account getAccountFrom() {
        return transactionType != TransactionType.Income ? accountFrom : null;
    }

    protected Account getAccountTo() {
        return transactionType != TransactionType.Expense ? accountTo : null;
    }

    protected Category getCategory() {
        return transactionType != TransactionType.Transfer ? category : null;
    }

    public List<Tag> getTags() {
        return tags;
    }

    protected String getNote() {
        return note;
    }

    public static interface TransactionAutoCompleteListener {
        public void onTransactionAutoComplete(List<Long> amounts, List<Account> accountsFrom, List<Account> accountsTo, List<Category> categories, List<Tag> tags);
    }
}
