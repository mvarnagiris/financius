package com.code44.finance.backend.entity;

import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.SymbolPosition;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class Currency extends BaseEntity {
    private String code;
    private String symbol;
    private SymbolPosition symbolPosition;
    private DecimalSeparator decimalSeparator;
    private GroupSeparator groupSeparator;
    private int decimalCount;
    private boolean isDefault;
    private double exchangeRate;

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
