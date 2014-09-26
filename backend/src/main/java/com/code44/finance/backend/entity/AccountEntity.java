package com.code44.finance.backend.entity;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class AccountEntity extends BaseEntity {
    @Index
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<UserAccount> userAccount;

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<CurrencyEntity> currency;

    @ApiResourceProperty(name = "currency_id")
    @Ignore
    private String currencyId;

    @ApiResourceProperty(name = "title")
    private String title;

    @ApiResourceProperty(name = "note")
    private String note;

    @ApiResourceProperty(name = "include_in_totals")
    private boolean includeInTotals;

    public static AccountEntity find(String id) {
        return ofy().load().type(AccountEntity.class).id(id).now();
    }

    public Key<UserAccount> getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(Key<UserAccount> userAccount) {
        this.userAccount = userAccount;
    }

    public Key<CurrencyEntity> getCurrency() {
        return currency;
    }

    public void setCurrency(Key<CurrencyEntity> currency) {
        this.currency = currency;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
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

    public boolean includeInTotals() {
        return includeInTotals;
    }

    public void setIncludeInTotals(boolean includeInTotals) {
        this.includeInTotals = includeInTotals;
    }
}
