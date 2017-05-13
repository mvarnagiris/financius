package com.code44.finance.backend.entities;

import com.code44.finance.common.model.TransactionType;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.annotation.Entity;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class CategoryEntity extends BaseUserEntity {
    @ApiResourceProperty(name = "title") private String title;

    @ApiResourceProperty(name = "color") private int color;

    @ApiResourceProperty(name = "transaction_type") private TransactionType transactionType;

    public static CategoryEntity find(String id) {
        return ofy().load().type(CategoryEntity.class).id(id).now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
