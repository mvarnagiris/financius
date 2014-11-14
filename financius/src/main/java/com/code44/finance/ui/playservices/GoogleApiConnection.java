package com.code44.finance.ui.playservices;

import com.code44.finance.utils.EventBus;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.otto.Produce;

import java.util.HashMap;
import java.util.Map;

public class GoogleApiConnection {
    private final EventBus eventBus;
    private final Map<String, GoogleApiClient> googleApiClients = new HashMap<>();

    public GoogleApiConnection(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Produce public GoogleApiConnection produceGoogleApiConnection() {
        return googleApiClients.size() > 0 ? this : null;
    }

    public void put(String clientUniqueId, GoogleApiClient client) {
        if (client != null && (client.isConnected() || client.isConnecting())) {
            googleApiClients.put(clientUniqueId, client);
            eventBus.post(this);
        }
    }

    public void remove(String clientUniqueId) {
        googleApiClients.remove(clientUniqueId);
    }

    public GoogleApiClient get(String clientUniqueId) {
        return googleApiClients.get(clientUniqueId);
    }

    public boolean contains(String clientUniqueId) {
        return googleApiClients.containsKey(clientUniqueId);
    }
}
