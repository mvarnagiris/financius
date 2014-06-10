package com.code44.finance.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.db.Column;
import com.code44.finance.db.Tables;

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

    @Override
    protected Column getIdColumn() {
        return Tables.Transactions.ID;
    }

    @Override
    protected Column getItemStateColumn() {
        return Tables.Transactions.ITEM_STATE;
    }

    @Override
    protected Column getSyncStateColumn() {
        return Tables.Transactions.SYNC_STATE;
    }

    @Override
    public ContentValues asContentValues() {
        final ContentValues values = super.asContentValues();

        values.put(Tables.Transactions.ACCOUNT_FROM_ID.getName(), accountFrom.getId());
        values.put(Tables.Transactions.ACCOUNT_TO_ID.getName(), accountTo.getId());
        values.put(Tables.Transactions.CATEGORY_ID.getName(), category.getId());
        values.put(Tables.Transactions.DATE.getName(), date);
        values.put(Tables.Transactions.AMOUNT.getName(), amount);
        values.put(Tables.Transactions.EXCHANGE_RATE.getName(), exchangeRate);
        values.put(Tables.Transactions.NOTE.getName(), note);

        return values;
    }

    @Override
    protected void updateFrom(Cursor cursor) {
        super.updateFrom(cursor);

        int index;

        final Account accountFrom = Account.fromAccountFrom(cursor);
        index = cursor.getColumnIndex(Tables.Transactions.ACCOUNT_FROM_ID.getName());
        if (index >= 0) {
            accountFrom.setId(cursor.getLong(index));
        } else {
            accountFrom.setId(0);
        }
        setAccountFrom(accountFrom);

        final Account accountTo = Account.fromAccountTo(cursor);
        index = cursor.getColumnIndex(Tables.Transactions.ACCOUNT_TO_ID.getName());
        if (index >= 0) {
            accountTo.setId(cursor.getLong(index));
        } else {
            accountTo.setId(0);
        }
        setAccountTo(accountTo);

        final Category category = Category.from(cursor);
        index = cursor.getColumnIndex(Tables.Transactions.CATEGORY_ID.getName());
        if (index >= 0) {
            category.setId(cursor.getLong(index));
        } else {
            category.setId(0);
        }
        setCategory(category);

        index = cursor.getColumnIndex(Tables.Transactions.DATE.getName());
        if (index >= 0) {
            setDate(cursor.getLong(index));
        }

        index = cursor.getColumnIndex(Tables.Transactions.AMOUNT.getName());
        if (index >= 0) {
            setAmount(cursor.getLong(index));
        }

        index = cursor.getColumnIndex(Tables.Transactions.EXCHANGE_RATE.getName());
        if (index >= 0) {
            setExchangeRate(cursor.getDouble(index));
        }

        index = cursor.getColumnIndex(Tables.Transactions.NOTE.getName());
        if (index >= 0) {
            setNote(cursor.getString(index));
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
