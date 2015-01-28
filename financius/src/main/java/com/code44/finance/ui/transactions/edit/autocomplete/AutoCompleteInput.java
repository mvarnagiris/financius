package com.code44.finance.ui.transactions.edit.autocomplete;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;

import java.util.List;

public final class AutoCompleteInput {
    private final TransactionType transactionType;
    private final long date;
    private final long amount;
    private final Account accountFrom;
    private final Account accountTo;
    private final Category category;
    private final List<Tag> tags;
    private final String note;

    private AutoCompleteInput(TransactionType transactionType, long date, long amount, Account accountFrom, Account accountTo, Category category, List<Tag> tags, String note) {
        this.transactionType = Preconditions.notNull(transactionType, "TransactionType cannot be null.");
        this.date = date;
        this.amount = amount;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.category = category;
        this.tags = tags;
        this.note = note;
    }

    public static Builder build(TransactionType transactionType) {
        return new Builder(transactionType);
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

    public static class Builder {
        private final TransactionType transactionType;
        private long date;
        private long amount;
        private Account accountFrom;
        private Account accountTo;
        private Category category;
        private List<Tag> tags;
        private String note;

        public Builder(TransactionType transactionType) {
            this.transactionType = transactionType;
        }

        public Builder setDate(long date) {
            this.date = date;
            return this;
        }

        public Builder setAmount(long amount) {
            this.amount = amount;
            return this;
        }

        public Builder setAccountFrom(Account accountFrom) {
            this.accountFrom = accountFrom;
            return this;
        }

        public Builder setAccountTo(Account accountTo) {
            this.accountTo = accountTo;
            return this;
        }

        public Builder setCategory(Category category) {
            this.category = category;
            return this;
        }

        public Builder setTags(List<Tag> tags) {
            this.tags = tags;
            return this;
        }

        public Builder setNote(String note) {
            this.note = note;
            return this;
        }

        public AutoCompleteInput build() {
            return new AutoCompleteInput(transactionType, date, amount, accountFrom, accountTo, category, tags, note);
        }
    }
}
