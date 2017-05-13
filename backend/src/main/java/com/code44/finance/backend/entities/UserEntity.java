package com.code44.finance.backend.entities;

import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class UserEntity extends BaseEntity {
    @Index @ApiResourceProperty(name = "email") private String email;

    @ApiResourceProperty(name = "google_id") private String googleId;

    @ApiResourceProperty(name = "photo_url") private String photoUrl;

    @ApiResourceProperty(name = "cover_url") private String coverUrl;

    @ApiResourceProperty(name = "first_name") private String firstName;

    @ApiResourceProperty(name = "last_name") private String lastName;

    public static UserEntity find(User user) {
        return ofy().load().type(UserEntity.class).filter("email", user.getEmail()).first().now();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
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
}
