package com.code44.finance.ui.transactions.edit;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.ui.common.activities.ModelEditActivity;

import java.util.ArrayList;
import java.util.List;

class TransactionEditData extends ModelEditActivity.ModelEditData<Transaction> {
    public static final Creator<TransactionEditData> CREATOR = new Creator<TransactionEditData>() {
        public TransactionEditData createFromParcel(Parcel in) {
            return new TransactionEditData(in);
        }

        public TransactionEditData[] newArray(int size) {
            return new TransactionEditData[size];
        }
    };

    protected TransactionType transactionType;
    protected Long amount;
    protected Long date;
    protected Account accountFrom;
    protected Account accountTo;
    protected Category category;
    protected List<Tag> tags;
    protected String note;
    protected TransactionState transactionState;
    protected Boolean includeInReports;
    protected Double exchangeRate;
    TransactionEditValidator transactionEditValidator;
    private boolean isTransactionTypeSet;
    private boolean isAmountSet;
    private boolean isDateSet;
    private boolean isAccountFromSet;
    private boolean isAccountToSet;
    private boolean isCategorySet;
    private boolean isTagsSet;
    private boolean isNoteSet;
    private boolean isTransactionStateSet;
    private boolean isIncludeInReportsSet;
    private boolean isExchangeRateSet;

    public TransactionEditData() {
        super();
    }

    public TransactionEditData(Parcel in) {
        super(in);
        transactionType = (TransactionType) in.readSerializable();
        amount = (Long) in.readSerializable();
        date = (Long) in.readSerializable();
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
        includeInReports = (Boolean) in.readSerializable();
        exchangeRate = (Double) in.readSerializable();

        isTransactionTypeSet = in.readByte() != 0;
        isAmountSet = in.readByte() != 0;
        isDateSet = in.readByte() != 0;
        isAccountFromSet = in.readByte() != 0;
        isAccountToSet = in.readByte() != 0;
        isCategorySet = in.readByte() != 0;
        isTagsSet = in.readByte() != 0;
        isNoteSet = in.readByte() != 0;
        isTransactionStateSet = in.readByte() != 0;
        isIncludeInReportsSet = in.readByte() != 0;
        isExchangeRateSet = in.readByte() != 0;
    }

    @Override public Transaction createModel() {
        final Transaction transaction = new Transaction();
        transaction.setId(getId());
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

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(transactionType);
        dest.writeSerializable(amount);
        dest.writeSerializable(date);
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
        dest.writeSerializable(includeInReports);
        dest.writeSerializable(exchangeRate);

        dest.writeByte((byte) (isTransactionTypeSet ? 1 : 0));
        dest.writeByte((byte) (isAmountSet ? 1 : 0));
        dest.writeByte((byte) (isDateSet ? 1 : 0));
        dest.writeByte((byte) (isAccountFromSet ? 1 : 0));
        dest.writeByte((byte) (isAccountToSet ? 1 : 0));
        dest.writeByte((byte) (isCategorySet ? 1 : 0));
        dest.writeByte((byte) (isTagsSet ? 1 : 0));
        dest.writeByte((byte) (isNoteSet ? 1 : 0));
        dest.writeByte((byte) (isTransactionStateSet ? 1 : 0));
        dest.writeByte((byte) (isIncludeInReportsSet ? 1 : 0));
        dest.writeByte((byte) (isExchangeRateSet ? 1 : 0));
    }

    @NonNull public TransactionType getTransactionType() {
        if (transactionType != null) {
            return transactionType;
        }

        if (getStoredModel() != null && getStoredModel().getTransactionType() != null) {
            return getStoredModel().getTransactionType();
        }

        return TransactionType.Expense;
    }

    public void setTransactionType(@Nullable TransactionType transactionType) {
        this.transactionType = transactionType;
        isTransactionTypeSet = true;
        onTransactionTypeChanged();
    }

    public long getAmount() {
        if (isAmountSet) {
            return amount != null && amount > 0 ? amount : 0;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getAmount();
        }

        return 0;
    }

    public void setAmount(@Nullable Long amount) {
        this.amount = amount;
        isAmountSet = true;
    }

    public long getDate() {
        if (isDateSet) {
            return date != null ? date : System.currentTimeMillis();
        }

        if (getStoredModel() != null) {
            return getStoredModel().getDate();
        }

        return System.currentTimeMillis();
    }

