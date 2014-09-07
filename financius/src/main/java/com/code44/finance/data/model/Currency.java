package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.backend.endpoint.currencies.model.CurrencyEntity;
import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.SymbolPosition;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.code44.finance.utils.IOUtils;

public class Currency extends BaseModel<CurrencyEntity> {
    public static final Parcelable.Creator<Currency> CREATOR = new Parcelable.Creator<Currency>() {
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    private String code;
    private String symbol;
    private SymbolPosition symbolPosition;
    private DecimalSeparator decimalSeparator;
    private GroupSeparator groupSeparator;
    private int decimalCount;
    private boolean isDefault;
    private double exchangeRate;

    public Currency() {
        super();
        setCode(null);
        setSymbol(null);
        setSymbolPosition(SymbolPosition.FAR_RIGHT);
        setDecimalSeparator(DecimalSeparator.DOT);
        setGroupSeparator(GroupSeparator.COMMA);
        setDecimalCount(2);
        setDefault(false);
        setExchangeRate(1.0);
    }

    public Currency(Parcel in) {
        super(in);
    }

    public static void updateDefaultCurrency(SQLiteDatabase database, Currency defaultCurrency) {
        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Currencies.LOCAL_ID)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.IS_DEFAULT + "=?", "1")
                .from(database, Tables.Currencies.TABLE_NAME)
                .execute();

