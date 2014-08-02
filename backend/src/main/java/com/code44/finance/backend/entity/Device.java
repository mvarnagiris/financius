package com.code44.finance.backend.entity;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class Device extends BaseEntity {
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<UserAccount> userAccount;

    public static Device find(String id) {
        return ofy().load().type(Device.class).id(id).now();
    }

    public Key<UserAccount> getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(Key<UserAccount> userAccount) {
        this.userAccount = userAccount;
    }
}
