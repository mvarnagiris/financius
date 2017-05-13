package com.code44.finance.ui.transactions.edit;

import android.support.annotation.NonNull;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

final class AutoCompleteInput {
    private final TransactionType transactionType;
    private final long date;
    private final long amount;
    private final Account accountFrom;
    private final Account accountTo;
    private final Category category;
    private final List<Tag> tags;
    private final String note;

    public AutoCompleteInput(@NonNull TransactionEditData transactionEditData) {
        checkNotNull(transactionEditData, "TransactionEditData cannot be null.");
        this.transactionType = transactionEditData.getTransactionType();
        this.date = transactionEditData.getDate();
        this.amount = transactionEditData.getAmount();
        this.accountFrom = transactionEditData.getAccountFrom();
        this.accountTo = transactionEditData.getAccountTo();
        this.category = transactionEditData.isCategorySet() ? transactionEditData.getCategory() : null;
        this.tags = transactionEditData.getTags();
        this.note = transactionEditData.getNote();
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public long getDate() {
        return date;
    }

    public long getAmount() {
        return amount;
    }

    public Account getAccountFrom() {
        return transactionType != TransactionType.Income ? accountFrom : null;
    }

    public Account getAccountTo() {
        return transactionType != TransactionType.Expense ? accountTo : null;
    }

    public Category getCategory() {
        return transactionType != TransactionType.Transfer ? category : null;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getNote() {
        return note;
    }
}
