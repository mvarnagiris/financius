package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.code44.finance.backend.financius.model.TransactionEntity;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.crashlytics.android.Crashlytics;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class Transaction extends Model<TransactionEntity> {
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
    private List<Tag> tags;
    private long date;
    private long amount;
    private double exchangeRate;
    private String note;
    private TransactionState transactionState;
    private TransactionType transactionType;
    private boolean includeInReports;

    public Transaction() {
        super();
        setAccountFrom(null);
        setAccountTo(null);
        setCategory(null);
        setTags(null);
        setDate(System.currentTimeMillis());
        setAmount(0);
        setExchangeRate(1.0);
        setNote(null);
        setTransactionState(TransactionState.Confirmed);
        setTransactionType(TransactionType.Expense);
        setIncludeInReports(true);
    }

    public Transaction(Parcel parcel) {
        super(parcel);
        setAccountFrom((Account) parcel.readParcelable(Account.class.getClassLoader()));
        setAccountTo((Account) parcel.readParcelable(Account.class.getClassLoader()));
        setCategory((Category) parcel.readParcelable(Category.class.getClassLoader()));
        tags = new ArrayList<>();
        parcel.readTypedList(tags, Tag.CREATOR);
        setDate(parcel.readLong());
        setAmount(parcel.readLong());
        setExchangeRate(parcel.readDouble());
        setNote(parcel.readString());
        setTransactionState(TransactionState.fromInt(parcel.readInt()));
        setTransactionType(TransactionType.fromInt(parcel.readInt()));
        setIncludeInReports(parcel.readInt() != 0);
    }

    public static Transaction from(Cursor cursor) {
        final Transaction transaction = new Transaction();
        if (cursor.getCount() > 0) {
            try {
                transaction.updateFromCursor(cursor, null);
            } catch (Exception e) {
                Crashlytics.log(DatabaseUtils.dumpCurrentRowToString(cursor));
                throw e;
            }
        }
        return transaction;
    }

    public static Transaction from(TransactionEntity entity) {
        final Transaction transaction = new Transaction();
        transaction.updateFromEntity(entity);
        return transaction;
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeParcelable(accountFrom, 0);
        parcel.writeParcelable(accountTo, 0);
        parcel.writeParcelable(category, 0);
        parcel.writeTypedList(tags);
        parcel.writeLong(date);
        parcel.writeLong(amount);
        parcel.writeDouble(exchangeRate);
        parcel.writeString(note);
        parcel.writeInt(transactionState.asInt());
        parcel.writeInt(transactionType.asInt());
        parcel.writeInt(includeInReports ? 1 : 0);
    }

    @Override public ContentValues asContentValues() {
        final ContentValues values = super.asContentValues();
        values.put(Tables.Transactions.ACCOUNT_FROM_ID.getName(), accountFrom == null ? null : accountFrom.getId());
        values.put(Tables.Transactions.ACCOUNT_TO_ID.getName(), accountTo == null ? null : accountTo.getId());
        values.put(Tables.Transactions.CATEGORY_ID.getName(), category == null ? null : category.getId());
        values.put(Tables.Transactions.DATE.getName(), date);
        values.put(Tables.Transactions.AMOUNT.getName(), amount);
        values.put(Tables.Transactions.EXCHANGE_RATE.getName(), exchangeRate);
        values.put(Tables.Transactions.NOTE.getName(), note);
        values.put(Tables.Transactions.STATE.getName(), transactionState.asInt());
        values.put(Tables.Transactions.TYPE.getName(), transactionType.asInt());
        values.put(Tables.Transactions.INCLUDE_IN_REPORTS.getName(), includeInReports);
        final StringBuilder sb = new StringBuilder();
        for (Tag tag : tags) {
            if (sb.length() > 0) {
                sb.append(Tables.CONCAT_SEPARATOR);
            }
            sb.append(tag.getId());
        }
        values.put(Tables.Tags.ID.getName(), sb.toString());
        return values;
    }

    @Override public TransactionEntity asEntity() {
        final TransactionEntity entity = super.asEntity();
        entity.setAccountFromId(accountFrom != null ? accountFrom.getId() : null);
        entity.setAccountToId(accountTo != null ? accountTo.getId() : null);
        entity.setCategoryId(category != null ? category.getId() : null);
        entity.setTagsIds(new ArrayList<>(Collections2.transform(tags, new Function<Tag, String>() {
            @Nullable @Override public String apply(@Nullable Tag input) {
                if (input == null) {
                    return null;
                }
                return input.getId();
            }
        })));
        entity.setDate(date);
        entity.setAmount(amount);
        entity.setExchangeRate(exchangeRate);
        entity.setNote(note);
        entity.setTransactionState(transactionState.name());
        entity.setTransactionType(transactionType.name());
        entity.setIncludeInReports(includeInReports);
        return entity;
    }

    @Override public void prepareForContentValues() {
        super.prepareForContentValues();

        if (tags == null) {
            tags = Collections.emptyList();
        }

        if (amount < 0) {
            amount = 0;
        }

        if (Double.compare(exchangeRate, 0) < 0) {
            exchangeRate = 1.0;
        }

        if (note == null) {
            note = "";
        }

        if (transactionState == null) {
            transactionState = TransactionState.Pending;
        }

        if (transactionType == null) {
            transactionType = TransactionType.Expense;
        }

        switch (transactionType) {
            case Expense:
                accountTo = null;
                exchangeRate = 1.0;
                break;
            case Income:
                accountFrom = null;
                exchangeRate = 1.0;
                break;
            case Transfer:
                category = null;
                break;
        }
    }

    @Override public void validateForContentValues() throws IllegalStateException {
        super.validateForContentValues();
        checkNotNull(transactionState, "Transaction state cannot be null.");
        checkNotNull(transactionType, "Transaction type cannot be null.");
        checkState(amount >= 0, "Amount must be >= 0.");
        checkNotNull(note, "Note cannot be null.");

        switch (transactionType) {
            case Expense:
                if (transactionState == TransactionState.Confirmed) {
                    checkNotNull(accountFrom, "AccountFrom cannot be null.");
                    checkState(accountFrom.hasId(), "AccountFrom must have an Id.");
                    checkState(Double.compare(exchangeRate, 1) == 0, "Exchange rate must be 1.0.");
                }
                checkState(accountTo == null, "AccountTo must be null.");
                break;
            case Income:
                if (transactionState == TransactionState.Confirmed) {
                    checkNotNull(accountTo, "AccountTo cannot be null.");
                    checkState(accountTo.hasId(), "AccountTo must have an Id.");
                    checkState(Double.compare(exchangeRate, 1) == 0, "Exchange rate must be 1.0.");
                }
                checkState(accountFrom == null, "AccountFrom must be null.");
                break;
            case Transfer:
                if (transactionState == TransactionState.Confirmed) {
                    checkNotNull(accountFrom, "AccountFrom cannot be null.");
                    checkState(accountFrom.hasId(), "AccountFrom must have an Id.");
                    checkNotNull(accountTo, "AccountTo cannot be null.");
                    checkState(accountTo.hasId(), "AccountTo must have an Id.");
                    checkState(Double.compare(exchangeRate, 0) > 0, "Exchange rate must be > 0.");

                    if (accountFrom.equals(accountTo)) {
                        throw new IllegalStateException("AccountFrom cannot be equal to AccountTo.");
                    }
                }
                checkState(category == null, "Transfer cannot have a category.");
                break;
            default:
                throw new IllegalArgumentException("Transaction type " + transactionType + " is not supported.");
        }

        if (Double.compare(exchangeRate, 0) < 0) {
            throw new IllegalStateException("Exchange rate must be > 0.");
        }
    }

    @Override public void updateFromCursor(Cursor cursor, String columnPrefixTable) {
        super.updateFromCursor(cursor, columnPrefixTable);
        int index;

        // Transaction type
        index = cursor.getColumnIndex(Tables.Transactions.TYPE.getName(columnPrefixTable));
        if (index >= 0) {
            setTransactionType(TransactionType.fromInt(cursor.getInt(index)));
        }

        // Account from
        index = cursor.getColumnIndex(Tables.Transactions.ACCOUNT_FROM_ID.getName(columnPrefixTable));
        if (index >= 0 && !Strings.isNullOrEmpty(cursor.getString(index)) && !cursor.getString(index).equals("null")) {
            final Account accountFrom = Account.fromAccountFrom(cursor);
            accountFrom.setId(cursor.getString(index));
            setAccountFrom(accountFrom);
        } else {
            setAccountFrom(null);
        }

        // Account to
        index = cursor.getColumnIndex(Tables.Transactions.ACCOUNT_TO_ID.getName(columnPrefixTable));
        if (index >= 0 && !Strings.isNullOrEmpty(cursor.getString(index)) && !cursor.getString(index).equals("null")) {
            final Account accountTo = Account.fromAccountTo(cursor);
            accountTo.setId(cursor.getString(index));
            setAccountTo(accountTo);
        } else {
            setAccountTo(null);
        }

        // Category
        index = cursor.getColumnIndex(Tables.Transactions.CATEGORY_ID.getName(columnPrefixTable));
        if (index >= 0 && !Strings.isNullOrEmpty(cursor.getString(index)) && !cursor.getString(index).equals("null")) {
            final Category category = Category.from(cursor);
            category.setId(cursor.getString(index));
            setCategory(category);
        } else {
            setCategory(null);
        }

        // Tags
        final String[] tagIds;
        final String[] tagTitles;

        index = cursor.getColumnIndex(Tables.Tags.ID.getName(columnPrefixTable));
        if (index >= 0) {
            final String str = cursor.getString(index);
            if (!Strings.isNullOrEmpty(str)) {
                tagIds = TextUtils.split(str, Tables.CONCAT_SEPARATOR);
            } else {
                tagIds = null;
            }
        } else {
            tagIds = null;
        }

        index = cursor.getColumnIndex(Tables.Tags.TITLE.getName(columnPrefixTable));
        if (index >= 0) {
            final String str = cursor.getString(index);
            if (!Strings.isNullOrEmpty(str)) {
                tagTitles = TextUtils.split(str, Tables.CONCAT_SEPARATOR);
            } else {
                tagTitles = null;
            }
        } else {
            tagTitles = null;
        }

        if (tagIds != null || tagTitles != null) {
            final List<Tag> tags = new ArrayList<>();
            final int count = tagIds != null ? tagIds.length : tagTitles.length;
            for (int i = 0; i < count; i++) {
                final Tag tag = new Tag();
                if (tagIds != null) {
                    tag.setId(tagIds[i]);
                }

                if (tagTitles != null) {
                    tag.setTitle(tagTitles[i]);
                }
                tags.add(tag);
            }
            setTags(tags);
        } else {
            setTags(null);
        }

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

    @Override public void updateFromEntity(TransactionEntity entity) {
        super.updateFromEntity(entity);
        setAccountFrom(Strings.isNullOrEmpty(entity.getAccountFromId()) ? null : (Account) new Account().setId(entity.getAccountFromId()));
        setAccountTo(Strings.isNullOrEmpty(entity.getAccountToId()) ? null : (Account) new Account().setId(entity.getAccountToId()));
        setCategory(Strings.isNullOrEmpty(entity.getCategoryId()) ? null : (Category) new Category().setId(entity.getCategoryId()));
        if (entity.getTagsIds() == null) {
            setTags(null);
        } else {
            setTags(new ArrayList<>(Collections2.transform(entity.getTagsIds(), new Function<String, Tag>() {
                @Nullable @Override public Tag apply(@Nullable String input) {
                    return (Tag) new Tag().setId(input);
                }
            })));
        }
        setDate(entity.getDate());
        setAmount(entity.getAmount());
        setExchangeRate(entity.getExchangeRate());
        setNote(entity.getNote());
        setTransactionState(TransactionState.valueOf(entity.getTransactionState()));
        setTransactionType(TransactionType.valueOf(entity.getTransactionType()));
        setIncludeInReports(entity.getIncludeInReports());
    }

    @Override protected Column getIdColumn() {
        return Tables.Transactions.ID;
    }

    @Override protected Column getModelStateColumn() {
        return Tables.Transactions.MODEL_STATE;
    }

    @Override protected Column getSyncStateColumn() {
        return Tables.Transactions.SYNC_STATE;
    }

    @Override protected Column getLocalIdColumn() {
        return Tables.Transactions.LOCAL_ID;
    }

    @Override protected TransactionEntity createEntity() {
        return new TransactionEntity();
    }

    @Override public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Account from", accountFrom)
                .add("Account to", accountTo)
                .add("Category", category)
                .add("Tags", tags)
                .add("Date", date)
                .add("Amount", amount)
                .add("Exchange rate", exchangeRate)
                .add("Note", note)
                .add("Transaction state", transactionState)
                .add("Transaction type", transactionType)
                .add("Include in reports", includeInReports)
                .toString();
    }

    public Account getAccountFrom() {
        return accountFrom;
    }

    public Transaction setAccountFrom(Account accountFrom) {
        this.accountFrom = accountFrom;
        return this;
    }

    public Account getAccountTo() {
        return accountTo;
    }

    public Transaction setAccountTo(Account accountTo) {
        this.accountTo = accountTo;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public Transaction setCategory(Category category) {
        this.category = category;
        return this;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public Transaction setTags(List<Tag> tags) {
        this.tags = tags == null ? Collections.<Tag>emptyList() : tags;
        return this;
    }

    public long getDate() {
        return date;
    }

    public Transaction setDate(long date) {
        this.date = date;
        return this;
    }

    public long getAmount() {
        return amount;
    }

    public Transaction setAmount(long amount) {
        this.amount = amount;
        return this;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public Transaction setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
        return this;
    }

    public String getNote() {
        return note;
    }

    public Transaction setNote(String note) {
        this.note = note;
        return this;
    }

    public TransactionState getTransactionState() {
        return transactionState;
    }

    public Transaction setTransactionState(TransactionState transactionState) {
        this.transactionState = transactionState;
        return this;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Transaction setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public boolean includeInReports() {
        return includeInReports;
    }

    public Transaction setIncludeInReports(boolean includeInReports) {
        this.includeInReports = includeInReports;
        return this;
    }
}
