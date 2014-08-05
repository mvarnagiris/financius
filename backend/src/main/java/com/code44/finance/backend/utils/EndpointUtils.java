package com.code44.finance.backend.utils;

import com.code44.finance.backend.endpoint.body.Body;
import com.code44.finance.backend.entity.DeviceEntity;
import com.code44.finance.backend.entity.UserAccount;
import com.code44.finance.common.utils.StringUtils;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.util.List;

import static com.code44.finance.backend.OfyService.ofy;

public class EndpointUtils {
    private static final String API_KEY = System.getProperty("gcm.api.key");

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
            throw new NotFoundException("User " + user.getEmail() + " is not registered");
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

    public static void notifyOtherDevices(UserAccount userAccount, String senderDeviceRedId) throws IOException {
        // Get list of user devices
        final List<DeviceEntity> devices = ofy()
                .load()
                .type(DeviceEntity.class)
                .filter("userAccount", Key.create(UserAccount.class, userAccount.getId()))
                .list();
        if (devices == null) {
            return;
        }

        // Check if there are devices other than the sender
        boolean hasOtherDevices = false;
        DeviceEntity senderDevice = null;
        for (DeviceEntity device : devices) {
            if (!device.getId().equals(senderDeviceRedId)) {
                hasOtherDevices = true;
                break;
            } else {
                senderDevice = device;
            }
        }
        if (!hasOtherDevices) {
            return;
        }
        devices.remove(senderDevice);

        final Sender sender = new Sender(API_KEY);
        final Message message = new Message.Builder()
                .collapseKey("notify")
                .build();

        for (DeviceEntity device : devices) {
            final Result result = sender.send(message, device.getId(), 5);
            if (result.getMessageId() != null) {
                final String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // The regId changed
                    device.setId(canonicalRegId);
                    ofy().save().entity(device).now();
                }
            } else {
                final String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    // The device is no longer registered with Gcm
                    ofy().delete().entity(device).now();
                }
            }
        }
    }
}
