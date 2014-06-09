package com.code44.finance.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.App;
import com.code44.finance.providers.AccountsProvider;

import nl.qbusict.cupboard.CupboardFactory;

public class Account extends BaseModel {
    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    private static Account systemAccount;

    private Currency currency;
    private String title;
    private String note;
    private long balance;
    private Owner owner;

    public Account() {
        super();
        setCurrency(Currency.getDefault());
        setTitle(null);
        setNote(null);
        setBalance(0);
        setOwner(Owner.USER);
    }

    public Account(Parcel in) {
        super(in);
        setCurrency((Currency) in.readParcelable(Currency.class.getClassLoader()));
        setTitle(in.readString());
        setNote(in.readString());
        setBalance(in.readLong());
        setOwner(Owner.fromInt(in.readInt()));
    }

    public static Account getSystem() {
        if (systemAccount == null) {
            systemAccount = CupboardFactory.cupboard().withContext(App.getAppContext())
                    .query(AccountsProvider.uriAccounts(), Account.class)
                    .withSelection("owner=?", Owner.SYSTEM.toString()).get();
        }
        return systemAccount;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(getCurrency(), flags);
        dest.writeString(getTitle());
        dest.writeString(getNote());
        dest.writeLong(getBalance());
        dest.writeInt(getOwner().asInt());
    }

    @Override
    public void checkValues() throws IllegalStateException {
        super.checkValues();

        if (currency == null) {
            throw new IllegalStateException("Currency cannot be null.");
        }

        if (owner == null) {
            throw new IllegalStateException("Owner cannot be null.");
        }
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public static enum Owner {
        SYSTEM(Owner.VALUE_SYSTEM), USER(Owner.VALUE_USER);

        private static final int VALUE_SYSTEM = 1;
        private static final int VALUE_USER = 2;

        private final int value;

        private Owner(int value) {
            this.value = value;
        }

        public static Owner fromInt(int value) {
            switch (value) {
                case VALUE_SYSTEM:
                    return SYSTEM;

                case VALUE_USER:
                    return USER;

                default:
                    throw new IllegalArgumentException("Value " + value + " is not supported.");
            }
        }

        public int asInt() {
            return value;
        }
    }
}
