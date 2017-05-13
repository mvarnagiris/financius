package com.code44.finance.backend.entities;

import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.annotation.Entity;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class AccountEntity extends BaseUserEntity {
    @ApiResourceProperty(name = "currency_code") private String currencyCode;

    @ApiResourceProperty(name = "title") private String title;

    @ApiResourceProperty(name = "note") private String note;

    @ApiResourceProperty(name = "include_in_totals") private boolean includeInTotals;

    public static AccountEntity find(String id) {
        return ofy().load().type(AccountEntity.class).id(id).now();
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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

    public boolean isIncludeInTotals() {
        return includeInTotals;
    }

    public void setIncludeInTotals(boolean includeInTotals) {
        this.includeInTotals = includeInTotals;
    }
}
