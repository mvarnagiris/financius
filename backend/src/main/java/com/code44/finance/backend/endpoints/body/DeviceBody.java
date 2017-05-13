package com.code44.finance.backend.endpoints.body;

import com.google.api.server.spi.response.BadRequestException;
import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

public class DeviceBody implements Body {
    @SerializedName(value = "registration_id") private String registrationId;

    @SerializedName(value = "device_name") private String deviceName;

    @Override public void verifyRequiredFields() throws BadRequestException {
        if (Strings.isNullOrEmpty(registrationId)) {
            throw new BadRequestException("registration_id cannot be empty.");
        }
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
