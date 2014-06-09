package com.code44.finance.db.model;

import java.util.Date;

public class Transaction extends BaseModel {
    private Account accountFrom;
    private Account accountTo;
    private Category category;
    private Date date;
    private long amount;
    private Double exchangeRate;
    private String note;

    @Override
    public void useDefaultsIfNotSet() {
        super.useDefaultsIfNotSet();

        if (date == null) {
            date = new Date();
        }

        if (exchangeRate == null) {
            setExchangeRate(1);
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

        if (category == null) {
            throw new IllegalStateException("Category cannot be null.");
        }

        if (date == null) {
            throw new IllegalStateException("Date cannot be null.");
        }

        if (exchangeRate == null || Double.compare(exchangeRate, 0) < 0) {
            throw new IllegalStateException("Exchange rate must be > 0.");
        }
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
}
