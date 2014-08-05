package com.code44.finance.backend.endpoint.body;

import java.util.List;

public class EntitiesBody<T> {
    private final List<T> entities;
    private final String deviceRegId;

    public EntitiesBody(List<T> entities, String deviceRegId) {
        this.entities = entities;
        this.deviceRegId = deviceRegId;
    }

    public List<T> getEntities() {
        return entities;
    }

    public String getDeviceRegId() {
        return deviceRegId;
    }
}
