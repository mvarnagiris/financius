package com.code44.finance.backend.endpoints.body;

import com.code44.finance.common.interval.IntervalType;
import com.code44.finance.common.security.SecurityType;
import com.google.api.server.spi.response.BadRequestException;
import com.google.gson.annotations.SerializedName;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ConfigBody extends GcmBody {
    @SerializedName(value = "currency_code") private String currencyCode;

    @SerializedName(value = "interval_type") private IntervalType intervalType;

    @SerializedName(value = "interval_length") private int intervalLength;

    @SerializedName(value = "security_type") private SecurityType securityType;

    @SerializedName(value = "password") private String password;

    @Override public void verifyRequiredFields() throws BadRequestException {
        checkNotNull(currencyCode, "Currency code cannot be null.");
        checkArgument(currencyCode.length() == 3, "Currency code length must be 3.");
        checkArgument(intervalLength > 0, "Interval length must be > 0.");
        checkNotNull(intervalType, "Interval type cannot be null.");
        checkNotNull(securityType, "Security type cannot be null.");
        if (securityType != SecurityType.None) {
            checkNotNull(password, "Password cannot be null.");
            checkArgument(password.length() > 0, "Password length must be > 0.");
        }
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public IntervalType getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(IntervalType intervalType) {
        this.intervalType = intervalType;
    }

    public int getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(int intervalLength) {
        this.intervalLength = intervalLength;
    }

    public SecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(SecurityType securityType) {
        this.securityType = securityType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
