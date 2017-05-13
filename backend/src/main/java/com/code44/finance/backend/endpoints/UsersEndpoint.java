package com.code44.finance.backend.endpoints;

import com.code44.finance.backend.endpoints.body.UserBody;
import com.code44.finance.backend.entities.ConfigEntity;
import com.code44.finance.backend.entities.UserEntity;
import com.code44.finance.backend.utils.EndpointUtils;
import com.code44.finance.common.gcm.CollapseKey;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

import java.io.IOException;

import static com.code44.finance.backend.OfyService.ofy;

@ApiReference(Endpoint.class)
public class UsersEndpoint {
    private static final String PATH = "users";

    @ApiMethod(name = "registerUser", httpMethod = "POST", path = PATH) public UserEntity register(UserBody body, User user) throws OAuthRequestException, BadRequestException, IOException {
        EndpointUtils.verifyUserNotNull(user);
        EndpointUtils.verifyBodyNotNull(body);

        UserEntity userEntity = UserEntity.find(user);
        if (userEntity == null) {
            body.verifyRequiredFields();

            userEntity = new UserEntity();
            userEntity.onCreate();
            userEntity.setEmail(user.getEmail());
        } else {
            userEntity.onUpdate();
        }

        updateEntity(userEntity, body);
        ofy().save().entity(userEntity).now();

        // Update timestamp
        final ConfigEntity configEntity = ConfigEntity.find(userEntity);
        final long systemTimestamp = System.currentTimeMillis();
        if (configEntity != null) {
            configEntity.setUserUpdateTimestamp(systemTimestamp);
            ofy().save().entity(configEntity).now();
        }

        EndpointUtils.notifyDataChanged(userEntity, null, CollapseKey.DataChanged);

        return userEntity;
    }

    @ApiMethod(name = "getUserMe", httpMethod = "GET", path = PATH + "/me") public UserEntity getMe(User user) throws OAuthRequestException, BadRequestException, NotFoundException {
        return EndpointUtils.getRequiredUserEntity(user);
    }

    private void updateEntity(UserEntity entity, UserBody body) {
        if (body.getGoogleId() != null) {
            entity.setGoogleId(body.getGoogleId());
        }

        if (body.getFirstName() != null) {
            entity.setFirstName(body.getFirstName());
        }

        if (body.getLastName() != null) {
            entity.setLastName(body.getLastName());
        }

        if (body.getPhotoUrl() != null) {
            entity.setPhotoUrl(body.getPhotoUrl());
        }

        if (body.getCoverUrl() != null) {
            entity.setCoverUrl(body.getCoverUrl());
        }
    }
}
