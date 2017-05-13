package com.code44.finance.backend.endpoints;

import com.code44.finance.backend.endpoints.body.DeviceBody;
import com.code44.finance.backend.entities.DeviceEntity;
import com.code44.finance.backend.entities.UserEntity;
import com.code44.finance.backend.utils.EndpointUtils;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;

import javax.inject.Named;

import static com.code44.finance.backend.OfyService.ofy;

@ApiReference(Endpoint.class)
public class DevicesEndpoint {
    private static final String PATH = "devices";

    @ApiMethod(name = "registerDevice", httpMethod = "POST", path = PATH) public DeviceEntity register(DeviceBody body, User user) throws OAuthRequestException, BadRequestException, NotFoundException, ForbiddenException {
        final UserEntity userEntity = EndpointUtils.getUserEntityAndVerifyPermissions(user);
        EndpointUtils.verifyBodyNotNull(body);

        body.verifyRequiredFields();
        DeviceEntity deviceEntity = DeviceEntity.findWithRegistrationId(body.getRegistrationId());
        if (deviceEntity == null) {
            deviceEntity = new DeviceEntity();
            deviceEntity.onCreate();
        } else {
            deviceEntity.onUpdate();
        }

        deviceEntity.setUserEntity(Key.create(UserEntity.class, userEntity.getId()));
        updateEntity(deviceEntity, body);
        ofy().save().entity(deviceEntity).now();

        return deviceEntity;
    }

    @ApiMethod(name = "unregisterDevice", httpMethod = "DELETE", path = PATH + "/{id}") public void unregister(@Named("id") String id, User user) throws OAuthRequestException, BadRequestException, NotFoundException, ForbiddenException {
        EndpointUtils.getUserEntityAndVerifyPermissions(user);
        EndpointUtils.verifyIdNotEmpty(id);

        final DeviceEntity device = DeviceEntity.find(id);
        if (device == null) {
            throw new NotFoundException("Device not found.");
        }

        ofy().delete().entity(device).now();
    }

    @ApiMethod(name = "getDevice", httpMethod = "GET", path = PATH + "/{id}") public DeviceEntity get(@Named("id") String id, User user) throws OAuthRequestException, BadRequestException, NotFoundException, ForbiddenException {
        EndpointUtils.getUserEntityAndVerifyPermissions(user);
        EndpointUtils.verifyIdNotEmpty(id);

        final DeviceEntity device = DeviceEntity.find(id);
        if (device == null) {
            throw new NotFoundException("Device not found.");
        }

        return device;
    }

    private void updateEntity(DeviceEntity entity, DeviceBody body) {
        entity.setRegistrationId(body.getRegistrationId());

        if (body.getDeviceName() != null) {
            entity.setDeviceName(body.getDeviceName());
        }
    }
}
