package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.SymbolPosition;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.code44.finance.utils.IOUtils;

import java.util.HashMap;
import java.util.Map;

public class Currency extends Model {
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
    private Map<String, ExchangeRate> exchangeRates;

    public Currency() {
        super();
        setCode(null);
        setSymbol(null);
        setSymbolPosition(SymbolPosition.FarRight);
        setDecimalSeparator(DecimalSeparator.Dot);
        setGroupSeparator(GroupSeparator.Comma);
        setDecimalCount(2);
        setDefault(false);
        setExchangeRates(null);
    }

    private Currency(Parcel parcel) {
        super(parcel);
        setCode(parcel.readString());
        setSymbol(parcel.readString());
        setSymbolPosition(SymbolPosition.fromInt(parcel.readInt()));
        setDecimalSeparator(DecimalSeparator.fromSymbol(parcel.readString()));
        setGroupSeparator(GroupSeparator.fromSymbol(parcel.readString()));
        setDecimalCount(parcel.readInt());
        setDefault(parcel.readInt() != 0);
        final int exchangeRatesSize = parcel.readInt();
        if (exchangeRatesSize == 0) {
            setExchangeRates(null);
        } else {
            final Map<String, ExchangeRate> exchangeRates = new HashMap<>();
            for (int i = 0; i < exchangeRatesSize; i++) {
                final ExchangeRate exchangeRate = parcel.readParcelable(ExchangeRate.class.getClassLoader());
                exchangeRates.put(exchangeRate.currencyCode, exchangeRate);
            }
            setExchangeRates(exchangeRates);
        }
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

    @Override public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(code);
        parcel.writeString(symbol);
        parcel.writeInt(symbolPosition.asInt());
        parcel.writeString(decimalSeparator.symbol());
        parcel.writeString(groupSeparator.symbol());
        parcel.writeInt(decimalCount);
        parcel.writeInt(isDefault ? 1 : 0);
        if (exchangeRates == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(exchangeRates.size());
            for (ExchangeRate exchangeRate : exchangeRates.values()) {
                parcel.writeParcelable(exchangeRate, flags);
            }
        }
    }

    @Override public void updateFrom(Cursor cursor, String columnPrefixTable) {
        super.updateFrom(cursor, columnPrefixTable);
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
        index = cursor.getColumnIndex(Tables.Currencies.EXCHANGE_RATES.getName(columnPrefixTable));
        if (index >= 0) {
            final String ratesPlainText = cursor.getString(index);
            if (!Strings.isEmpty(ratesPlainText)) {
                final String[] ratesSplit = cursor.getString(index).split(";");
                final Map<String, ExchangeRate> exchangeRates = new HashMap<>();
                for (String currencyCodeWithRate : ratesSplit) {
                    final String[] rateSplit = currencyCodeWithRate.split(":");
                    final ExchangeRate exchangeRate = new ExchangeRate(rateSplit[0], Double.parseDouble(rateSplit[1]));
                    exchangeRates.put(exchangeRate.currencyCode, exchangeRate);
                }
                setExchangeRates(exchangeRates);
            }
        }
    }

    @Override public void prepareForDb() {
        super.prepareForDb();

        if (symbolPosition == null) {
            symbolPosition = SymbolPosition.FarRight;
        }

        if (decimalSeparator == null) {
            decimalSeparator = DecimalSeparator.Dot;
        }

        if (groupSeparator == null) {
            groupSeparator = GroupSeparator.Comma;
        }

        if (decimalCount > 2) {
            decimalCount = 2;
        }

        if (decimalCount < 0) {
            decimalCount = 0;
        }
    }

    @Override public void validate() {
        super.validate();
        Preconditions.notEmpty(code, "Code cannot be empty.");
        Preconditions.lengthEquals(code, 3, "Code length must be 3.");
        Preconditions.notNull(symbolPosition, "SymbolPosition cannot be null.");
        Preconditions.notNull(decimalSeparator, "DecimalSeparator cannot be null.");
        Preconditions.notNull(groupSeparator, "GroupSeparator cannot be null.");
        Preconditions.between(decimalCount, 0, 2, "Decimal count must be [0, 2]");
    }

    @Override public ContentValues asValues() {
        final ContentValues values = super.asValues();
        values.put(Tables.Currencies.CODE.getName(), code);
        values.put(Tables.Currencies.SYMBOL.getName(), symbol);
        values.put(Tables.Currencies.SYMBOL_POSITION.getName(), symbolPosition.asInt());
        values.put(Tables.Currencies.DECIMAL_SEPARATOR.getName(), decimalSeparator.symbol());
        values.put(Tables.Currencies.GROUP_SEPARATOR.getName(), groupSeparator.symbol());
        values.put(Tables.Currencies.DECIMAL_COUNT.getName(), decimalCount);
        values.put(Tables.Currencies.IS_DEFAULT.getName(), isDefault);
        final StringBuilder sb = new StringBuilder();
        for (ExchangeRate exchangeRate : exchangeRates.values()) {
            sb.append(exchangeRate.currencyCode).append(":").append(exchangeRate.rate).append(";");
        }
        values.put(Tables.Currencies.EXCHANGE_RATES.getName(), sb.toString());
        return values;
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

    public Map<String, ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(Map<String, ExchangeRate> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    public double getExchangeRate(String currencyCode) {
        if (exchangeRates == null) {
            return 1;
        }

        final ExchangeRate exchangeRate = exchangeRates.get(currencyCode);
        if (exchangeRate == null) {
            return 1;
        }
        return exchangeRate.rate;
    }

    public static class ExchangeRate implements Parcelable {
        public static final Parcelable.Creator<ExchangeRate> CREATOR = new Parcelable.Creator<ExchangeRate>() {
            public ExchangeRate createFromParcel(Parcel in) {
                return new ExchangeRate(in);
            }

            public ExchangeRate[] newArray(int size) {
                return new ExchangeRate[size];
            }
        };

        private final String currencyCode;
        private final double rate;

        public ExchangeRate(String currencyCode, double rate) {
            this.currencyCode = currencyCode;
            this.rate = rate;
        }

        private ExchangeRate(Parcel parcel) {
            currencyCode = parcel.readString();
            rate = parcel.readDouble();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ExchangeRate)) return false;

            final ExchangeRate that = (ExchangeRate) o;

            if (Double.compare(that.rate, rate) != 0) return false;
            //noinspection RedundantIfStatement
            if (!currencyCode.equals(that.currencyCode)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = currencyCode.hashCode();
            temp = Double.doubleToLongBits(rate);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override public int describeContents() {
            return 0;
        }

        @Override public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(currencyCode);
            dest.writeDouble(rate);
        }
    }
}
