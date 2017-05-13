package com.code44.finance.backend.endpoints;

import com.code44.finance.backend.endpoints.body.CurrenciesBody;
import com.code44.finance.backend.entities.ConfigEntity;
import com.code44.finance.backend.entities.CurrencyEntity;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

import java.io.IOException;

import javax.inject.Named;

@ApiReference(Endpoint.class)
public class CurrenciesEndpoint extends BaseUserEntityEndpoint<CurrencyEntity> {
    private static final String PATH = "currencies";

    @ApiMethod(name = "listCurrencies", httpMethod = "GET", path = PATH) public CollectionResponse<CurrencyEntity> list(@Named("timestamp") long timestamp, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException {
        return listEntities(timestamp, user, CurrencyEntity.class);
    }

    @ApiMethod(name = "updateCurrencies", httpMethod = "POST", path = PATH) public UpdateData update(CurrenciesBody body, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException, IOException {
        final UpdateData updateData = new UpdateData();
        updateData.setUpdateTimestamp(updateEntities(body, user));
        return updateData;
    }

    @Override protected CurrencyEntity findById(String id) {
        return CurrencyEntity.find(id);
    }

    @Override protected void updateTimestamp(ConfigEntity configEntity, long timestamp) {
        configEntity.setCurrenciesUpdateTimestamp(timestamp);
    }
}
