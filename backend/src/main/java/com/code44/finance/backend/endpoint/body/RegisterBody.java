package com.code44.finance.backend.endpoint.body;

import com.code44.finance.common.utils.Strings;
import com.google.api.server.spi.response.BadRequestException;
import com.google.gson.annotations.SerializedName;

public class RegisterBody implements Body {
    @SerializedName(value = "google_id")
    private String googleId;

    @SerializedName(value = "first_name")
    private String firstName;

    @SerializedName(value = "last_name")
    private String lastName;

    @SerializedName(value = "photo_url")
    private String photoUrl;

    @SerializedName(value = "cover_url")
    private String coverUrl;

    public void verifyRequiredFields() throws BadRequestException {
        if (Strings.isEmpty(googleId)) {
            throw new BadRequestException("google_id cannot be empty.");
        }

        if (Strings.isEmpty(firstName)) {
            throw new BadRequestException("first_name cannot be empty.");
        }
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
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
}
