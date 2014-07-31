package com.code44.finance.backend.entity;

import com.googlecode.objectify.annotation.Entity;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class Device extends BaseEntity {
    private UserAccount userAccount;

    public static Device find(String id) {
        return ofy().load().type(Device.class).id(id).now();
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        if (userAccount == null) {
            throw new NullPointerException("UserAccount cannot be null.");
        }

        this.userAccount = userAccount;
        if (!userAccount.getDevices().contains(this)) {
            userAccount.addDevice(this);
        }
    }
}
