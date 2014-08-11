package com.code44.finance.api.requests;

import com.code44.finance.api.Request;
import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.users.Users;
import com.code44.finance.backend.endpoint.users.model.RegisterBody;
import com.code44.finance.backend.endpoint.users.model.UserAccount;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.services.StartupService;

import javax.inject.Inject;

public class RegisterRequest extends Request {
    private final RegisterBody body;
    @Inject User user;
    @Inject DBHelper dbHelper;
    @Inject Users usersService;

    public RegisterRequest(String email, String googleId, String firstName, String lastName, String photoUrl, String coverUrl) {
        super();

        user.setEmail(email);

        body = new RegisterBody();
        body.setGoogleId(googleId);
        body.setFirstName(firstName);
        body.setLastName(lastName);
        body.setPhotoUrl(photoUrl);
        body.setCoverUrl(coverUrl);
    }

    @Override
    protected void performRequest() throws Exception {
        try {
            final UserAccount userAccount = usersService.register(body).execute();

            user.setId(userAccount.getId());
            user.setEmail(userAccount.getEmail());
            user.setGoogleId(userAccount.getGoogleId());
            user.setFirstName(userAccount.getFirstName());
            user.setLastName(userAccount.getLastName());
            user.setPhotoUrl(userAccount.getPhotoUrl());
            user.setCoverUrl(userAccount.getCoverUrl());
            user.setPremium(userAccount.getPremium());

            final boolean isExistingUser = !userAccount.getCreateTs().equals(userAccount.getEditTs());
            if (isExistingUser) {
                dbHelper.clear();
            }
            StartupService.start(context);
        } catch (Exception e) {
            e.printStackTrace();
            user.clear();
            throw e;
        }
        user.notifyChanged();
    }
}
