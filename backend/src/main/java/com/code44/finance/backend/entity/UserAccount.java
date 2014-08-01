package com.code44.finance.backend.entity;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import java.util.HashSet;
import java.util.Set;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class UserAccount extends BaseEntity {
    private String googleId;
    @Index
    private String email;
    private String photoUrl;
    private String coverUrl;
    private String firstName;
    private String lastName;
    private boolean isPremium;
    private Set<Device> devices = new HashSet<Device>();

    public static UserAccount find(User user) {
        return ofy().load().type(UserAccount.class).filter("email", user.getEmail()).first().now();
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean isPremium) {
        this.isPremium = isPremium;
    }

    public Set<Device> getDevices() {
        return devices;
    }

    public void addDevice(Device device) {
        if (device == null) {
            throw new NullPointerException("Device cannot be null.");
        }

        this.devices.add(device);
        if (device.getUserAccount() != this) {
            device.setUserAccount(this);
        }
    }
}
