package com.code44.finance.ui.accounts.edit;

import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.data.model.Account;
import com.code44.finance.ui.common.activities.ModelEditActivity;

class AccountEditData extends ModelEditActivity.ModelEditData<Account> {
    public static final Parcelable.Creator<AccountEditData> CREATOR = new Parcelable.Creator<AccountEditData>() {
        public AccountEditData createFromParcel(Parcel in) {
            return new AccountEditData(in);
        }

        public AccountEditData[] newArray(int size) {
            return new AccountEditData[size];
        }
    };

    private String title;
    private String currencyCode;
    private Long balance;
    private String note;
    private Boolean includeInTotals;

    public AccountEditData() {
        super();
    }

    private AccountEditData(Parcel in) {
        super(in);
        title = in.readString();
        currencyCode = in.readString();
        balance = (Long) in.readSerializable();
        note = in.readString();
        includeInTotals = (Boolean) in.readSerializable();
    }

    @Override public Account createModel() {
        final Account account = new Account();
        account.setId(getId());
        account.setTitle(getTitle());
        account.setCurrencyCode(getCurrencyCode());
        account.setBalance(getBalance());
        account.setNote(getNote());
        account.setIncludeInTotals(isIncludeInTotals());
        return account;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(currencyCode);
        dest.writeSerializable(balance);
        dest.writeString(note);
        dest.writeSerializable(includeInTotals);
    }

    public String getTitle() {
        if (title != null) {
            return title;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getTitle();
        }

        return null;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurrencyCode() {
        if (currencyCode != null) {
            return currencyCode;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getCurrencyCode();
        }

        return null;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public long getBalance() {
        if (balance != null) {
            return balance;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getBalance();
        }

        return 0;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getNote() {
        if (note != null) {
            return note;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getNote();
        }

        return "";
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean isIncludeInTotals() {
        if (includeInTotals != null) {
            return includeInTotals;
        }

        return getStoredModel() == null || getStoredModel().includeInTotals();
    }

    public void setIncludeInTotals(Boolean includeInTotals) {
        this.includeInTotals = includeInTotals;
    }
}
