package com.code44.finance.ui.transactions.controllers;

import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.ui.common.ViewController;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;

import java.util.ArrayList;
import java.util.List;

public class TransactionEditData implements Parcelable {
    public static final Parcelable.Creator<TransactionEditData> CREATOR = new Parcelable.Creator<TransactionEditData>() {
        public TransactionEditData createFromParcel(Parcel in) {
            return new TransactionEditData(in);
        }

        public TransactionEditData[] newArray(int size) {
            return new TransactionEditData[size];
        }
    };

    private Transaction storedTransaction;
    private AutoCompleteResult autoCompleteResult;

    private TransactionType transactionType;
    private Long amount;
    private Long date;
    private Account accountFrom;
    private Account accountTo;
    private Category category;
    private List<Tag> tags;
    private String note;
    private TransactionState transactionState;
    private Boolean includeInReports;
    private Double exchangeRate;

    private boolean isTransactionTypeSet = false;
    private boolean isAmountSet = false;
    private boolean isDateSet = false;
    private boolean isAccountFromSet = false;
    private boolean isAccountToSet = false;
    private boolean isCategorySet = false;
    private boolean isTagsSet = false;
    private boolean isNoteSet = false;
    private boolean isTransactionStateSet = false;
    private boolean isIncludeInReportsSet = false;
    private boolean isExchangeRateSet = false;

    public TransactionEditData() {
    }

    private TransactionEditData(Parcel in) {
        storedTransaction = in.readParcelable(Transaction.class.getClassLoader());
        autoCompleteResult = in.readParcelable(AutoCompleteResult.class.getClassLoader());
        transactionType = (TransactionType) in.readSerializable();
        amount = (Long) in.readValue(Long.class.getClassLoader());
        date = (Long) in.readValue(Long.class.getClassLoader());
        accountFrom = in.readParcelable(Account.class.getClassLoader());
        accountTo = in.readParcelable(Account.class.getClassLoader());
        category = in.readParcelable(Category.class.getClassLoader());
        final boolean hasTags = in.readInt() != 0;
        if (hasTags) {
            tags = new ArrayList<>();
            in.readTypedList(tags, Tag.CREATOR);
        }
        note = in.readString();
        transactionState = (TransactionState) in.readSerializable();
        includeInReports = (Boolean) in.readValue(Boolean.class.getClassLoader());
        exchangeRate = (Double) in.readValue(Double.class.getClassLoader());
        isTransactionTypeSet = in.readInt() == 1;
        isAmountSet = in.readInt() == 1;
        isDateSet = in.readInt() == 1;
        isAccountFromSet = in.readInt() == 1;
        isAccountToSet = in.readInt() == 1;
        isCategorySet = in.readInt() == 1;
        isTagsSet = in.readInt() == 1;
        isNoteSet = in.readInt() == 1;
        isTransactionStateSet = in.readInt() == 1;
        isIncludeInReportsSet = in.readInt() == 1;
        isExchangeRateSet = in.readInt() == 1;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(storedTransaction, flags);
        dest.writeParcelable(autoCompleteResult, flags);
        dest.writeSerializable(transactionType);
        dest.writeValue(amount);
        dest.writeValue(date);
        dest.writeParcelable(accountFrom, flags);
        dest.writeParcelable(accountTo, flags);
        dest.writeParcelable(category, flags);
        final boolean hasTags = tags != null;
        dest.writeInt(hasTags ? 1 : 0);
        if (hasTags) {
            dest.writeTypedList(tags);
        }
        dest.writeString(note);
        dest.writeSerializable(transactionState);
        dest.writeValue(includeInReports);
        dest.writeValue(exchangeRate);
        dest.writeInt(isTransactionTypeSet ? 1 : 0);
        dest.writeInt(isAmountSet ? 1 : 0);
        dest.writeInt(isDateSet ? 1 : 0);
        dest.writeInt(isAccountFromSet ? 1 : 0);
        dest.writeInt(isAccountToSet ? 1 : 0);
        dest.writeInt(isCategorySet ? 1 : 0);
        dest.writeInt(isTagsSet ? 1 : 0);
        dest.writeInt(isNoteSet ? 1 : 0);
        dest.writeInt(isTransactionStateSet ? 1 : 0);
        dest.writeInt(isIncludeInReportsSet ? 1 : 0);
        dest.writeInt(isExchangeRateSet ? 1 : 0);
    }

