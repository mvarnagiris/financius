package com.code44.finance.backend.utils;

import com.code44.finance.backend.endpoint.body.Body;
import com.code44.finance.backend.entity.UserAccount;
import com.code44.finance.common.utils.StringUtils;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

public class EndpointUtils {
    public static void verifyUserNotNull(User user) throws OAuthRequestException {
        if (user == null) {
            throw new OAuthRequestException("Requires authentication.");
        }
    }

    public static void verifyBodyNotNull(Body body) throws BadRequestException {
        if (body == null) {
            throw new BadRequestException("Body cannot be empty.");
        }
    }

    public static void verifyIdNotEmpty(String id) throws BadRequestException {
        if (StringUtils.isEmpty(id)) {
            throw new BadRequestException("Id cannot be empty.");
        }
    }

    public static UserAccount getUserAccount(User user) throws OAuthRequestException, NotFoundException {
        verifyUserNotNull(user);

        final UserAccount userAccount = UserAccount.find(user);
        if (userAccount == null) {
            throw new NotFoundException("User is not registered");
        }

        return userAccount;
    }

    public static UserAccount getUserAccountAndVerifyPermissions(User user) throws OAuthRequestException, NotFoundException, ForbiddenException {
        UserAccount userAccount = getUserAccount(user);
        if (!userAccount.isPremium()) {
            throw new ForbiddenException("User does not have permission to call this API because it's not a premium account.");
        }

        return userAccount;
    }
}
