package com.code44.finance.api.endpoints.requests.users;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.EndpointRequest;
import com.code44.finance.api.endpoints.EndpointsApi;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.backend.financius.model.UserBody;
import com.code44.finance.backend.financius.model.UserEntity;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.utils.EventBus;
import com.google.android.gms.auth.GoogleAuthUtil;

import static com.google.common.base.Preconditions.checkNotNull;

public class RegisterRequest extends EndpointRequest<UserEntity> {
    private final Context context;
    private final EndpointsApi endpointsApi;
    private final User user;
    private final DBHelper dbHelper;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String photoUrl;
    private final String coverUrl;

    public RegisterRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull EndpointsApi endpointsApi, @NonNull User user, @NonNull DBHelper dbHelper, @NonNull String email, @Nullable String firstName, @Nullable String lastName, @Nullable String photoUrl, @Nullable String coverUrl) {
        super(checkNotNull(eventBus, "EventBus cannot be null."), endpointFactory);
        this.context = checkNotNull(context, "Context cannot be null.");
        this.endpointsApi = checkNotNull(endpointsApi, "Endpoints Api cannot be null.");
        this.user = checkNotNull(user, "User cannot be null.");
        this.dbHelper = checkNotNull(dbHelper, "DBHelper cannot be null.");
        this.email = checkNotNull(email, "Email cannot be null.");
        this.firstName = firstName;
        this.lastName = lastName;
        this.photoUrl = photoUrl;
        this.coverUrl = coverUrl;
    }

    @Override protected UserEntity performRequest() throws Exception {
        final UserBody userBody = new UserBody();
        userBody.setGoogleId(GoogleAuthUtil.getAccountId(context, email));
        userBody.setFirstName(firstName);
        userBody.setLastName(lastName);
        userBody.setPhotoUrl(photoUrl);
        userBody.setCoverUrl(coverUrl);

        final UserEntity userEntity = getEndpoint().registerUser(userBody).execute();
        user.updateFromEntity(userEntity);
        user.notifyChanged();

        final boolean isExistingUser = !userEntity.getCreateTs().equals(userEntity.getEditTs());
        if (isExistingUser) {
            dbHelper.clear();
        } else {
            endpointsApi.syncConfig();
        }

        endpointsApi.registerDevice(context);
        endpointsApi.syncModels();

        return userEntity;
    }
}