    public Transaction getStoredTransaction() {
        return storedTransaction;
    }

    public void setStoredTransaction(Transaction storedTransaction) {
        this.storedTransaction = storedTransaction;
    }

    public void setAutoCompleteResult(AutoCompleteResult autoCompleteResult) {
        this.autoCompleteResult = autoCompleteResult;
    }

    public TransactionType getTransactionType() {
        if (isTransactionTypeSet || transactionType != null) {
            return transactionType;
        }

        if (storedTransaction != null && storedTransaction.getTransactionType() != null) {
            return storedTransaction.getTransactionType();
        }

        return TransactionType.Expense;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
        isTransactionTypeSet = true;
        onTransactionTypeChanged();
    }

    public long getAmount() {
        if (amount != null && amount > 0) {
            return amount;
        }

        if (storedTransaction != null) {
            return storedTransaction.getAmount();
        }

        if (autoCompleteResult != null && autoCompleteResult.getAmount() != null) {
            return autoCompleteResult.getAmount();
        }

        return 0;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
        isAmountSet = amount != null && amount > 0;
    }

    public long getDate() {
        if (isDateSet || date != null) {
            return date;
        }

        if (storedTransaction != null) {
            return storedTransaction.getDate();
        }

        return System.currentTimeMillis();
    }

    public void setDate(Long date) {
        this.date = date;
        isDateSet = true;
    }

    public Account getAccountFrom() {
        if (getTransactionType() == TransactionType.Income) {
            return null;
        }

        if (isAccountFromSet || accountFrom != null) {
            return accountFrom;
        }

        if (storedTransaction != null && storedTransaction.getAccountFrom() != null) {
            return storedTransaction.getAccountFrom();
        }

        if (autoCompleteResult != null) {
            return autoCompleteResult.getAccountFrom();
        }

        return null;
    }

    public void setAccountFrom(Account accountFrom) {
        this.accountFrom = accountFrom;
        isAccountFromSet = true;
    }

    public Account getAccountTo() {
        if (getTransactionType() == TransactionType.Expense) {
            return null;
        }

        if (isAccountToSet || accountTo != null) {
            return accountTo;
        }

        if (storedTransaction != null && storedTransaction.getAccountTo() != null) {
            return storedTransaction.getAccountTo();
        }

        if (autoCompleteResult != null) {
            return autoCompleteResult.getAccountTo();
        }

        return null;
    }

    public void setAccountTo(Account accountTo) {
        this.accountTo = accountTo;
        isAccountToSet = true;
    }

    public Category getCategory() {
        if (getTransactionType() == TransactionType.Transfer) {
            return null;
        }

        if (isCategorySet || category != null) {
            return category;
        }

        if (storedTransaction != null && storedTransaction.getCategory() != null && storedTransaction.getCategory().getTransactionType() == getTransactionType()) {
            return storedTransaction.getCategory();
        }

        if (autoCompleteResult != null) {
            return autoCompleteResult.getCategory();
        }

        return null;
    }

    public void setCategory(Category category) {
        this.category = category;
        isCategorySet = true;
    }

    public List<Tag> getTags() {
        if (isTagsSet || tags != null) {
            return tags;
        }

        if (storedTransaction != null && storedTransaction.getTags() != null) {
            return storedTransaction.getTags();
        }

        if (autoCompleteResult != null) {
            return autoCompleteResult.getTags();
        }

        return null;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
        isTagsSet = true;
    }

    public String getNote() {
        if (isNoteSet || note != null) {
            return note;
        }

        if (storedTransaction != null && storedTransaction.getNote() != null) {
            return storedTransaction.getNote();
        }

        if (autoCompleteResult != null) {
            return autoCompleteResult.getNote();
        }

        return null;
    }

    public void setNote(String note) {
        this.note = note;
        isNoteSet = true;
    }

    public TransactionState getTransactionState() {
        if (!canBeConfirmed()) {
            return TransactionState.Pending;
        }

        if (isTransactionStateSet && transactionState != null) {
            return transactionState;
        }

        if (storedTransaction != null && storedTransaction.getTransactionState() != null) {
            return storedTransaction.getTransactionState();
        }

        return TransactionState.Confirmed;
    }

    public void setTransactionState(TransactionState transactionState) {
        this.transactionState = transactionState;
        isTransactionStateSet = true;
    }

