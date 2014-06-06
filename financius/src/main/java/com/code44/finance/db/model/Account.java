package com.code44.finance.db.model;

import com.code44.finance.App;
import com.code44.finance.providers.AccountsProvider;

import nl.qbusict.cupboard.CupboardFactory;

public class Account extends BaseModel {
    private static Account systemAccount;

    private Currency currency;
    private String title;
    private String note;
    private long balance;
    private Owner owner;

    public Account() {
        super();
    }

    public static Account getSystem() {
        if (systemAccount == null) {
            systemAccount = CupboardFactory.cupboard().withContext(App.getAppContext())
                    .query(AccountsProvider.uriAccounts(), Account.class)
                    .withSelection("owner=?", Owner.SYSTEM.toString()).get();
        }
        return systemAccount;
    }

    @Override
    public void useDefaultsIfNotSet() {
        super.useDefaultsIfNotSet();

        if (currency == null) {
            setCurrency(Currency.getDefault());
        }

        if (owner == null) {
            setOwner(Owner.USER);
        }
    }

    @Override
    public void checkRequiredValues() throws IllegalStateException {
        super.checkRequiredValues();

        if (currency == null) {
            throw new IllegalStateException("Currency cannot be null.");
        }

        if (owner == null) {
            throw new IllegalStateException("Owner cannot be null.");
        }
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public static enum Owner {
        SYSTEM, USER
    }
}
