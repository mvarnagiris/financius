package com.code44.finance.ui.transactions.edit;

import android.os.Parcel;
import android.support.annotation.Nullable;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;

import java.util.List;

class AutocompleteTransactionEditData extends TransactionEditData {
    public static final Creator<AutocompleteTransactionEditData> CREATOR = new Creator<AutocompleteTransactionEditData>() {
        public AutocompleteTransactionEditData createFromParcel(Parcel in) {
            return new AutocompleteTransactionEditData(in);
        }

        public AutocompleteTransactionEditData[] newArray(int size) {
            return new AutocompleteTransactionEditData[size];
        }
    };

    private AutoCompleteResult autoCompleteResult;

    public AutocompleteTransactionEditData() {
        super();
    }

    public AutocompleteTransactionEditData(Parcel in) {
        super(in);
        autoCompleteResult = in.readParcelable(AutoCompleteResult.class.getClassLoader());
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(autoCompleteResult, flags);
    }

    @Override public void setTransactionType(TransactionType transactionType) {
        final boolean isChanged = this.transactionType != transactionType;
        if (isChanged) {
            autoCompleteResult = null;
        }
        super.setTransactionType(transactionType);
    }

    @Override public Account getAccountFrom() {
        if (getTransactionType() == TransactionType.Income) {
            return null;
        }

        final Account accountFrom = super.getAccountFrom();
        if (accountFrom != null || isAccountFromSet()) {
            return accountFrom;
        }

        if (autoCompleteResult == null || autoCompleteResult.getAccountsFrom().isEmpty()) {
            return null;
        }

        return autoCompleteResult.getAccountsFrom().get(0);
    }

    @Override public void setAccountFrom(@Nullable Account accountFrom) {
        setTags(getTags());
        super.setAccountFrom(accountFrom);
    }

    @Override public Account getAccountTo() {
        if (getTransactionType() == TransactionType.Expense) {
            return null;
        }

        final Account accountTo = super.getAccountTo();
        if (accountTo != null || isAccountToSet()) {
            return accountTo;
        }

        if (autoCompleteResult == null || autoCompleteResult.getAccountsTo().isEmpty()) {
            return null;
        }

        return autoCompleteResult.getAccountsTo().get(0);
    }

    @Override public void setAccountTo(@Nullable Account accountTo) {
        setTags(getTags());
        super.setAccountTo(accountTo);
    }

    @Override public Category getCategory() {
        if (getTransactionType() == TransactionType.Transfer) {
            return null;
        }

        final Category category = super.getCategory();
        if (category != null || isCategorySet()) {
            return category;
        }

        if (autoCompleteResult == null || autoCompleteResult.getCategories().isEmpty()) {
            return null;
        }

        return autoCompleteResult.getCategories().get(0);
    }

    @Override public List<Tag> getTags() {
        final List<Tag> tags = super.getTags();
        if (tags != null || isTagsSet()) {
            return tags;
        }

        if (autoCompleteResult == null || autoCompleteResult.getTags().isEmpty()) {
            return null;
        }

        return autoCompleteResult.getTags().get(0);
    }

    @Override public void setTags(List<Tag> tags) {
        setCategory(getCategory());
        super.setTags(tags);
    }

    public AutoCompleteResult getAutoCompleteResult() {
        return autoCompleteResult;
    }

    public void setAutoCompleteResult(AutoCompleteResult autoCompleteResult) {
        this.autoCompleteResult = autoCompleteResult;
    }
}
