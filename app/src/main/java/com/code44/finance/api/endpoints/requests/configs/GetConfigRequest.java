package com.code44.finance.api.endpoints.requests.configs;

import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.EndpointRequest;
import com.code44.finance.backend.financius.model.ConfigEntity;
import com.code44.finance.common.interval.IntervalType;
import com.code44.finance.common.security.SecurityType;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.settings.security.Security;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.interval.ActiveInterval;
import com.code44.finance.utils.interval.CurrentInterval;
import com.code44.finance.utils.preferences.GeneralPrefs;

import static com.google.common.base.Preconditions.checkNotNull;

public class GetConfigRequest extends EndpointRequest<ConfigEntity> {
    private final CurrenciesManager currenciesManager;
    private final GeneralPrefs generalPrefs;
    private final Security security;
    private final CurrentInterval currentInterval;
    private final ActiveInterval activeInterval;

    public GetConfigRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull CurrenciesManager currenciesManager, @NonNull GeneralPrefs generalPrefs, @NonNull Security security, @NonNull CurrentInterval currentInterval, @NonNull ActiveInterval activeInterval) {
        super(eventBus, endpointFactory);
        this.currenciesManager = checkNotNull(currenciesManager, "CurrenciesManager cannot be null.");
        this.generalPrefs = checkNotNull(generalPrefs, "GeneralPrefs cannot be null.");
        this.security = checkNotNull(security, "Security cannot be null.");
        this.currentInterval = checkNotNull(currentInterval, "CurrentInterval cannot be null.");
        this.activeInterval = checkNotNull(activeInterval, "ActiveInterval cannot be null.");
    }

    @Override protected ConfigEntity performRequest() throws Exception {
        final ConfigEntity configEntity = getEndpoint().getConfig().execute();

        final String currencyCode = configEntity.getCurrencyCode();
        final IntervalType intervalType = IntervalType.valueOf(configEntity.getIntervalType());
        final int intervalLength = configEntity.getIntervalLength();
        final SecurityType securityType = SecurityType.valueOf(configEntity.getSecurityType());
        final String password = configEntity.getPassword();

        currenciesManager.setMainCurrencyCode(currencyCode);
        generalPrefs.setIntervalTypeAndLength(intervalType, intervalLength);
        generalPrefs.notifyChanged();
        currentInterval.setTypeAndLength(intervalType, intervalLength);
        activeInterval.setTypeAndLength(intervalType, intervalLength);
        generalPrefs.notifyChanged();
        security.setTypeWithoutHashing(securityType, password);
        security.notifyChanged();

        return configEntity;
    }
}
