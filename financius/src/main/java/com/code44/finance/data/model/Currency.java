package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.App;
import com.code44.finance.backend.endpoint.currencies.model.CurrencyEntity;
import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.common.model.SymbolPosition;
import com.code44.finance.common.utils.StringUtils;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.IOUtils;

public class Currency extends BaseModel {
    public static final Parcelable.Creator<Currency> CREATOR = new Parcelable.Creator<Currency>() {
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    private static Currency defaultCurrency;

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

    public static Currency getDefault() {
        if (defaultCurrency == null) {
            final Cursor cursor = Query.create()
                    .projectionId(Tables.Currencies.ID)
                    .projection(Tables.Currencies.PROJECTION)
                    .selection(Tables.Currencies.IS_DEFAULT.getName() + "=?", "1")
                    .from(App.getContext(), CurrenciesProvider.uriCurrencies())
                    .execute();

            defaultCurrency = Currency.from(cursor);
            IOUtils.closeQuietly(cursor);
        }
        return defaultCurrency;
    }

    public static void updateDefaultCurrency(SQLiteDatabase db) {
        final Cursor cursor = Query.create()
                .projectionId(Tables.Currencies.ID)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.IS_DEFAULT + "=?", "1")
                .from(db, Tables.Currencies.TABLE_NAME)
                .execute();

        defaultCurrency = Currency.from(cursor);
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
        currency.setServerId(entity.getId());
        currency.setModelState(ModelState.valueOf(entity.getModelState()));
        currency.setSyncState(SyncState.SYNCED);
        currency.setCode(entity.getCode());
        currency.setSymbol(entity.getSymbol());
        currency.setSymbolPosition(SymbolPosition.valueOf(entity.getSymbolPosition()));
        currency.setDecimalSeparator(DecimalSeparator.valueOf(entity.getDecimalSeparator()));
        currency.setGroupSeparator(GroupSeparator.valueOf(entity.getGroupSeparator()));
        currency.setDecimalCount(entity.getDecimalCount());
        currency.setDefault(entity.getDefault());
        return currency;
    }

    @Override
    protected Column getIdColumn() {
        return Tables.Currencies.ID;
    }

    @Override
    protected Column getServerIdColumn() {
        return Tables.Currencies.SERVER_ID;
    }

    @Override
    protected Column getModelStateColumn() {
        return Tables.Currencies.MODEL_STATE;
    }

    @Override
    protected Column getSyncStateColumn() {
        return Tables.Currencies.SYNC_STATE;
    }

    @Override
    protected void fromParcel(Parcel parcel) {
        setCode(parcel.readString());
        setSymbol(parcel.readString());
        setSymbolPosition(SymbolPosition.fromInt(parcel.readInt()));
        setDecimalSeparator(DecimalSeparator.fromSymbol(parcel.readString()));
        setGroupSeparator(GroupSeparator.fromSymbol(parcel.readString()));
        setDecimalCount(parcel.readInt());
        setDefault(parcel.readInt() != 0);
        setExchangeRate(parcel.readDouble());
    }

    @Override
    protected void toParcel(Parcel parcel) {
        parcel.writeString(getCode());
        parcel.writeString(getSymbol());
        parcel.writeInt(getSymbolPosition().asInt());
        parcel.writeString(getDecimalSeparator().symbol());
        parcel.writeString(getGroupSeparator().symbol());
        parcel.writeInt(getDecimalCount());
        parcel.writeInt(isDefault() ? 1 : 0);
        parcel.writeDouble(getExchangeRate());
    }

    @Override
    protected void toValues(ContentValues values) {
        values.put(Tables.Currencies.CODE.getName(), getCode());
        values.put(Tables.Currencies.SYMBOL.getName(), getSymbol());
        values.put(Tables.Currencies.SYMBOL_POSITION.getName(), getSymbolPosition().asInt());
        values.put(Tables.Currencies.DECIMAL_SEPARATOR.getName(), getDecimalSeparator().symbol());
        values.put(Tables.Currencies.GROUP_SEPARATOR.getName(), getGroupSeparator().symbol());
        values.put(Tables.Currencies.DECIMAL_COUNT.getName(), getDecimalCount());
        values.put(Tables.Currencies.IS_DEFAULT.getName(), isDefault());
        values.put(Tables.Currencies.EXCHANGE_RATE.getName(), isDefault() ? 1.0f : getExchangeRate());
    }

    @Override
    protected void fromCursor(Cursor cursor, String columnPrefixTable) {
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

    @Override
    public void checkValues() throws IllegalStateException {
        super.checkValues();

        if (StringUtils.isEmpty(code)) {
            throw new IllegalStateException("Code cannot be empty.");
        }

        if (code.length() != 3) {
            throw new IllegalStateException("Code length must be 3.");
        }

        if (symbolPosition == null) {
            throw new IllegalStateException("SymbolPosition cannot be null.");
        }

        if (decimalSeparator == null) {
            throw new IllegalStateException("DecimalSeparator cannot be null.");
        }

        if (groupSeparator == null) {
            throw new IllegalStateException("GroupSeparator cannot be null.");
        }

        if (decimalCount < 0 || decimalCount > 2) {
            throw new IllegalStateException("Decimal count must be [0, 2]");
        }

        if (Double.compare(exchangeRate, 0.0) < 0) {
            throw new IllegalStateException("Exchange rate must be > 0");
        }
    }

    public CurrencyEntity toEntity() {
        final CurrencyEntity entity = new CurrencyEntity();
        entity.setId(getServerId());
        entity.setModelState(getModelState().toString());
        entity.setCode(getCode());
        entity.setSymbol(getSymbol());
        entity.setSymbolPosition(getSymbolPosition().toString());
        entity.setDecimalSeparator(getDecimalSeparator().toString());
        entity.setGroupSeparator(getGroupSeparator().toString());
        entity.setDecimalCount(getDecimalCount());
        entity.setIsDefault(isDefault());
        return entity;
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
