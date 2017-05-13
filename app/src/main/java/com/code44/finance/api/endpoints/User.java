package com.code44.finance.api.endpoints;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.code44.finance.R;
import com.code44.finance.backend.financius.model.UserEntity;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.preferences.PrefsObject;
import com.google.common.base.Strings;
import com.squareup.otto.Produce;

import static com.google.common.base.Preconditions.checkNotNull;

public class User extends PrefsObject {
    private static final String PREFERENCES_KEY = "user";

    private transient EventBus eventBus;

    private String id;
    private String googleId;
    private String email;
    private String firstName;
    private String lastName;
    private String photoUrl;
    private String coverUrl;

    private long lastConfigUpdateTimestamp;
    private long lastUserUpdateTimestamp;
    private long lastCurrenciesUpdateTimestamp;
    private long lastCategoriesUpdateTimestamp;
    private long lastAccountsUpdateTimestamp;
    private long lastTagsUpdateTimestamp;
    private long lastTransactionsUpdateTimestamp;

    private User() {
    }

    static User getInstance(@NonNull Context context, @NonNull EventBus eventBus) {
        checkNotNull(context, "Context cannot be null.");
        User user = get(context, PREFERENCES_KEY, User.class);
        if (user == null) {
            user = new User();
        }

        user.setContext(context);
        user.eventBus = checkNotNull(eventBus, "EventBus cannot be null.");
        eventBus.register(user);

        return user;
    }

    @Override public void clear() {
        super.clear();

        id = null;
        googleId = null;
        email = null;
        firstName = null;
        lastName = null;
        photoUrl = null;
        coverUrl = null;

        lastConfigUpdateTimestamp = 0;
        lastUserUpdateTimestamp = 0;
        lastCurrenciesUpdateTimestamp = 0;
        lastCategoriesUpdateTimestamp = 0;
        lastAccountsUpdateTimestamp = 0;
        lastTagsUpdateTimestamp = 0;
        lastTransactionsUpdateTimestamp = 0;
    }

    @Override protected String getPreferencesKey() {
        return PREFERENCES_KEY;
    }

    @Produce public User produceUser() {
        return this;
    }

    public void notifyChanged() {
        eventBus.post(this);
    }

    public void updateFromEntity(UserEntity userEntity) {
        setId(userEntity.getId());
        setEmail(userEntity.getEmail());
        setGoogleId(userEntity.getGoogleId());
        setFirstName(userEntity.getFirstName());
        setLastName(userEntity.getLastName());
        setPhotoUrl(userEntity.getPhotoUrl());
        setCoverUrl(userEntity.getCoverUrl());
    }

    public String getName() {
        if (!isLoggedIn()) {
            return getContext().getString(R.string.login);
        }

        if (!Strings.isNullOrEmpty(firstName) || !Strings.isNullOrEmpty(lastName)) {
            return (Strings.isNullOrEmpty(firstName) ? "" : firstName) + (Strings.isNullOrEmpty(lastName) ? "" : " " + lastName);
        }

        if (!Strings.isNullOrEmpty(email)) {
            return email;
        }

        return id;
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        save();
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
        save();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        save();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        save();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        save();
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        save();
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
        save();
    }

    public long getLastConfigUpdateTimestamp() {
        return lastConfigUpdateTimestamp;
    }

    public void setLastConfigUpdateTimestamp(long lastConfigUpdateTimestamp) {
        this.lastConfigUpdateTimestamp = lastConfigUpdateTimestamp;
        save();
    }

    public long getLastUserUpdateTimestamp() {
        return lastUserUpdateTimestamp;
    }

    public void setLastUserUpdateTimestamp(long lastUserUpdateTimestamp) {
        this.lastUserUpdateTimestamp = lastUserUpdateTimestamp;
        save();
    }

    public long getLastCurrenciesUpdateTimestamp() {
        return lastCurrenciesUpdateTimestamp;
    }

    public void setLastCurrenciesUpdateTimestamp(long lastCurrenciesUpdateTimestamp) {
        this.lastCurrenciesUpdateTimestamp = lastCurrenciesUpdateTimestamp;
        save();
    }

    public long getLastCategoriesUpdateTimestamp() {
        return lastCategoriesUpdateTimestamp;
    }

    public void setLastCategoriesUpdateTimestamp(long lastCategoriesUpdateTimestamp) {
        this.lastCategoriesUpdateTimestamp = lastCategoriesUpdateTimestamp;
        save();
    }

    public long getLastAccountsUpdateTimestamp() {
        return lastAccountsUpdateTimestamp;
    }

    public void setLastAccountsUpdateTimestamp(long lastAccountsUpdateTimestamp) {
        this.lastAccountsUpdateTimestamp = lastAccountsUpdateTimestamp;
        save();
    }

    public long getLastTagsUpdateTimestamp() {
        return lastTagsUpdateTimestamp;
    }

    public void setLastTagsUpdateTimestamp(long lastTagsUpdateTimestamp) {
        this.lastTagsUpdateTimestamp = lastTagsUpdateTimestamp;
        save();
    }

    public long getLastTransactionsUpdateTimestamp() {
        return lastTransactionsUpdateTimestamp;
    }

    public void setLastTransactionsUpdateTimestamp(long lastTransactionsUpdateTimestamp) {
        this.lastTransactionsUpdateTimestamp = lastTransactionsUpdateTimestamp;
        save();
    }
}
