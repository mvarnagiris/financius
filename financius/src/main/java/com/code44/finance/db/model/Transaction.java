package com.code44.finance.db.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaction extends BaseModel {
    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    private Account accountFrom;
    private Account accountTo;
    private Category category;
    private long date;
    private long amount;
    private double exchangeRate;
    private String note;

    public Transaction() {
        super();
        setAccountFrom(Account.getSystem());
        setAccountTo(Account.getSystem());
        setCategory(Category.getExpense());
        setDate(0);
        setAmount(0);
        setExchangeRate(1.0);
        setNote(null);
    }

    public Transaction(Parcel in) {
        super(in);
        setAccountFrom((Account) in.readParcelable(Account.class.getClassLoader()));
        setAccountTo((Account) in.readParcelable(Account.class.getClassLoader()));
        setCategory((Category) in.readParcelable(Category.class.getClassLoader()));
        setDate(in.readLong());
        setAmount(in.readLong());
        setExchangeRate(in.readDouble());
        setNote(in.readString());
    }

    @Override
    public void checkValues() throws IllegalStateException {
        super.checkValues();

        if (accountFrom == null) {
            throw new IllegalStateException("AccountFrom cannot be null.");
        }

        if (accountTo == null) {
            throw new IllegalStateException("AccountTo cannot be null.");
        }

        if (accountFrom == accountTo) {
            throw new IllegalStateException("AccountFrom cannot be equal to AccountTo.");
        }

        if (category == null) {
            throw new IllegalStateException("Category cannot be null.");
        }

        if (Double.compare(exchangeRate, 0) < 0) {
            throw new IllegalStateException("Exchange rate must be > 0.");
        }
    }

    public Account getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(Account accountFrom) {
        this.accountFrom = accountFrom;
    }

    public Account getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(Account accountTo) {
        this.accountTo = accountTo;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
