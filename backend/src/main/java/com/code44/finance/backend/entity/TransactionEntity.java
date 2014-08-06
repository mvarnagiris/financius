package com.code44.finance.backend.entity;

import com.code44.finance.common.model.TransactionState;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class TransactionEntity extends BaseEntity {
    @Index
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<UserAccount> userAccount;

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<AccountEntity> accountFrom;

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<AccountEntity> accountTo;

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<CategoryEntity> category;

    @ApiResourceProperty(name = "account_from_id")
    @Ignore
    private String accountFromId;

    @ApiResourceProperty(name = "account_to_id")
    @Ignore
    private String accountToId;

    @ApiResourceProperty(name = "category_id")
    @Ignore
    private String categoryId;

    @ApiResourceProperty(name = "title")
    private String title;

    @ApiResourceProperty(name = "date")
    private long date;

    @ApiResourceProperty(name = "amount")
    private long amount;

    @ApiResourceProperty(name = "exchange_rate")
    private double exchangeRate;

    @ApiResourceProperty(name = "note")
    private String note;

    @ApiResourceProperty(name = "transaction_state")
    private TransactionState transactionState;

    public static TransactionEntity find(String id) {
        return ofy().load().type(TransactionEntity.class).id(id).now();
    }

    public Key<UserAccount> getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(Key<UserAccount> userAccount) {
        this.userAccount = userAccount;
    }

    public Key<AccountEntity> getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(Key<AccountEntity> accountFrom) {
        this.accountFrom = accountFrom;
    }

    public Key<AccountEntity> getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(Key<AccountEntity> accountTo) {
        this.accountTo = accountTo;
    }

    public Key<CategoryEntity> getCategory() {
        return category;
    }

    public void setCategory(Key<CategoryEntity> category) {
        this.category = category;
    }

    public String getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(String accountFromId) {
        this.accountFromId = accountFromId;
    }

    public String getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(String accountToId) {
        this.accountToId = accountToId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
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

    public TransactionState getTransactionState() {
        return transactionState;
    }

    public void setTransactionState(TransactionState transactionState) {
        this.transactionState = transactionState;
    }
}
