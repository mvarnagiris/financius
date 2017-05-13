package com.code44.finance.backend.endpoints;

import com.code44.finance.backend.endpoints.body.AccountsBody;
import com.code44.finance.backend.entities.AccountEntity;
import com.code44.finance.backend.entities.ConfigEntity;
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
public class AccountsEndpoint extends BaseUserEntityEndpoint<AccountEntity> {
    private static final String PATH = "accounts";

    @ApiMethod(name = "listAccounts", httpMethod = "GET", path = PATH) public CollectionResponse<AccountEntity> list(@Named("timestamp") long timestamp, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException {
        return listEntities(timestamp, user, AccountEntity.class);
    }

    @ApiMethod(name = "updateAccounts", httpMethod = "POST", path = PATH) public UpdateData update(AccountsBody body, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException, IOException {
        final UpdateData updateData = new UpdateData();
        updateData.setUpdateTimestamp(updateEntities(body, user));
        return updateData;
    }

    @Override protected AccountEntity findById(String id) {
        return AccountEntity.find(id);
    }

    @Override protected void updateTimestamp(ConfigEntity configEntity, long timestamp) {
        configEntity.setAccountsUpdateTimestamp(timestamp);
    }
}
