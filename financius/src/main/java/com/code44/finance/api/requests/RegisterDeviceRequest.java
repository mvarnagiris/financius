package com.code44.finance.api.requests;

import android.text.TextUtils;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.Request;
import com.code44.finance.backend.endpoint.users.Users;
import com.code44.finance.backend.endpoint.users.model.RegisterDeviceBody;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import javax.inject.Inject;

public class RegisterDeviceRequest extends Request {
    private static final String PROJECT_NUMBER = "1007413878843";

    @Inject Users usersService;
    @Inject GcmRegistration gcmRegistration;

    public RegisterDeviceRequest() {
        super();
    }

    @Override
    protected void performRequest() throws Exception {
        String registrationId = gcmRegistration.getRegistrationId();

        // Register with GCM if necessary
        if (TextUtils.isEmpty(registrationId)) {
            final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            registrationId = gcm.register(PROJECT_NUMBER);
            gcmRegistration.setRegistrationId(registrationId);
        }

        // Prepare registration body
        final RegisterDeviceBody body = new RegisterDeviceBody();
        body.setRegId(registrationId);

        // Register device with AppEngine
        usersService.registerDevice(body).execute();
        gcmRegistration.setRegisteredWithServer(true);
    }
}
