package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.code44.finance.backend.financius.model.CurrencyEntity;
import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.SymbolPosition;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.google.common.base.Strings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class CurrencyFormat extends Model<CurrencyEntity> {
    public static final Parcelable.Creator<CurrencyFormat> CREATOR = new Parcelable.Creator<CurrencyFormat>() {
        public CurrencyFormat createFromParcel(Parcel in) {
            return new CurrencyFormat(in);
        }

        public CurrencyFormat[] newArray(int size) {
            return new CurrencyFormat[size];
        }
    };

    private String code;
    private String symbol;
    private SymbolPosition symbolPosition;
    private DecimalSeparator decimalSeparator;
    private GroupSeparator groupSeparator;
    private int decimalCount;

    private transient DecimalFormat decimalFormat;

    public CurrencyFormat() {
        super();
        setCode(null);
        setSymbol(null);
        setSymbolPosition(SymbolPosition.FarRight);
        setDecimalSeparator(DecimalSeparator.Dot);
        setGroupSeparator(GroupSeparator.Comma);
        setDecimalCount(2);
    }

    private CurrencyFormat(Parcel parcel) {
        super(parcel);
        setCode(parcel.readString());
        setSymbol(parcel.readString());
        setSymbolPosition(SymbolPosition.fromInt(parcel.readInt()));
        setDecimalSeparator(DecimalSeparator.fromSymbol(parcel.readString()));
        setGroupSeparator(GroupSeparator.fromSymbol(parcel.readString()));
        setDecimalCount(parcel.readInt());
    }

    public static CurrencyFormat from(Cursor cursor) {
        final CurrencyFormat currencyFormat = new CurrencyFormat();
        if (cursor.getCount() > 0) {
            currencyFormat.updateFromCursor(cursor, null);
        }
        return currencyFormat;
    }

    public static CurrencyFormat from(CurrencyEntity entity) {
        final CurrencyFormat currencyFormat = new CurrencyFormat();
        currencyFormat.updateFromEntity(entity);
        return currencyFormat;
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(code);
        parcel.writeString(symbol);
        parcel.writeInt(symbolPosition.asInt());
        parcel.writeString(decimalSeparator.symbol());
        parcel.writeString(groupSeparator.symbol());
        parcel.writeInt(decimalCount);
    }

    @Override public ContentValues asContentValues() {
        final ContentValues values = super.asContentValues();
        values.put(Tables.CurrencyFormats.CODE.getName(), code);
        values.put(Tables.CurrencyFormats.SYMBOL.getName(), symbol);
        values.put(Tables.CurrencyFormats.SYMBOL_POSITION.getName(), symbolPosition.asInt());
        values.put(Tables.CurrencyFormats.DECIMAL_SEPARATOR.getName(), decimalSeparator.symbol());
        values.put(Tables.CurrencyFormats.GROUP_SEPARATOR.getName(), groupSeparator.symbol());
        values.put(Tables.CurrencyFormats.DECIMAL_COUNT.getName(), decimalCount);
        return values;
    }

    @Override public void prepareForContentValues() {
        super.prepareForContentValues();

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

    @Override public void validateForContentValues() {
        super.validateForContentValues();
        checkState(!Strings.isNullOrEmpty(code), "Code cannot be empty.");
        checkState(code.length() == 3, "Code length must be 3.");
        checkNotNull(symbolPosition, "SymbolPosition cannot be null.");
        checkNotNull(decimalSeparator, "DecimalSeparator cannot be null.");
        checkNotNull(groupSeparator, "GroupSeparator cannot be null.");
        checkState(decimalCount >= 0 && decimalCount <= 2, "Decimal count must be [0, 2]");
    }

    @Override public void updateFromCursor(Cursor cursor, String columnPrefixTable) {
        super.updateFromCursor(cursor, columnPrefixTable);
        int index;

        // Code
        index = cursor.getColumnIndex(Tables.CurrencyFormats.CODE.getName(columnPrefixTable));
        if (index >= 0) {
            setCode(cursor.getString(index));
        }

        // Symbol
        index = cursor.getColumnIndex(Tables.CurrencyFormats.SYMBOL.getName(columnPrefixTable));
        if (index >= 0) {
            setSymbol(cursor.getString(index));
        }

        // Symbol position
        index = cursor.getColumnIndex(Tables.CurrencyFormats.SYMBOL_POSITION.getName(columnPrefixTable));
        if (index >= 0) {
            setSymbolPosition(SymbolPosition.fromInt(cursor.getInt(index)));
        }

        // Decimal separator
        index = cursor.getColumnIndex(Tables.CurrencyFormats.DECIMAL_SEPARATOR.getName(columnPrefixTable));
        if (index >= 0) {
            setDecimalSeparator(DecimalSeparator.fromSymbol(cursor.getString(index)));
        }

        // Group separator
        index = cursor.getColumnIndex(Tables.CurrencyFormats.GROUP_SEPARATOR.getName(columnPrefixTable));
        if (index >= 0) {
            setGroupSeparator(GroupSeparator.fromSymbol(cursor.getString(index)));
        }

        // Decimal count
        index = cursor.getColumnIndex(Tables.CurrencyFormats.DECIMAL_COUNT.getName(columnPrefixTable));
        if (index >= 0) {
            setDecimalCount(cursor.getInt(index));
        }
    }

    @Override public void updateFromEntity(CurrencyEntity entity) {
        super.updateFromEntity(entity);
        setCode(entity.getCode());
        setSymbol(entity.getSymbol());
        setSymbolPosition(SymbolPosition.valueOf(entity.getSymbolPosition()));
        setDecimalSeparator(DecimalSeparator.valueOf(entity.getDecimalSeparator()));
        setGroupSeparator(GroupSeparator.valueOf(entity.getGroupSeparator()));
        setDecimalCount(entity.getDecimalCount());
    }

    @Override protected Column getIdColumn() {
        return Tables.CurrencyFormats.ID;
    }

    @Override protected Column getModelStateColumn() {
        return Tables.CurrencyFormats.MODEL_STATE;
    }

    @Override protected Column getSyncStateColumn() {
        return Tables.CurrencyFormats.SYNC_STATE;
    }

    @Override protected Column getLocalIdColumn() {
        return Tables.CurrencyFormats.LOCAL_ID;
    }

    @Override protected CurrencyEntity createEntity() {
        return new CurrencyEntity();
    }

    @Override public CurrencyEntity asEntity() {
        final CurrencyEntity entity = super.asEntity();
        entity.setCode(code);
        entity.setSymbol(symbol);
        entity.setSymbolPosition(symbolPosition.name());
        entity.setDecimalSeparator(decimalSeparator.name());
        entity.setGroupSeparator(groupSeparator.name());
        entity.setDecimalCount(decimalCount);
        return entity;
    }

    public String getCode() {
        return code;
    }

    public CurrencyFormat setCode(String code) {
        this.code = code;
        decimalFormat = null;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public CurrencyFormat setSymbol(String symbol) {
        this.symbol = symbol;
        decimalFormat = null;
        return this;
    }

    public SymbolPosition getSymbolPosition() {
        return symbolPosition;
    }

    public CurrencyFormat setSymbolPosition(SymbolPosition symbolPosition) {
        this.symbolPosition = symbolPosition;
        decimalFormat = null;
        return this;
    }

    public DecimalSeparator getDecimalSeparator() {
        return decimalSeparator;
    }

    public CurrencyFormat setDecimalSeparator(DecimalSeparator decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
        decimalFormat = null;
        return this;
    }

    public GroupSeparator getGroupSeparator() {
        return groupSeparator;
    }

    public CurrencyFormat setGroupSeparator(GroupSeparator groupSeparator) {
        this.groupSeparator = groupSeparator;
        decimalFormat = null;
        return this;
    }

    public int getDecimalCount() {
        return decimalCount;
    }

    public CurrencyFormat setDecimalCount(int decimalCount) {
        this.decimalCount = decimalCount;
        decimalFormat = null;
        return this;
    }

    public String format(long moneyValue) {
        if (decimalFormat == null) {
            decimalFormat = (DecimalFormat) DecimalFormat.getInstance();

            // Setup symbols
            final DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
            final char groupSeparator = getGroupSeparator() != GroupSeparator.None ? getGroupSeparator().symbol().charAt(0) : 0;
            symbols.setGroupingSeparator(groupSeparator);
            symbols.setDecimalSeparator(getDecimalSeparator().symbol().charAt(0));

            // Setup format
            decimalFormat.setDecimalFormatSymbols(symbols);
            decimalFormat.setMinimumFractionDigits(getDecimalCount());
            decimalFormat.setMaximumFractionDigits(getDecimalCount());
            final String symbol = getSymbol();
            final boolean hasSymbol = !TextUtils.isEmpty(symbol);
            switch (getSymbolPosition()) {
                case FarLeft:
                    if (hasSymbol) {
                        decimalFormat.setPositivePrefix(symbol + " ");
                        decimalFormat.setNegativePrefix(symbol + " -");
                    } else {
                        decimalFormat.setNegativePrefix("-");
                    }

                    break;

                case CloseLeft:
                    if (hasSymbol) {
                        decimalFormat.setPositivePrefix(symbol);
                        decimalFormat.setNegativePrefix(symbol + "-");
                    } else {
                        decimalFormat.setNegativePrefix("-");
                    }

                    break;

                case FarRight:
                    if (hasSymbol) {
                        decimalFormat.setPositiveSuffix(" " + symbol);
                        decimalFormat.setNegativeSuffix(" " + symbol);
                    }
                    break;

                case CloseRight:
                    if (hasSymbol) {
                        decimalFormat.setPositiveSuffix(symbol);
                        decimalFormat.setNegativeSuffix(symbol);
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Symbol position " + getSymbolPosition() + " is not supported.");
            }
        }

        return decimalFormat.format(moneyValue / 100.0);
    }
}
