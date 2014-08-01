package com.code44.finance.backend.endpoint;

import com.code44.finance.backend.endpoint.body.RegisterBody;
import com.code44.finance.backend.endpoint.body.RegisterDeviceBody;
import com.code44.finance.backend.entity.Device;
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

        updateUserAccountFromBody(userAccount, body);
        ofy().save().entity(userAccount).now();

        return userAccount;
    }

    @ApiMethod(name = "get", httpMethod = "GET", path = "{id}")
    public UserAccount get(@Named("id") String id, User user) throws OAuthRequestException, BadRequestException, NotFoundException, ForbiddenException {
        EndpointUtils.verifyIdNotEmpty(id);
        EndpointUtils.verifyUserNotNull(user);

        final UserAccount userAccount = EndpointUtils.getUserAccount(user);
        if (userAccount == null) {
            throw new NotFoundException("User not found.");
        }

        return userAccount;
    }

    @ApiMethod(name = "registerDevice", httpMethod = "POST", path = "devices")
    public Device registerDevice(RegisterDeviceBody body, User user) throws OAuthRequestException, BadRequestException, ForbiddenException {
        EndpointUtils.verifyUserNotNull(user);
        EndpointUtils.verifyBodyNotNull(body);

        final UserAccount userAccount = EndpointUtils.getUserAccountAndVerifyPermissions(user);

        body.verifyRequiredFields();
        Device device = Device.find(body.getRegId());
        if (device == null) {
            device = new Device();
            device.onCreate();
        } else {
            device.onUpdate();
        }

        updateDeviceFromBody(device, body);
        ofy().save().entity(device).now();
        userAccount.addDevice(device);

        return device;
    }

    @ApiMethod(name = "unregisterDevice", httpMethod = "DELETE", path = "devices/{id}")
    public void unregisterDevice(@Named("id") String id, User user) throws OAuthRequestException, BadRequestException, ForbiddenException, NotFoundException {
        EndpointUtils.verifyUserNotNull(user);
        EndpointUtils.verifyIdNotEmpty(id);

        final UserAccount userAccount = EndpointUtils.getUserAccountAndVerifyPermissions(user);

        final Device device = Device.find(id);
        if (device == null) {
            throw new NotFoundException("Device not found.");
        }

        userAccount.getDevices().remove(device);
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

    private void updateDeviceFromBody(Device device, RegisterDeviceBody body) {
        if (body.getRegId() != null) {
            device.setId(body.getRegId());
        }
    }
}
