package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;

public class Account extends Model {
    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    private String currencyCode;
    private String title;
    private String note;
    private long balance;
    private boolean includeInTotals;

    public Account() {
        super();
        setCurrencyCode(null);
        setTitle(null);
        setNote(null);
        setBalance(0);
        setIncludeInTotals(true);
    }

    public Account(Parcel parcel) {
        super(parcel);
        setCurrencyCode(parcel.readString());
        setTitle(parcel.readString());
        setNote(parcel.readString());
        setBalance(parcel.readLong());
        setIncludeInTotals(parcel.readInt() != 0);
    }

    public static Account from(Cursor cursor) {
        final Account account = new Account();
        if (cursor.getCount() > 0) {
            account.updateFromCursor(cursor, null);
        }
        return account;
    }

    public static Account fromAccountFrom(Cursor cursor) {
        final Account account = new Account();
        if (cursor.getCount() > 0) {
            account.updateFromCursor(cursor, Tables.Accounts.TEMP_TABLE_NAME_FROM_ACCOUNT);
        }
        return account;
    }

    public static Account fromAccountTo(Cursor cursor) {
        final Account account = new Account();
        if (cursor.getCount() > 0) {
            account.updateFromCursor(cursor, Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT);
        }
        return account;
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(currencyCode);
        parcel.writeString(title);
        parcel.writeString(note);
        parcel.writeLong(balance);
        parcel.writeInt(includeInTotals ? 1 : 0);
    }

    @Override public ContentValues asContentValues() {
        final ContentValues values = super.asContentValues();
        values.put(Tables.Accounts.CURRENCY_CODE.getName(), currencyCode);
        values.put(Tables.Accounts.TITLE.getName(), title);
        values.put(Tables.Accounts.NOTE.getName(), note);
        values.put(Tables.Accounts.BALANCE.getName(), balance);
        values.put(Tables.Accounts.INCLUDE_IN_TOTALS.getName(), includeInTotals);
        return values;
    }

    @Override public void prepareForContentValues() {
        super.prepareForContentValues();

        if (note == null) {
            note = "";
        }
    }

    @Override public void validateForContentValues() throws IllegalStateException {
        super.validateForContentValues();
        Preconditions.notNull(currencyCode, "Currency cannot be null.");
        Preconditions.lengthEquals(currencyCode, 3, "Currency code must be 3 characters long.");
        Preconditions.notEmpty(title, "Title cannot be empty.");
        Preconditions.notNull(note, "Note cannot be null.");
    }

    @Override public void updateFromCursor(Cursor cursor, String columnPrefixTable) {
        super.updateFromCursor(cursor, columnPrefixTable);
        int index;

        // Currency code
        index = cursor.getColumnIndex(Tables.Accounts.CURRENCY_CODE.getName(columnPrefixTable));
        if (index >= 0) {
            setCurrencyCode(cursor.getString(index));
        }

        // Title
        index = cursor.getColumnIndex(Tables.Accounts.TITLE.getName(columnPrefixTable));
        if (index >= 0) {
            setTitle(cursor.getString(index));
        }

        // Note
        index = cursor.getColumnIndex(Tables.Accounts.NOTE.getName(columnPrefixTable));
        if (index >= 0) {
            setNote(cursor.getString(index));
        }

        // Balance
        index = cursor.getColumnIndex(Tables.Accounts.BALANCE.getName(columnPrefixTable));
        if (index >= 0) {
            setBalance(cursor.getLong(index));
        }

        // Include in totals
        index = cursor.getColumnIndex(Tables.Accounts.INCLUDE_IN_TOTALS.getName(columnPrefixTable));
        if (index >= 0) {
            setIncludeInTotals(cursor.getInt(index) != 0);
        }
    }

    @Override protected Column getLocalIdColumn() {
        return Tables.Accounts.LOCAL_ID;
    }

    @Override protected Column getIdColumn() {
        return Tables.Accounts.ID;
    }

    @Override protected Column getModelStateColumn() {
        return Tables.Accounts.MODEL_STATE;
    }

    @Override protected Column getSyncStateColumn() {
        return Tables.Accounts.SYNC_STATE;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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

    public boolean includeInTotals() {
        return includeInTotals;
    }

    public void setIncludeInTotals(boolean includeInTotals) {
        this.includeInTotals = includeInTotals;
    }
}