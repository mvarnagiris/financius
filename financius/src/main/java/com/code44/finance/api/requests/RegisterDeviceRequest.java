package com.code44.finance.api.requests;

import android.content.Context;
import android.text.TextUtils;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.Request;
import com.code44.finance.backend.endpoint.users.Users;
import com.code44.finance.backend.endpoint.users.model.RegisterDeviceBody;
import com.code44.finance.common.utils.Preconditions;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegisterDeviceRequest extends Request {
    private static final String PROJECT_NUMBER = "1007413878843";

    private final Context context;
    private final Users usersService;
    private final GcmRegistration gcmRegistration;

    public RegisterDeviceRequest(Context context, Users usersService, GcmRegistration gcmRegistration) {
        super(null);

        Preconditions.notNull(context, "Context cannot be null.");
        Preconditions.notNull(usersService, "Users cannot be null.");
        Preconditions.notNull(gcmRegistration, "GCM Registration cannot be null.");

        this.context = context;
        this.usersService = usersService;
        this.gcmRegistration = gcmRegistration;
    }

    @Override protected Object performRequest() throws Exception {
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
        return null;
    }
}
