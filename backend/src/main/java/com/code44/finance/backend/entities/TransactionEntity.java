package com.code44.finance.backend.entities;

import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class TransactionEntity extends BaseUserEntity {
    @ApiResourceProperty(name = "account_from", ignored = AnnotationBoolean.TRUE) private Key<AccountEntity> accountFrom;

    @ApiResourceProperty(name = "account_to", ignored = AnnotationBoolean.TRUE) private Key<AccountEntity> accountTo;

    @ApiResourceProperty(name = "category", ignored = AnnotationBoolean.TRUE) private Key<CategoryEntity> category;

    @ApiResourceProperty(name = "tags", ignored = AnnotationBoolean.TRUE) private Collection<Key<TagEntity>> tags;

    @ApiResourceProperty(name = "date") private long date;

    @ApiResourceProperty(name = "amount") private long amount;

    @ApiResourceProperty(name = "exchange_rate") private double exchangeRate;

    @ApiResourceProperty(name = "note") private String note;

    @ApiResourceProperty(name = "transaction_state") private TransactionState transactionState;

    @ApiResourceProperty(name = "transaction_type") private TransactionType transactionType;

    @ApiResourceProperty(name = "include_in_reports") private boolean includeInReports;

    public static TransactionEntity find(String id) {
        return ofy().load().type(TransactionEntity.class).id(id).now();
    }

    public Key<AccountEntity> getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(Key<AccountEntity> accountFrom) {
        this.accountFrom = accountFrom;
    }

    public String getAccountFromId() {
        return accountFrom == null ? null : accountFrom.getName();
    }

    public void setAccountFromId(String accountFromId) {
        this.accountFrom = Key.create(AccountEntity.class, accountFromId);
    }

    public Key<AccountEntity> getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(Key<AccountEntity> accountTo) {
        this.accountTo = accountTo;
    }

    public String getAccountToId() {
        return accountTo == null ? null : accountTo.getName();
    }

    public void setAccountToId(String accountToId) {
        this.accountTo = Key.create(AccountEntity.class, accountToId);
    }

    public Key<CategoryEntity> getCategory() {
        return category;
    }

    public void setCategory(Key<CategoryEntity> category) {
        this.category = category;
    }

    public String getCategoryId() {
        return category == null ? null : category.getName();
    }

    public void setCategoryId(String categoryId) {
        this.category = Key.create(CategoryEntity.class, categoryId);
    }

    public Collection<Key<TagEntity>> getTags() {
        return tags;
    }

    public void setTags(Collection<Key<TagEntity>> tags) {
        this.tags = tags;
    }

    public Collection<String> getTagsIds() {
        if (tags == null) {
            return Collections.emptySet();
        }

        final Set<String> tagsIds = new HashSet<>();
        for (Key<TagEntity> tagKey : tags) {
            tagsIds.add(tagKey.getName());
        }

        return tagsIds;
    }

    public void setTagsIds(Collection<String> tagsIds) {
        if (tagsIds == null || tagsIds.isEmpty()) {
            tags = null;
            return;
        }

        if (tags == null) {
            tags = new HashSet<>();
        }
        tags.clear();

        for (String tagId : tagsIds) {
            tags.add(Key.create(TagEntity.class, tagId));
        }
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

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public boolean isIncludeInReports() {
        return includeInReports;
    }

    public void setIncludeInReports(boolean includeInReports) {
        this.includeInReports = includeInReports;
    }
}
