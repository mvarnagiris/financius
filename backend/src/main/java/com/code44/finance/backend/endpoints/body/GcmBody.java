package com.code44.finance.backend.endpoints.body;

import com.google.gson.annotations.SerializedName;

public abstract class GcmBody implements Body {
    @SerializedName(value = "device_registration_id") private String deviceRegistrationId;

    public String getDeviceRegistrationId() {
        return deviceRegistrationId;
    }
}
