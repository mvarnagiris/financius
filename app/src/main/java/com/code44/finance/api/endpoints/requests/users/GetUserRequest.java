package com.code44.finance.api.endpoints.requests.users;

import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.EndpointRequest;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.backend.financius.model.UserEntity;
import com.code44.finance.utils.EventBus;

import static com.google.common.base.Preconditions.checkNotNull;

public class GetUserRequest extends EndpointRequest<UserEntity> {
    private final User user;

    public GetUserRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull User user) {
        super(checkNotNull(eventBus, "EventBus cannot be null"), endpointFactory);
        this.user = checkNotNull(user, "User cannot be null.");
    }

    @Override protected UserEntity performRequest() throws Exception {
        final UserEntity userEntity = getEndpoint().getUserMe().execute();
        user.updateFromEntity(userEntity);
        user.notifyChanged();
        return userEntity;
    }
}
