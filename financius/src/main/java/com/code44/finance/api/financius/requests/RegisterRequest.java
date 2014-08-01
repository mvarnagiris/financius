package com.code44.finance.api.financius.requests;

import android.content.Context;

import com.code44.finance.api.BaseRequest;
import com.code44.finance.api.BaseRequestEvent;
import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.users.Users;
import com.code44.finance.backend.endpoint.users.model.RegisterBody;
import com.code44.finance.backend.endpoint.users.model.UserAccount;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

public class RegisterRequest extends FinanciusBaseRequest<User> {
    private final RegisterBody body;

    public RegisterRequest(Context context, User user, String email, String googleId, String firstName, String lastName, String photoUrl, String coverUrl) {
        super(null, context, user);

        user.setEmail(email);

        body = new RegisterBody();
        body.setGoogleId(googleId);
        body.setFirstName(firstName);
        body.setLastName(lastName);
        body.setPhotoUrl(photoUrl);
        body.setCoverUrl(coverUrl);
    }

    @Override
    protected User performRequest(HttpTransport httpTransport, JsonFactory jsonFactory, HttpRequestInitializer httpRequestInitializer) throws Exception {
        try {
            final UserAccount userAccount = new Users.Builder(httpTransport, jsonFactory, httpRequestInitializer)
                    .build()
                    .register(body)
                    .execute();

            user.setId(userAccount.getId());
            user.setEmail(userAccount.getEmail());
            user.setGoogleId(userAccount.getGoogleId());
            user.setFirstName(userAccount.getFirstName());
            user.setLastName(userAccount.getLastName());
            user.setPremium(userAccount.getPremium());
        } catch (Exception e) {
            user.clear();
            throw e;
        }
        User.notifyUserChanged();

        return user;
    }

    @Override
    protected BaseRequestEvent<User, ? extends BaseRequest<User>> createEvent(User result, Exception error, BaseRequestEvent.State state) {
        return new RegisterRequestEvent(this, result, error, state);
    }

    public static class RegisterRequestEvent extends BaseRequestEvent<User, RegisterRequest> {
        protected RegisterRequestEvent(RegisterRequest request, User result, Exception error, State state) {
            super(request, result, error, state);
        }
    }
}