        defaultCurrency.updateFrom(cursor, null);
        IOUtils.closeQuietly(cursor);
    }

    public static Currency from(Cursor cursor) {
        final Currency currency = new Currency();
        if (cursor.getCount() > 0) {
            currency.updateFrom(cursor, null);
        }
        return currency;
    }

    public static Currency fromCurrencyFrom(Cursor cursor) {
        final Currency currency = new Currency();
        if (cursor.getCount() > 0) {
            currency.updateFrom(cursor, Tables.Currencies.TEMP_TABLE_NAME_FROM_CURRENCY);
        }
        return currency;
    }

    public static Currency fromCurrencyTo(Cursor cursor) {
        final Currency currency = new Currency();
        if (cursor.getCount() > 0) {
            currency.updateFrom(cursor, Tables.Currencies.TEMP_TABLE_NAME_TO_CURRENCY);
        }
        return currency;
    }

    public static Currency from(CurrencyEntity entity) {
        final Currency currency = new Currency();
        currency.updateFrom(entity);
        return currency;
    }

    @Override protected Column getLocalIdColumn() {
        return Tables.Currencies.LOCAL_ID;
    }

    @Override protected Column getIdColumn() {
        return Tables.Currencies.ID;
    }

    @Override protected Column getModelStateColumn() {
        return Tables.Currencies.MODEL_STATE;
    }

    @Override protected Column getSyncStateColumn() {
        return Tables.Currencies.SYNC_STATE;
    }

    @Override protected void toValues(ContentValues values) {
        values.put(Tables.Currencies.CODE.getName(), code);
        values.put(Tables.Currencies.SYMBOL.getName(), symbol);
        values.put(Tables.Currencies.SYMBOL_POSITION.getName(), symbolPosition.asInt());
        values.put(Tables.Currencies.DECIMAL_SEPARATOR.getName(), decimalSeparator.symbol());
        values.put(Tables.Currencies.GROUP_SEPARATOR.getName(), groupSeparator.symbol());
        values.put(Tables.Currencies.DECIMAL_COUNT.getName(), decimalCount);
        values.put(Tables.Currencies.IS_DEFAULT.getName(), isDefault);
        values.put(Tables.Currencies.EXCHANGE_RATE.getName(), isDefault ? 1.0f : getExchangeRate());
    }

    @Override protected void toParcel(Parcel parcel) {
        parcel.writeString(code);
        parcel.writeString(symbol);
        parcel.writeInt(symbolPosition.asInt());
        parcel.writeString(decimalSeparator.symbol());
        parcel.writeString(groupSeparator.symbol());
        parcel.writeInt(decimalCount);
        parcel.writeInt(isDefault ? 1 : 0);
        parcel.writeDouble(exchangeRate);
    }

    @Override protected void toEntity(CurrencyEntity entity) {
        entity.setCode(code);
        entity.setSymbol(symbol);
        entity.setSymbolPosition(symbolPosition.toString());
        entity.setDecimalSeparator(decimalSeparator.toString());
        entity.setGroupSeparator(groupSeparator.toString());
        entity.setDecimalCount(decimalCount);
        entity.setIsDefault(isDefault);
    }

    @Override protected CurrencyEntity createEntity() {
        return new CurrencyEntity();
    }

    @Override protected void fromParcel(Parcel parcel) {
        setCode(parcel.readString());
        setSymbol(parcel.readString());
        setSymbolPosition(SymbolPosition.fromInt(parcel.readInt()));
        setDecimalSeparator(DecimalSeparator.fromSymbol(parcel.readString()));
        setGroupSeparator(GroupSeparator.fromSymbol(parcel.readString()));
        setDecimalCount(parcel.readInt());
        setDefault(parcel.readInt() != 0);
        setExchangeRate(parcel.readDouble());
    }

    @Override protected void fromCursor(Cursor cursor, String columnPrefixTable) {
        int index;

        // Code
        index = cursor.getColumnIndex(Tables.Currencies.CODE.getName(columnPrefixTable));
        if (index >= 0) {
            setCode(cursor.getString(index));
        }

        // Symbol
        index = cursor.getColumnIndex(Tables.Currencies.SYMBOL.getName(columnPrefixTable));
        if (index >= 0) {
            setSymbol(cursor.getString(index));
        }

        // Symbol position
        index = cursor.getColumnIndex(Tables.Currencies.SYMBOL_POSITION.getName(columnPrefixTable));
        if (index >= 0) {
            setSymbolPosition(SymbolPosition.fromInt(cursor.getInt(index)));
        }

        // Decimal separator
        index = cursor.getColumnIndex(Tables.Currencies.DECIMAL_SEPARATOR.getName(columnPrefixTable));
        if (index >= 0) {
            setDecimalSeparator(DecimalSeparator.fromSymbol(cursor.getString(index)));
        }

        // Group separator
        index = cursor.getColumnIndex(Tables.Currencies.GROUP_SEPARATOR.getName(columnPrefixTable));
        if (index >= 0) {
            setGroupSeparator(GroupSeparator.fromSymbol(cursor.getString(index)));
        }

        // Decimal count
        index = cursor.getColumnIndex(Tables.Currencies.DECIMAL_COUNT.getName(columnPrefixTable));
        if (index >= 0) {
            setDecimalCount(cursor.getInt(index));
        }

        // Is default
        index = cursor.getColumnIndex(Tables.Currencies.IS_DEFAULT.getName(columnPrefixTable));
        if (index >= 0) {
            setDefault(cursor.getInt(index) != 0);
        }

        // Exchange rate
        index = cursor.getColumnIndex(Tables.Currencies.EXCHANGE_RATE.getName(columnPrefixTable));
        if (index >= 0) {
            setExchangeRate(cursor.getDouble(index));
        }
    }

    @Override protected void fromEntity(CurrencyEntity entity) {
        setCode(entity.getCode());
        setSymbol(entity.getSymbol());
        setSymbolPosition(SymbolPosition.valueOf(entity.getSymbolPosition()));
        setDecimalSeparator(DecimalSeparator.valueOf(entity.getDecimalSeparator()));
        setGroupSeparator(GroupSeparator.valueOf(entity.getGroupSeparator()));
        setDecimalCount(entity.getDecimalCount());
        setDefault(entity.getDefault());
    }

    @Override public void checkValues() throws IllegalStateException {
        super.checkValues();
        Preconditions.checkNotEmpty(code, "Code cannot be empty.");
        Preconditions.checkLength(code, 3, "Code length must be 3.");
        Preconditions.checkNotNull(symbolPosition, "SymbolPosition cannot be null.");
        Preconditions.checkNotNull(decimalSeparator, "DecimalSeparator cannot be null.");
        Preconditions.checkNotNull(groupSeparator, "GroupSeparator cannot be null.");
        Preconditions.checkBetween(decimalCount, 0, 2, "Decimal count must be [0, 2]");
        Preconditions.checkLess(exchangeRate, 0.0, "Exchange rate must be > 0");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public SymbolPosition getSymbolPosition() {
        return symbolPosition;
    }

    public void setSymbolPosition(SymbolPosition symbolPosition) {
        this.symbolPosition = symbolPosition;
    }

    public DecimalSeparator getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(DecimalSeparator decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public GroupSeparator getGroupSeparator() {
        return groupSeparator;
    }

    public void setGroupSeparator(GroupSeparator groupSeparator) {
        this.groupSeparator = groupSeparator;
    }

    public int getDecimalCount() {
        return decimalCount;
    }

    public void setDecimalCount(int decimalCount) {
        this.decimalCount = decimalCount;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
