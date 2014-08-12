package com.code44.finance.backend.entity;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class TagEntity extends BaseEntity {
    @Index
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<UserAccount> userAccount;

    @ApiResourceProperty(name = "title")
    private String title;

    public static TagEntity find(String id) {
        return ofy().load().type(TagEntity.class).id(id).now();
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
}
