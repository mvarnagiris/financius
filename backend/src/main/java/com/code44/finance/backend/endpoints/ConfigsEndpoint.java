package com.code44.finance.backend.endpoints;

import com.code44.finance.backend.endpoints.body.ConfigBody;
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
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.code44.finance.backend.OfyService.ofy;

@ApiReference(Endpoint.class)
public class ConfigsEndpoint {
    private static final String PATH = "configs";

    private static final Logger log = Logger.getLogger(ConfigsEndpoint.class.getName());

    @ApiMethod(name = "setConfig", httpMethod = "POST", path = PATH) public ConfigEntity setConfig(ConfigBody body, User user) throws OAuthRequestException, BadRequestException, NotFoundException, IOException {
        log.entering(getClass().getSimpleName(), "setConfig");
        final UserEntity userEntity = EndpointUtils.getRequiredUserEntity(user);
        EndpointUtils.verifyBodyNotNull(body);
        body.verifyRequiredFields();

        ConfigEntity configEntity = ConfigEntity.find(userEntity);
        if (configEntity == null) {
            configEntity = new ConfigEntity();
            configEntity.onCreate();
            configEntity.setUserEntity(Key.create(UserEntity.class, userEntity.getId()));
        } else {
            configEntity.onUpdate();
        }
        updateEntity(configEntity, body);

        ofy().save().entity(configEntity).now();
        EndpointUtils.notifyDataChanged(userEntity, body.getDeviceRegistrationId(), CollapseKey.DataChanged);

        log.exiting(getClass().getSimpleName(), "setConfig");
        return configEntity;
    }

    @ApiMethod(name = "getConfig", httpMethod = "GET", path = PATH) public ConfigEntity getConfig(User user) throws OAuthRequestException, BadRequestException, NotFoundException {
        log.entering(getClass().getSimpleName(), "getConfig");
        final UserEntity userEntity = EndpointUtils.getRequiredUserEntity(user);
        final ConfigEntity configEntity = ConfigEntity.find(userEntity);
        if (configEntity == null) {
            throw new NotFoundException("Config not found.");
        }
        log.exiting(getClass().getSimpleName(), "getConfig", configEntity);
        return configEntity;
    }

    private void updateEntity(ConfigEntity entity, ConfigBody body) {
        entity.setCurrencyCode(body.getCurrencyCode());
        entity.setIntervalType(body.getIntervalType());
        entity.setIntervalLength(body.getIntervalLength());
        entity.setSecurityType(body.getSecurityType());
        entity.setPassword(body.getPassword());
        entity.setConfigUpdateTimestamp(System.currentTimeMillis());
    }

    static {
        log.setLevel(Level.ALL);
    }
}
