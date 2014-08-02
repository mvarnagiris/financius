package com.code44.finance.backend.entity;

import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.SymbolPosition;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class Currency extends BaseEntity {
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<UserAccount> userAccount;

    @ApiResourceProperty(name = "code")
    private String code;

    @ApiResourceProperty(name = "symbol")
    private String symbol;

    @ApiResourceProperty(name = "symbol_position")
    private SymbolPosition symbolPosition;

    @ApiResourceProperty(name = "decimal_separator")
    private DecimalSeparator decimalSeparator;

    @ApiResourceProperty(name = "group_separator")
    private GroupSeparator groupSeparator;

    @ApiResourceProperty(name = "decimal_count")
    private int decimalCount;

    @ApiResourceProperty(name = "is_default")
    private boolean isDefault;

    public static Currency find(String id) {
        return ofy().load().type(Currency.class).id(id).now();
    }

    public Key<UserAccount> getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(Key<UserAccount> userAccount) {
        this.userAccount = userAccount;
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
}
