package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.backend.endpoint.transactions.model.TransactionEntity;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;

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
    private TransactionState transactionState;
    private boolean includeInReports;

    public Transaction() {
        super();
        setAccountFrom(Account.getSystem());
        setAccountTo(Account.getSystem());
        setCategory(Category.getExpense());
        setDate(0);
        setAmount(0);
        setExchangeRate(1.0);
        setNote(null);
        setTransactionState(TransactionState.CONFIRMED);
        setIncludeInReports(true);
    }

    public Transaction(Parcel in) {
        super(in);
    }

    public static Transaction from(Cursor cursor) {
        final Transaction transaction = new Transaction();
        if (cursor.getCount() > 0) {
            transaction.updateFrom(cursor, null);
        }
        return transaction;
    }

    public static Transaction from(TransactionEntity entity, Account accountFrom, Account accountTo, Category category) {
        final Transaction transaction = new Transaction();
        transaction.setServerId(entity.getId());
        transaction.setModelState(ModelState.valueOf(entity.getModelState()));
        transaction.setSyncState(SyncState.SYNCED);
        transaction.setAccountFrom(accountFrom);
        transaction.setAccountTo(accountTo);
        transaction.setCategory(category);
        transaction.setDate(entity.getDate());
        transaction.setAmount(entity.getAmount());
        transaction.setExchangeRate(entity.getExchangeRate());
        transaction.setNote(entity.getNote());
        transaction.setTransactionState(TransactionState.valueOf(entity.getTransactionState()));
        transaction.setIncludeInReports(entity.getIncludeInReports());
        return transaction;
    }

    @Override
    protected Column getIdColumn() {
        return Tables.Transactions.ID;
    }

    @Override
    protected Column getServerIdColumn() {
        return Tables.Transactions.SERVER_ID;
    }

    @Override
    protected Column getModelStateColumn() {
        return Tables.Transactions.MODEL_STATE;
    }

    @Override
    protected Column getSyncStateColumn() {
        return Tables.Transactions.SYNC_STATE;
    }

    @Override
    protected void fromParcel(Parcel parcel) {
        setAccountFrom((Account) parcel.readParcelable(Account.class.getClassLoader()));
        setAccountTo((Account) parcel.readParcelable(Account.class.getClassLoader()));
        setCategory((Category) parcel.readParcelable(Category.class.getClassLoader()));
        setDate(parcel.readLong());
        setAmount(parcel.readLong());
        setExchangeRate(parcel.readDouble());
        setNote(parcel.readString());
        setTransactionState(TransactionState.fromInt(parcel.readInt()));
        setIncludeInReports(parcel.readInt() != 0);
    }

    @Override
    protected void toParcel(Parcel parcel) {
        parcel.writeParcelable(getAccountFrom(), 0);
        parcel.writeParcelable(getAccountTo(), 0);
        parcel.writeParcelable(getCategory(), 0);
        parcel.writeLong(getDate());
        parcel.writeLong(getAmount());
        parcel.writeDouble(getExchangeRate());
        parcel.writeString(getNote());
        parcel.writeInt(getTransactionState().asInt());
        parcel.writeInt(includeInReports() ? 1 : 0);
    }

    @Override
    protected void toValues(ContentValues values) {
        values.put(Tables.Transactions.ACCOUNT_FROM_ID.getName(), accountFrom.getServerId());
        values.put(Tables.Transactions.ACCOUNT_TO_ID.getName(), accountTo.getServerId());
        values.put(Tables.Transactions.CATEGORY_ID.getName(), category.getServerId());
        values.put(Tables.Transactions.DATE.getName(), date);
        values.put(Tables.Transactions.AMOUNT.getName(), amount);
        values.put(Tables.Transactions.EXCHANGE_RATE.getName(), exchangeRate);
        values.put(Tables.Transactions.NOTE.getName(), note);
        values.put(Tables.Transactions.STATE.getName(), transactionState.asInt());
        values.put(Tables.Transactions.INCLUDE_IN_REPORTS.getName(), includeInReports);
    }

    @Override
    protected void fromCursor(Cursor cursor, String columnPrefixTable) {
        int index;

        // Account from
        final Account accountFrom = Account.fromAccountFrom(cursor);
        accountFrom.setId(0);
        setAccountFrom(accountFrom);

        // Account to
        final Account accountTo = Account.fromAccountTo(cursor);
        accountTo.setId(0);
        setAccountTo(accountTo);

        // Category
        final Category category = Category.from(cursor);
        category.setId(0);
        setCategory(category);

        // Date
        index = cursor.getColumnIndex(Tables.Transactions.DATE.getName(columnPrefixTable));
        if (index >= 0) {
            setDate(cursor.getLong(index));
        }

        // Amount
        index = cursor.getColumnIndex(Tables.Transactions.AMOUNT.getName(columnPrefixTable));
        if (index >= 0) {
            setAmount(cursor.getLong(index));
        }

        // Exchange rate
        index = cursor.getColumnIndex(Tables.Transactions.EXCHANGE_RATE.getName(columnPrefixTable));
        if (index >= 0) {
            setExchangeRate(cursor.getDouble(index));
        }

        // Note
        index = cursor.getColumnIndex(Tables.Transactions.NOTE.getName(columnPrefixTable));
        if (index >= 0) {
            setNote(cursor.getString(index));
        }

        // Transaction state
        index = cursor.getColumnIndex(Tables.Transactions.STATE.getName(columnPrefixTable));
        if (index >= 0) {
            setTransactionState(TransactionState.fromInt(cursor.getInt(index)));
        }

        // Include in reports
        index = cursor.getColumnIndex(Tables.Transactions.INCLUDE_IN_REPORTS.getName(columnPrefixTable));
        if (index >= 0) {
            setIncludeInReports(cursor.getInt(index) != 0);
        }
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

        if (transactionState == null) {
            throw new IllegalStateException("State cannot be null.");
        }
    }

    public TransactionEntity toEntity() {
        final TransactionEntity entity = new TransactionEntity();
        entity.setId(getServerId());
        entity.setModelState(getModelState().toString());
        entity.setAccountFromId(getAccountFrom().getServerId());
        entity.setAccountToId(getAccountTo().getServerId());
        entity.setCategoryId(getCategory().getServerId());
        entity.setDate(getDate());
        entity.setAmount(getAmount());
        entity.setExchangeRate(getExchangeRate());
        entity.setNote(getNote());
        entity.setTransactionState(getTransactionState().toString());
        entity.setIncludeInReports(includeInReports());
        return entity;
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

    public TransactionState getTransactionState() {
        return transactionState;
    }

    public void setTransactionState(TransactionState transactionState) {
        this.transactionState = transactionState;
    }

    public boolean includeInReports() {
        return includeInReports;
    }

    public void setIncludeInReports(boolean includeInReports) {
        this.includeInReports = includeInReports;
    }
}