    public void setDate(@Nullable Long date) {
        this.date = date;
        isDateSet = true;
    }

    @Nullable public Account getAccountFrom() {
        if (getTransactionType() == TransactionType.Income) {
            return null;
        }

        if (isAccountFromSet) {
            return accountFrom;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getAccountFrom();
        }

        return null;
    }

    public void setAccountFrom(@Nullable Account accountFrom) {
        this.accountFrom = accountFrom;
        isAccountFromSet = true;
    }

    @Nullable public Account getAccountTo() {
        if (getTransactionType() == TransactionType.Expense) {
            return null;
        }

        if (isAccountToSet) {
            return accountTo;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getAccountTo();
        }

        return null;
    }

    public void setAccountTo(@Nullable Account accountTo) {
        this.accountTo = accountTo;
        isAccountToSet = true;
    }

    @Nullable public Category getCategory() {
        if (getTransactionType() == TransactionType.Transfer) {
            return null;
        }

        if (isCategorySet) {
            return category;
        }

        if (getStoredModel() != null && getStoredModel().getCategory() != null && getStoredModel().getCategory()
                .getTransactionType() == getTransactionType()) {
            return getStoredModel().getCategory();
        }

        return null;
    }

    public void setCategory(@Nullable Category category) {
        this.category = category;
        isCategorySet = true;
    }

    @Nullable public List<Tag> getTags() {
        if (isTagsSet) {
            return tags;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getTags();
        }

        return null;
    }

    public void setTags(@Nullable List<Tag> tags) {
        this.tags = tags;
        isTagsSet = true;
    }

    @Nullable public String getNote() {
        if (isNoteSet) {
            return note;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getNote();
        }

        return null;
    }

    public void setNote(@Nullable String note) {
        this.note = note;
        isNoteSet = true;
    }

    @NonNull public TransactionState getTransactionState() {
        if (!transactionEditValidator.canBeConfirmed(this)) {
            return TransactionState.Pending;
        }

        if (transactionState != null) {
            return transactionState;
        }

        if (getStoredModel() != null && getStoredModel().getTransactionState() != null) {
            return getStoredModel().getTransactionState();
        }

        return TransactionState.Confirmed;
    }

    public void setTransactionState(@Nullable TransactionState transactionState) {
        this.transactionState = transactionState;
        isTransactionStateSet = true;
    }

    public boolean getIncludeInReports() {
        if (includeInReports != null) {
            return includeInReports;
        }

        return getStoredModel() == null || getStoredModel().includeInReports();
    }

    public void setIncludeInReports(@Nullable Boolean includeInReports) {
        this.includeInReports = includeInReports;
        isIncludeInReportsSet = true;
    }

    public double getExchangeRate() {
        double exchangeRate;

        if (this.exchangeRate != null) {
            exchangeRate = this.exchangeRate;
        } else if (getStoredModel() != null) {
            exchangeRate = getStoredModel().getExchangeRate();
        } else {
            exchangeRate = 1;
        }

        if (Double.compare(exchangeRate, 0) <= 0) {
            exchangeRate = 1;
        }

        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        if (Double.compare(exchangeRate, 0) <= 0) {
            exchangeRate = 1.0;
        }
        this.exchangeRate = exchangeRate;
        isExchangeRateSet = true;
    }

    public boolean isTransactionTypeSet() {
        return isTransactionTypeSet;
    }

    public boolean isAmountSet() {
        return isAmountSet;
    }

    public boolean isDateSet() {
        return isDateSet;
    }

    public boolean isAccountFromSet() {
        return isAccountFromSet;
    }

    public boolean isAccountToSet() {
        return isAccountToSet;
    }

    public boolean isCategorySet() {
        return isCategorySet;
    }

    public boolean isTagsSet() {
        return isTagsSet;
    }

    public boolean isNoteSet() {
        return isNoteSet;
    }

    public boolean isTransactionStateSet() {
        return isTransactionStateSet;
    }

    public boolean isIncludeInReportsSet() {
        return isIncludeInReportsSet;
    }

    public boolean isExchangeRateSet() {
        return isExchangeRateSet;
    }

    private void onTransactionTypeChanged() {
        accountFrom = null;
        accountTo = null;
        category = null;
        tags = null;
        note = null;
    }
}
