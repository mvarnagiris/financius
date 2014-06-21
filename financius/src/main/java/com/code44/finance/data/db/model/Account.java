package com.code44.finance.data.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.code44.finance.App;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.utils.IOUtils;

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
            final Cursor cursor = Query.get()
                    .projectionId(Tables.Accounts.ID)
                    .projection(Tables.Accounts.PROJECTION)
                    .selection(Tables.Accounts.OWNER.getName() + "=?", String.valueOf(Owner.SYSTEM.asInt()))
                    .from(App.getAppContext(), AccountsProvider.uriAccounts())
                    .execute();

            systemAccount = Account.from(cursor);
            IOUtils.closeQuietly(cursor);
        }
        return systemAccount;
    }

    public static Account from(Cursor cursor) {
        final Account account = new Account();
        if (cursor.getCount() > 0) {
            account.updateFrom(cursor, null);
        }
        return account;
    }

    public static Account fromAccountFrom(Cursor cursor) {
        final Account account = new Account();
        if (cursor.getCount() > 0) {
            account.updateFrom(cursor, Tables.Accounts.TEMP_TABLE_NAME_FROM_ACCOUNT);
        }
        return account;
    }

    public static Account fromAccountTo(Cursor cursor) {
        final Account account = new Account();
        if (cursor.getCount() > 0) {
            account.updateFrom(cursor, Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT);
        }
        return account;
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

    @Override
    protected Column getIdColumn() {
        return Tables.Accounts.ID;
    }

    @Override
    protected Column getServerIdColumn() {
        return Tables.Accounts.SERVER_ID;
    }

    @Override
    protected Column getItemStateColumn() {
        return Tables.Accounts.ITEM_STATE;
    }

    @Override
    protected Column getSyncStateColumn() {
        return Tables.Accounts.SYNC_STATE;
    }

    @Override
    public ContentValues asContentValues() {
        final ContentValues values = super.asContentValues();

        values.put(Tables.Accounts.CURRENCY_ID.getName(), currency.getId());
        values.put(Tables.Accounts.TITLE.getName(), title);
        values.put(Tables.Accounts.NOTE.getName(), note);
        values.put(Tables.Accounts.BALANCE.getName(), balance);
        values.put(Tables.Accounts.OWNER.getName(), owner.asInt());

        return values;
    }

    @Override
    protected void updateFrom(Cursor cursor, String columnPrefixTable) {
        super.updateFrom(cursor, columnPrefixTable);

        int index;

        Currency currency;
        if (TextUtils.isEmpty(columnPrefixTable)) {
            currency = Currency.from(cursor);
        } else if (columnPrefixTable.equals(Tables.Currencies.TEMP_TABLE_NAME_FROM_CURRENCY)) {
            currency = Currency.fromCurrencyFrom(cursor);
        } else if (columnPrefixTable.equals(Tables.Currencies.TEMP_TABLE_NAME_TO_CURRENCY)) {
            currency = Currency.fromCurrencyTo(cursor);
        } else {
            throw new IllegalArgumentException("Table prefix " + columnPrefixTable + " is not supported.");
        }
        index = cursor.getColumnIndex(Tables.Accounts.CURRENCY_ID.getName(columnPrefixTable));
        if (index >= 0) {
            currency.setId(cursor.getLong(index));
        } else {
            currency.setId(0);
        }
        setCurrency(currency);

        index = cursor.getColumnIndex(Tables.Accounts.TITLE.getName(columnPrefixTable));
        if (index >= 0) {
            setTitle(cursor.getString(index));
        }

        index = cursor.getColumnIndex(Tables.Accounts.NOTE.getName(columnPrefixTable));
        if (index >= 0) {
            setNote(cursor.getString(index));
        }

        index = cursor.getColumnIndex(Tables.Accounts.BALANCE.getName(columnPrefixTable));
        if (index >= 0) {
            setBalance(cursor.getInt(index));
        }

        index = cursor.getColumnIndex(Tables.Accounts.OWNER.getName(columnPrefixTable));
        if (index >= 0) {
            setOwner(Owner.fromInt(cursor.getInt(index)));
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

        public String asString() {
            return String.valueOf(value);
        }
    }
}
