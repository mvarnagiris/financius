package com.code44.finance.api.endpoints.requests.devices;

import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.Device;
import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.EndpointRequest;
import com.code44.finance.utils.EventBus;

import static com.google.common.base.Preconditions.checkNotNull;

public class UnregisterDeviceRequest extends EndpointRequest<Void> {
    private final Device device;

    public UnregisterDeviceRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Device device) {
        super(eventBus, endpointFactory);
        this.device = checkNotNull(device, "Device cannot be null.");
    }

    @Override protected Void performRequest() throws Exception {
        if (!device.isRegisteredWithServer()) {
            return null;
        }

        getEndpoint().unregisterDevice(device.getRegistrationId()).execute();
        device.clear();
        return null;
    }
}
