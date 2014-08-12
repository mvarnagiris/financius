package com.code44.finance.api.requests;

import android.content.Context;

import com.code44.finance.api.Request;
import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.users.Users;
import com.code44.finance.backend.endpoint.users.model.RegisterBody;
import com.code44.finance.backend.endpoint.users.model.UserAccount;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.services.StartupService;
import com.code44.finance.utils.EventBus;

public class RegisterRequest extends Request {
    private final Context context;
    private final Users usersService;
    private final User user;
    private final DBHelper dbHelper;
    private final RegisterBody body;

    public RegisterRequest(EventBus eventBus, Context context, Users usersService, User user, DBHelper dbHelper, String email, String googleId, String firstName, String lastName, String photoUrl, String coverUrl) {
        super(eventBus);

        Preconditions.checkNotNull(eventBus, "EventBus cannot be null.");
        Preconditions.checkNotNull(context, "Context cannot be null.");
        Preconditions.checkNotNull(usersService, "Users cannot be null.");
        Preconditions.checkNotNull(dbHelper, "DBHelper cannot be null.");
        Preconditions.checkNotEmpty(email, "Email cannot be empty.");
        Preconditions.checkNotEmpty(googleId, "Google Id cannot be empty.");
        Preconditions.checkNotEmpty(firstName, "First name cannot be empty.");

        this.context = context;
        this.usersService = usersService;
        this.user = user;
        this.dbHelper = dbHelper;

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
