package com.code44.finance.ui.currencies.edit;

import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.SymbolPosition;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.ui.common.activities.ModelEditActivity;

class CurrencyFormatEditData extends ModelEditActivity.ModelEditData<CurrencyFormat> {
    public static final Parcelable.Creator<CurrencyFormatEditData> CREATOR = new Parcelable.Creator<CurrencyFormatEditData>() {
        public CurrencyFormatEditData createFromParcel(Parcel in) {
            return new CurrencyFormatEditData(in);
        }

        public CurrencyFormatEditData[] newArray(int size) {
            return new CurrencyFormatEditData[size];
        }
    };

    private String code;
    private String symbol;
    private SymbolPosition symbolPosition;
    private GroupSeparator groupSeparator;
    private DecimalSeparator decimalSeparator;
    private Integer decimalCount;

    public CurrencyFormatEditData() {
        super();
    }

    public CurrencyFormatEditData(Parcel in) {
        super(in);
        code = in.readString();
        symbol = in.readString();
        symbolPosition = (SymbolPosition) in.readSerializable();
        groupSeparator = (GroupSeparator) in.readSerializable();
        decimalSeparator = (DecimalSeparator) in.readSerializable();
        decimalCount = (Integer) in.readSerializable();
    }

    @Override public CurrencyFormat createModel() {
        final CurrencyFormat currencyFormat = new CurrencyFormat();
        currencyFormat.setId(getId());
        currencyFormat.setCode(getCode());
        currencyFormat.setSymbol(getSymbol());
        currencyFormat.setSymbolPosition(getSymbolPosition());
        currencyFormat.setGroupSeparator(getGroupSeparator());
        currencyFormat.setDecimalSeparator(getDecimalSeparator());
        currencyFormat.setDecimalCount(getDecimalCount());
        return currencyFormat;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(symbol);
        dest.writeSerializable(symbolPosition);
        dest.writeSerializable(groupSeparator);
        dest.writeSerializable(decimalSeparator);
        dest.writeSerializable(decimalCount);
    }

    public String getCode() {
        if (code != null) {
            return code;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getCode();
        }

        return null;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        if (symbol != null) {
            return symbol;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getSymbol();
        }

        return "";
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public SymbolPosition getSymbolPosition() {
        if (symbolPosition != null) {
            return symbolPosition;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getSymbolPosition();
        }

        return SymbolPosition.FarRight;
    }

    public void setSymbolPosition(SymbolPosition symbolPosition) {
        this.symbolPosition = symbolPosition;
    }

    public GroupSeparator getGroupSeparator() {
        if (groupSeparator != null) {
            return groupSeparator;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getGroupSeparator();
        }

        return GroupSeparator.Comma;
    }

    public void setGroupSeparator(GroupSeparator groupSeparator) {
        this.groupSeparator = groupSeparator;
    }

    public DecimalSeparator getDecimalSeparator() {
        if (decimalSeparator != null) {
            return decimalSeparator;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getDecimalSeparator();
        }

        return DecimalSeparator.Dot;
    }

    public void setDecimalSeparator(DecimalSeparator decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public int getDecimalCount() {
        if (decimalCount != null) {
            return decimalCount;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getDecimalCount();
        }

        return 2;
    }

    public void setDecimalCount(Integer decimalCount) {
        this.decimalCount = decimalCount;
    }
}
