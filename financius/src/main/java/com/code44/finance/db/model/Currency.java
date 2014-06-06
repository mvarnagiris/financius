package com.code44.finance.db.model;

import android.text.TextUtils;

import com.code44.finance.App;
import com.code44.finance.providers.CurrenciesProvider;

import nl.qbusict.cupboard.CupboardFactory;

public class Currency extends BaseModel {
    private static Currency defaultCurrency;

    private String code;
    private String symbol;
    private SymbolPosition symbolPosition;
    private DecimalSeparator decimalSeparator;
    private GroupSeparator groupSeparator;
    private Integer decimalCount;
    private boolean isDefault;
    private Double exchangeRate;

    public Currency() {
        super();
    }

    public static Currency getDefault() {
        if (defaultCurrency == null) {
            defaultCurrency = CupboardFactory.cupboard().withContext(App.getAppContext())
                    .query(CurrenciesProvider.uriCurrencies(), Currency.class)
                    .withSelection("isDefault=?", "1").get();
        }
        return defaultCurrency;
    }

    @Override
    public void useDefaultsIfNotSet() {
        super.useDefaultsIfNotSet();

        if (symbolPosition == null) {
            setSymbolPosition(SymbolPosition.CLOSE_RIGHT);
        }

        if (decimalSeparator == null) {
            setDecimalSeparator(DecimalSeparator.DOT);
        }

        if (groupSeparator == null) {
            setGroupSeparator(GroupSeparator.COMMA);
        }

        if (decimalCount == null) {
            setDecimalCount(2);
        }

        if (exchangeRate == null) {
            setExchangeRate(1);
        }
    }

    @Override
    public void checkRequiredValues() throws IllegalStateException {
        super.checkRequiredValues();

        if (TextUtils.isEmpty(code)) {
            throw new IllegalStateException("Code cannot be empty.");
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

        if (decimalCount == null || decimalCount < 0 || decimalCount > 2) {
            throw new IllegalStateException("Decimal count must be [0, 2]");
        }

        if (exchangeRate == null || Double.compare(exchangeRate, 0.0) < 0) {
            throw new IllegalStateException("Exchange rate must be > 0");
        }
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

    public static enum DecimalSeparator {
        DOT("."), COMMA(","), SPACE(" ");

        private final String symbol;

        private DecimalSeparator(String symbol) {
            this.symbol = symbol;
        }

        private String symbol() {
            return symbol;
        }
    }

    public static enum GroupSeparator {
        NONE(""), DOT("."), COMMA(","), SPACE(" ");

        private final String symbol;

        private GroupSeparator(String symbol) {
            this.symbol = symbol;
        }

        private String symbol() {
            return symbol;
        }
    }

    public static enum SymbolPosition {
        CLOSE_RIGHT, FAR_RIGHT, CLOSE_LEFT, FAR_LEFT
    }
}
