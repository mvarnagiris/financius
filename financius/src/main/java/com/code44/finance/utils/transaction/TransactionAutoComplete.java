package com.code44.finance.utils.transaction;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;

import java.util.List;

public abstract class TransactionAutoComplete {
    private final Transaction transaction;

    private TransactionAutoCompleteListener listener;

    protected TransactionAutoComplete() {
        transaction = new Transaction();
    }

    public void setListener(TransactionAutoCompleteListener listener) {
        this.listener = listener;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction.setTransactionType(transaction.getTransactionType());
        this.transaction.setDate(transaction.getDate());
        this.transaction.setAmount(transaction.getAmount());
        this.transaction.setAccountFrom(transaction.getAccountFrom());
        this.transaction.setAccountTo(transaction.getAccountTo());
        this.transaction.setCategory(transaction.getCategory());
        this.transaction.setTags(transaction.getTags());
        this.transaction.setNote(transaction.getNote());
        onTransactionLoaded(this.transaction);
    }

    public void setTransactionType(TransactionType transactionType) {
        transaction.setTransactionType(transactionType);
        onTransactionTypeChanged(transaction, transactionType);
    }

    public void setDate(long date) {
        transaction.setDate(date);
        onDateChanged(transaction, date);
    }

    public void setAmount(long amount) {
        transaction.setAmount(amount);
        onAmountChanged(transaction, amount);
    }

    public void setAccountFrom(Account account) {
        transaction.setAccountFrom(account);
        onAccountFromChanged(transaction, account);
    }

    public void setAccountTo(Account account) {
        transaction.setAccountTo(account);
        onAccountToChanged(transaction, account);
    }

    public void setCategory(Category category) {
        transaction.setCategory(category);
        onCategoryChanged(transaction, category);
    }

    public void setTags(List<Tag> tags) {
        transaction.setTags(tags);
        onTagsChanged(transaction, tags);
    }

    public void setNote(String note) {
        transaction.setNote(note);
        onNoteChanged(transaction, note);
    }

    protected void autoCompleteAmounts(List<Long> amounts) {
        if (listener != null) {
            listener.onTransactionAutoCompleteAmounts(amounts);
        }
    }

    protected void autoCompleteAccontsFrom(List<Account> accounts) {
        if (listener != null) {
            listener.onTransactionAutoCompleteAccountsFrom(accounts);
        }
    }

    protected void autoCompleteAccontsTo(List<Account> accounts) {
        if (listener != null) {
            listener.onTransactionAutoCompleteAccountsTo(accounts);
        }
    }

    protected void autoCompleteCategories(List<Category> categories) {
        if (listener != null) {
            listener.onTransactionAutoCompleteCategories(categories);
        }
    }

    protected void autoCompleteTags(List<Tag> tags) {
        if (listener != null) {
            listener.onTransactionAutoCompleteTags(tags);
        }
    }

    protected abstract void onTransactionLoaded(Transaction transaction);

    protected abstract void onTransactionTypeChanged(Transaction transaction, TransactionType transactionType);

    protected abstract void onDateChanged(Transaction transaction, long date);

    protected abstract void onAmountChanged(Transaction transaction, long amount);

    protected abstract void onAccountFromChanged(Transaction transaction, Account account);

    protected abstract void onAccountToChanged(Transaction transaction, Account account);

    protected abstract void onCategoryChanged(Transaction transaction, Category category);

    protected abstract void onTagsChanged(Transaction transaction, List<Tag> tags);

    protected abstract void onNoteChanged(Transaction transaction, String note);

    public static interface TransactionAutoCompleteListener {
        public void onTransactionAutoCompleteAmounts(List<Long> amounts);

        public void onTransactionAutoCompleteAccountsFrom(List<Account> accounts);

        public void onTransactionAutoCompleteAccountsTo(List<Account> accounts);

        public void onTransactionAutoCompleteCategories(List<Category> categories);

        public void onTransactionAutoCompleteTags(List<Tag> tags);
    }
}
