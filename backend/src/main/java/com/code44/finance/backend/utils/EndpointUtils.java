package com.code44.finance.backend.utils;

import com.code44.finance.backend.endpoints.body.Body;
import com.code44.finance.backend.entities.DeviceEntity;
import com.code44.finance.backend.entities.UserEntity;
import com.code44.finance.common.gcm.CollapseKey;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.google.common.base.Strings;
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static com.code44.finance.backend.OfyService.ofy;

public final class EndpointUtils {
    private static final String API_KEY = System.getProperty("gcm.api.key");

    private static final Logger log = Logger.getLogger(EndpointUtils.class.getName());

    private EndpointUtils() {
    }

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
        if (Strings.isNullOrEmpty(id)) {
            throw new BadRequestException("Id cannot be empty.");
        }
    }

    public static UserEntity getRequiredUserEntity(User user) throws OAuthRequestException, NotFoundException {
        verifyUserNotNull(user);

        final UserEntity userEntity = UserEntity.find(user);
        if (userEntity == null) {
            throw new NotFoundException("User " + user.getEmail() + " is not registered");
        }

        return userEntity;
    }

    public static UserEntity getUserEntityAndVerifyPermissions(User user) throws OAuthRequestException, NotFoundException, ForbiddenException {
        UserEntity userEntity = getRequiredUserEntity(user);
        //        if (!userEntity.isPremium()) {
        //            throw new ForbiddenException("User does not have permission to call this API because it's not a premium account.");
        //        }

        return userEntity;
    }

    public static void notifyDataChanged(UserEntity userEntity, String senderDeviceRegistrationId, CollapseKey collapseKey) throws IOException {
        final List<DeviceEntity> notifyDevices = getOtherDevices(userEntity, senderDeviceRegistrationId);
        if (notifyDevices.isEmpty()) {
            return;
        }

        final Message message = new Message.Builder().collapseKey(collapseKey.name()).build();
        sendGcmMessage(userEntity, notifyDevices, message);
    }

    private static List<DeviceEntity> getOtherDevices(UserEntity userEntity, String senderDeviceRegistrationId) {
        // Get list of user devices
        final List<DeviceEntity> devices = ofy().load()
                .type(DeviceEntity.class)
                .filter("userEntity", Key.create(UserEntity.class, userEntity.getId()))
                .list();
        if (devices == null || devices.isEmpty()) {
            return Collections.emptyList();
        }

        // Check if there are devices other than the sender
        final List<DeviceEntity> notifyDevices = new ArrayList<>();
        for (DeviceEntity device : devices) {
            // TODO For some reason it's still sending to sender device
            if (!device.getRegistrationId().equals(senderDeviceRegistrationId)) {
                notifyDevices.add(device);
            }
        }

        return notifyDevices;
    }

    private static void sendGcmMessage(UserEntity userEntity, List<DeviceEntity> devices, Message message) throws IOException {
        final Sender sender = new Sender(API_KEY);

        for (DeviceEntity device : devices) {
            log.info("Sending GCM to " + userEntity.getFirstName() + " " + userEntity.getLastName() + ", " + device.getDeviceName() + ". Collapse key = '" + message
                    .getCollapseKey() + "'" + " Data = '" + message.getData() + "'");
            final Result result = sender.send(message, device.getRegistrationId(), 5);
            if (result.getMessageId() != null) {
                final String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // The registrationId changed
                    device.setRegistrationId(canonicalRegId);
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
