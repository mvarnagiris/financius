package com.code44.finance.backend.endpoints.body;

import com.code44.finance.backend.entities.BaseEntity;
import com.google.api.server.spi.response.BadRequestException;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public abstract class EntitiesBody<E extends BaseEntity> extends GcmBody {
    @SerializedName(value = "entities") private List<E> entities;

    @Override public void verifyRequiredFields() throws BadRequestException {
        if (entities == null || entities.isEmpty()) {
            throw new BadRequestException("entities cannot be empty.");
        }
    }

    public List<E> getEntities() {
        return entities;
    }
}
