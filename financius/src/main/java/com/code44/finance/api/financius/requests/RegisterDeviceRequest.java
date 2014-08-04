package com.code44.finance.api.financius.requests;

import android.content.Context;
import android.text.TextUtils;

import com.code44.finance.api.BaseRequest;
import com.code44.finance.api.BaseRequestEvent;
import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.users.model.RegisterDeviceBody;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegisterDeviceRequest extends FinanciusBaseRequest<Void> {
    private static final String PROJECT_NUMBER = "1007413878843";

    public RegisterDeviceRequest(Context context, User user) {
        super(null, context, user);
    }

    @Override
    protected Void performRequest() throws Exception {
        final GcmRegistration gcmRegistration = GcmRegistration.get();
        String registrationId = GcmRegistration.get().getRegistrationId();

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
        getUsersService().registerDevice(body).execute();
        GcmRegistration.get().setRegisteredWithServer(true);

        return null;
    }

    @Override
    protected BaseRequestEvent<Void, ? extends BaseRequest<Void>> createEvent(Void result, Exception error, BaseRequestEvent.State state) {
        return new RegisterDeviceRequestEvent(this, result, error, state);
    }

    public static class RegisterDeviceRequestEvent extends BaseRequestEvent<Void, RegisterDeviceRequest> {
        protected RegisterDeviceRequestEvent(RegisterDeviceRequest request, Void result, Exception error, State state) {
            super(request, result, error, state);
        }
    }
}
