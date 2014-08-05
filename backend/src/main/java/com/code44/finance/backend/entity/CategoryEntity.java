package com.code44.finance.backend.entity;

import com.code44.finance.common.model.CategoryType;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class CategoryEntity extends BaseEntity {
    @Index
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<UserAccount> userAccount;

    @ApiResourceProperty(name = "title")
    private String title;

    @ApiResourceProperty(name = "color")
    private int color;

    @ApiResourceProperty(name = "category_type")
    private CategoryType categoryType;

    @ApiResourceProperty(name = "sort_order")
    private int sortOrder;

    public static CategoryEntity find(String id) {
        return ofy().load().type(CategoryEntity.class).id(id).now();
    }

    public Key<UserAccount> getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(Key<UserAccount> userAccount) {
        this.userAccount = userAccount;
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

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