    public boolean getIncludeInReports() {
        if (isIncludeInReportsSet && includeInReports != null) {
            return includeInReports;
        }

        return storedTransaction == null || storedTransaction.includeInReports();
    }

    public void setIncludeInReports(Boolean includeInReports) {
        this.includeInReports = includeInReports;
        isIncludeInReportsSet = true;
    }

    public double getExchangeRate() {
        double exchangeRate;

        if (isExchangeRateSet || this.exchangeRate != null) {
            exchangeRate = this.exchangeRate;
        } else if (storedTransaction != null) {
            exchangeRate = storedTransaction.getExchangeRate();
        } else {
            exchangeRate = 1;
        }

        if (Double.compare(exchangeRate, 0) <= 0) {
            exchangeRate = 1;
        }

        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        if (Double.compare(exchangeRate, 0) <= 0) {
            exchangeRate = 1.0;
        }
        this.exchangeRate = exchangeRate;
        isExchangeRateSet = true;
    }

    public boolean isTransactionTypeSet() {
        return isTransactionTypeSet || storedTransaction != null;
    }

    public boolean isAmountSet() {
        return isAmountSet || storedTransaction != null;
    }

    public boolean isDateSet() {
        return isDateSet || storedTransaction != null;
    }

    public boolean isAccountFromSet() {
        return isAccountFromSet || storedTransaction != null;
    }

    public boolean isAccountToSet() {
        return isAccountToSet || storedTransaction != null;
    }

    public boolean isCategorySet() {
        return isCategorySet || storedTransaction != null;
    }

    public boolean isTagsSet() {
        return isTagsSet || storedTransaction != null;
    }

    public boolean isNoteSet() {
        return isNoteSet || storedTransaction != null;
    }

    public boolean isTransactionStateSet() {
        return isTransactionStateSet || storedTransaction != null;
    }

    public boolean isIncludeInReportsSet() {
        return isIncludeInReportsSet || storedTransaction != null;
    }

    public boolean isExchangeRateSet() {
        return isExchangeRateSet || storedTransaction != null;
    }

    public Transaction getModel() {
        final Transaction transaction = new Transaction();
        if (storedTransaction != null) {
            transaction.setId(storedTransaction.getId());
        }
        transaction.setAccountFrom(getAccountFrom());
        transaction.setAccountTo(getAccountTo());
        transaction.setCategory(getCategory());
        transaction.setTags(getTags());
        transaction.setDate(getDate());
        transaction.setAmount(getAmount());
        transaction.setExchangeRate(getExchangeRate());
        transaction.setNote(getNote());
        transaction.setTransactionState(getTransactionState());
        transaction.setTransactionType(getTransactionType());
        transaction.setIncludeInReports(getIncludeInReports());
        return transaction;
    }

    public boolean canBeConfirmed() {
        return validateAmount(null) && validateAccountFrom(null) && validateAccountTo(null);
    }

    public boolean validateAmount(ViewController controller) {
        if (getAmount() > 0) {
            return true;
        }

        if (controller != null) {
            controller.showError(new Throwable());
        }

        return false;
    }

    public boolean validateAccountFrom(ViewController controller) {
        if (getTransactionType() == TransactionType.Income) {
            return true;
        }

        if (getAccountFrom() == null) {
            if (controller != null) {
                controller.showError(new Throwable());
            }
            return false;
        }

        if (getTransactionType() == TransactionType.Transfer && getAccountFrom().equals(getAccountTo())) {
            if (controller != null) {
                controller.showError(new Throwable());
            }
            return false;
        }

        return true;
    }

    public boolean validateAccountTo(ViewController controller) {
        if (getTransactionType() == TransactionType.Expense) {
            return true;
        }

        if (getAccountTo() == null) {
            if (controller != null) {
                controller.showError(new Throwable());
            }
            return false;
        }

        if (getTransactionType() == TransactionType.Transfer && getAccountTo().equals(getAccountFrom())) {
            if (controller != null) {
                controller.showError(new Throwable());
            }
            return false;
        }

        return true;
    }

    private void onTransactionTypeChanged() {
        autoCompleteResult = null;

        accountFrom = null;
        accountTo = null;
        category = null;
        tags = null;
        note = null;

        isAccountFromSet = false;
        isAccountToSet = false;
        isCategorySet = false;
        isTagsSet = false;
        isNoteSet = false;
    }
}
