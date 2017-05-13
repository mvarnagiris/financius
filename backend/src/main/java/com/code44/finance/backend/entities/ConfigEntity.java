package com.code44.finance.backend.entities;

import com.code44.finance.common.interval.IntervalType;
import com.code44.finance.common.security.SecurityType;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class ConfigEntity extends BaseUserEntity {
    @ApiResourceProperty(name = "currency_code") private String currencyCode;

    @ApiResourceProperty(name = "interval_type") private IntervalType intervalType;

    @ApiResourceProperty(name = "interval_length") private int intervalLength;

    @ApiResourceProperty(name = "security_type") private SecurityType securityType;

    @ApiResourceProperty(name = "password") private String password;

    @ApiResourceProperty(name = "user_update_timestamp") private long userUpdateTimestamp;

    @ApiResourceProperty(name = "config_update_timestamp") private long configUpdateTimestamp;

    @ApiResourceProperty(name = "currencies_update_timestamp") private long currenciesUpdateTimestamp;

    @ApiResourceProperty(name = "tags_update_timestamp") private long tagsUpdateTimestamp;

    @ApiResourceProperty(name = "categories_update_timestamp") private long categoriesUpdateTimestamp;

    @ApiResourceProperty(name = "accounts_update_timestamp") private long accountsUpdateTimestamp;

    @ApiResourceProperty(name = "transactions_update_timestamp") private long transactionsUpdateTimestamp;

    public static ConfigEntity find(UserEntity userEntity) {
        return ofy().load().type(ConfigEntity.class).filter("userEntity", Key.create(UserEntity.class, userEntity.getId())).first().now();
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public IntervalType getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(IntervalType intervalType) {
        this.intervalType = intervalType;
    }

    public int getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(int intervalLength) {
        this.intervalLength = intervalLength;
    }

    public SecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(SecurityType securityType) {
        this.securityType = securityType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getUserUpdateTimestamp() {
        return userUpdateTimestamp;
    }

    public void setUserUpdateTimestamp(long userUpdateTimestamp) {
        this.userUpdateTimestamp = userUpdateTimestamp;
    }

    public long getConfigUpdateTimestamp() {
        return configUpdateTimestamp;
    }

    public void setConfigUpdateTimestamp(long configUpdateTimestamp) {
        this.configUpdateTimestamp = configUpdateTimestamp;
    }

    public long getCurrenciesUpdateTimestamp() {
        return currenciesUpdateTimestamp;
    }

    public void setCurrenciesUpdateTimestamp(long currenciesUpdateTimestamp) {
        this.currenciesUpdateTimestamp = currenciesUpdateTimestamp;
    }

    public long getTagsUpdateTimestamp() {
        return tagsUpdateTimestamp;
    }

    public void setTagsUpdateTimestamp(long tagsUpdateTimestamp) {
        this.tagsUpdateTimestamp = tagsUpdateTimestamp;
    }

    public long getCategoriesUpdateTimestamp() {
        return categoriesUpdateTimestamp;
    }

    public void setCategoriesUpdateTimestamp(long categoriesUpdateTimestamp) {
        this.categoriesUpdateTimestamp = categoriesUpdateTimestamp;
    }

    public long getAccountsUpdateTimestamp() {
        return accountsUpdateTimestamp;
    }

    public void setAccountsUpdateTimestamp(long accountsUpdateTimestamp) {
        this.accountsUpdateTimestamp = accountsUpdateTimestamp;
    }

    public long getTransactionsUpdateTimestamp() {
        return transactionsUpdateTimestamp;
    }

    public void setTransactionsUpdateTimestamp(long transactionsUpdateTimestamp) {
        this.transactionsUpdateTimestamp = transactionsUpdateTimestamp;
    }
}
