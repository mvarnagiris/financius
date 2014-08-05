package com.code44.finance.backend.endpoint;

import com.code44.finance.backend.endpoint.body.RegisterBody;
import com.code44.finance.backend.endpoint.body.RegisterDeviceBody;
import com.code44.finance.backend.entity.DeviceEntity;
import com.code44.finance.backend.entity.UserAccount;
import com.code44.finance.backend.utils.EndpointUtils;
import com.code44.finance.common.Constants;
import com.google.api.server.spi.Constant;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;

import javax.inject.Named;

import static com.code44.finance.backend.OfyService.ofy;

@Api(
        name = "users",
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constant.API_EXPLORER_CLIENT_ID, Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID},
        audiences = {Constants.ANDROID_AUDIENCE},
        namespace = @ApiNamespace(
                ownerDomain = "endpoint.backend.finance.code44.com",
                ownerName = "endpoint.backend.finance.code44.com",
                packagePath = ""
        )
)
public class UsersEndpoint {
    @ApiMethod(name = "register", httpMethod = "POST", path = "")
    public UserAccount register(RegisterBody body, User user) throws OAuthRequestException, BadRequestException {
        EndpointUtils.verifyUserNotNull(user);
        EndpointUtils.verifyBodyNotNull(body);

        UserAccount userAccount = UserAccount.find(user);
        if (userAccount == null) {
            body.verifyRequiredFields();

            userAccount = new UserAccount();
            userAccount.onCreate();
            userAccount.setEmail(user.getEmail());
        } else {
            userAccount.onUpdate();
        }

        // TODO Remove setPremium, when IAB is implemented
        userAccount.setPremium(true);

        updateUserAccountFromBody(userAccount, body);
        ofy().save().entity(userAccount).now();

        return userAccount;
    }

    @ApiMethod(name = "get", httpMethod = "GET", path = "{id}")
    public UserAccount get(@Named("id") String id, User user) throws OAuthRequestException, BadRequestException, NotFoundException, ForbiddenException {
        EndpointUtils.verifyIdNotEmpty(id);
        EndpointUtils.verifyUserNotNull(user);

        return EndpointUtils.getUserAccount(user);
    }

    @ApiMethod(name = "registerDevice", httpMethod = "POST", path = "devices")
    public DeviceEntity registerDevice(RegisterDeviceBody body, User user) throws OAuthRequestException, BadRequestException, ForbiddenException, NotFoundException {
        EndpointUtils.verifyUserNotNull(user);
        EndpointUtils.verifyBodyNotNull(body);

        final UserAccount userAccount = EndpointUtils.getUserAccountAndVerifyPermissions(user);

        body.verifyRequiredFields();
        DeviceEntity device = DeviceEntity.find(body.getRegId());
        if (device == null) {
            device = new DeviceEntity();
            device.onCreate();
        } else {
            device.onUpdate();
        }

        device.setUserAccount(Key.create(UserAccount.class, userAccount.getId()));
        updateDeviceFromBody(device, body);
        ofy().save().entity(device).now();

        return device;
    }

    @ApiMethod(name = "unregisterDevice", httpMethod = "DELETE", path = "devices/{id}")
    public void unregisterDevice(@Named("id") String id, User user) throws OAuthRequestException, BadRequestException, ForbiddenException, NotFoundException {
        EndpointUtils.verifyUserNotNull(user);
        EndpointUtils.verifyIdNotEmpty(id);

        EndpointUtils.getUserAccountAndVerifyPermissions(user);

        final DeviceEntity device = DeviceEntity.find(id);
        if (device == null) {
            throw new NotFoundException("Device not found.");
        }

        ofy().delete().entity(device).now();
    }

    private void updateUserAccountFromBody(UserAccount userAccount, RegisterBody body) {
        if (body.getGoogleId() != null) {
            userAccount.setGoogleId(body.getGoogleId());
        }

        if (body.getFirstName() != null) {
            userAccount.setFirstName(body.getFirstName());
        }

        if (body.getLastName() != null) {
            userAccount.setLastName(body.getLastName());
        }

        if (body.getPhotoUrl() != null) {
            userAccount.setPhotoUrl(body.getPhotoUrl());
        }

        if (body.getCoverUrl() != null) {
            userAccount.setCoverUrl(body.getCoverUrl());
        }
    }

    private void updateDeviceFromBody(DeviceEntity device, RegisterDeviceBody body) {
        if (body.getRegId() != null) {
            device.setId(body.getRegId());
        }
    }
}
