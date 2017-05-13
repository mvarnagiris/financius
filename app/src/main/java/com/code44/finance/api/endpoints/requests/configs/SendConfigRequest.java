package com.code44.finance.api.endpoints.requests.configs;

import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.Device;
import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.EndpointRequest;
import com.code44.finance.backend.financius.model.ConfigBody;
import com.code44.finance.backend.financius.model.ConfigEntity;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.settings.security.Security;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.preferences.GeneralPrefs;

import static com.google.common.base.Preconditions.checkNotNull;

public class SendConfigRequest extends EndpointRequest<ConfigEntity> {
    private final Device device;
    private final CurrenciesManager currenciesManager;
    private final GeneralPrefs generalPrefs;
    private final Security security;

    public SendConfigRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Device device, @NonNull CurrenciesManager currenciesManager, @NonNull GeneralPrefs generalPrefs, @NonNull Security security) {
        super(eventBus, endpointFactory);
        this.device = checkNotNull(device, "Device cannot be null.");
        this.currenciesManager = checkNotNull(currenciesManager, "CurrenciesManager cannot be null.");
        this.generalPrefs = checkNotNull(generalPrefs, "GeneralPrefs cannot be null.");
        this.security = checkNotNull(security, "Security cannot be null.");
    }

    @Override protected ConfigEntity performRequest() throws Exception {
        final ConfigBody configBody = new ConfigBody();
        configBody.setDeviceRegistrationId(device.getRegistrationId());
        configBody.setCurrencyCode(currenciesManager.getMainCurrencyCode());
        configBody.setIntervalType(generalPrefs.getIntervalIntervalType().name());
        configBody.setIntervalLength(generalPrefs.getIntervalLength());
        configBody.setSecurityType(security.getSecurityType().name());
        configBody.setPassword(security.getPassword());

        return getEndpoint().setConfig(configBody).execute();
    }
}
