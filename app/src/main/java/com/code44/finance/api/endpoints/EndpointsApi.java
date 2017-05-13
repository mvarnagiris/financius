package com.code44.finance.api.endpoints;

import android.content.Context;

import com.code44.finance.ApplicationContext;
import com.code44.finance.api.Request;
import com.code44.finance.api.endpoints.requests.SyncRequest;
import com.code44.finance.api.endpoints.requests.configs.SendConfigRequest;
import com.code44.finance.api.endpoints.requests.devices.RegisterDeviceRequest;
import com.code44.finance.api.endpoints.requests.devices.UnregisterDeviceRequest;
import com.code44.finance.api.endpoints.requests.users.RegisterRequest;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.settings.security.Security;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.executors.Network;
import com.code44.finance.utils.interval.ActiveInterval;
import com.code44.finance.utils.interval.CurrentInterval;
import com.code44.finance.utils.preferences.GeneralPrefs;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class EndpointsApi {
    private final Context context;
    private final ExecutorService executor;
    private final User user;
    private final Device device;
    private final DBHelper dbHelper;
    private final EventBus eventBus;
    private final GeneralPrefs generalPrefs;
    private final Security security;
    private final CurrenciesManager currenciesManager;
    private final EndpointFactory endpointFactory;
    private final CurrentInterval currentInterval;
    private final ActiveInterval activeInterval;

    @Inject public EndpointsApi(@ApplicationContext Context context, @Network ExecutorService executor, User user, Device device, DBHelper dbHelper, EventBus eventBus, EndpointFactory endpointFactory, GeneralPrefs generalPrefs, Security security, CurrenciesManager currenciesManager, CurrentInterval currentInterval, ActiveInterval activeInterval) {
        this.context = checkNotNull(context, "Context cannot be null.");
        this.executor = checkNotNull(executor, "Executor cannot be null.");
        this.user = checkNotNull(user, "User cannot be null.");
        this.device = checkNotNull(device, "Device cannot be null.");
        this.dbHelper = checkNotNull(dbHelper, "DBHelper cannot be null.");
        this.eventBus = checkNotNull(eventBus, "EventBus cannot be null.");
        this.endpointFactory = checkNotNull(endpointFactory, "EndpointFactory cannot be null.");
        this.generalPrefs = checkNotNull(generalPrefs, "GeneralPrefs cannot be null.");
        this.security = checkNotNull(security, "Security cannot be null.");
        this.currenciesManager = checkNotNull(currenciesManager, "CurrenciesManager cannot be null.");
        this.currentInterval = checkNotNull(currentInterval, "CurrentInterval cannot be null.");
        this.activeInterval = checkNotNull(activeInterval, "ActiveInterval cannot be null.");
    }

    public void syncModels() {
        final SyncRequest request = new SyncRequest(eventBus, endpointFactory, context, user, dbHelper, device, currenciesManager, generalPrefs, security, currentInterval, activeInterval);
        execute(request);
    }

    public void syncConfig() {
        final SendConfigRequest request = new SendConfigRequest(eventBus, endpointFactory, device, currenciesManager, generalPrefs, security);
        execute(request);
    }

    public void register(Context context, String email, String firstName, String lastName, String photoUrl, String coverUrl) {
        final RegisterRequest request = new RegisterRequest(eventBus, endpointFactory, context, this, user, dbHelper, email, firstName, lastName, photoUrl, coverUrl);
        execute(request);
    }

    public void registerDevice(Context context) {
        final RegisterDeviceRequest request = new RegisterDeviceRequest(eventBus, endpointFactory, context, device);
        execute(request);
    }

    public void unregisterDevice() {
        final UnregisterDeviceRequest request = new UnregisterDeviceRequest(eventBus, endpointFactory, device);
        execute(request);
    }

    private void execute(Request request) {
        executor.submit(request);
    }
}
