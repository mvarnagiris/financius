package com.code44.finance.api.endpoints.requests.devices;

import android.content.Context;
import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.Device;
import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.EndpointRequest;
import com.code44.finance.backend.financius.model.DeviceBody;
import com.code44.finance.backend.financius.model.DeviceEntity;
import com.code44.finance.common.Constants;
import com.code44.finance.utils.EventBus;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkNotNull;

public class RegisterDeviceRequest extends EndpointRequest<DeviceEntity> {
    private final Context context;
    private final Device device;

    public RegisterDeviceRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull Device device) {
        super(eventBus, endpointFactory);
        this.context = checkNotNull(context, "Context cannot be null.").getApplicationContext();
        this.device = checkNotNull(device, "Device cannot be null.");
    }

    @Override protected DeviceEntity performRequest() throws Exception {
        String registrationId = device.getRegistrationId();

        // Register with GCM if necessary
        if (Strings.isNullOrEmpty(registrationId)) {
            final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            registrationId = gcm.register(Constants.PROJECT_NUMBER);
            device.setRegistrationId(registrationId);
        }

        // Prepare registration body
        final DeviceBody body = new DeviceBody();
        body.setRegistrationId(registrationId);
        body.setDeviceName(android.os.Build.MODEL);

        // Register device with AppEngine
        final DeviceEntity deviceEntity = getEndpoint().registerDevice(body).execute();
        device.setDeviceName(deviceEntity.getDeviceName());
        device.setRegisteredWithServer(true);

        return deviceEntity;
    }
}
