package com.code44.finance.backend.endpoints;

import com.code44.finance.backend.endpoints.body.TransactionsBody;
import com.code44.finance.backend.entities.ConfigEntity;
import com.code44.finance.backend.entities.TransactionEntity;
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
public class TransactionsEndpoint extends BaseUserEntityEndpoint<TransactionEntity> {
    private static final String PATH = "transactions";

    @ApiMethod(name = "listTransactions", httpMethod = "GET", path = PATH) public CollectionResponse<TransactionEntity> list(@Named("timestamp") long timestamp, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException {
        return listEntities(timestamp, user, TransactionEntity.class);
    }

    @ApiMethod(name = "updateTransactions", httpMethod = "POST", path = PATH) public UpdateData update(TransactionsBody body, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException, IOException {
        final UpdateData updateData = new UpdateData();
        updateData.setUpdateTimestamp(updateEntities(body, user));
        return updateData;
    }

    @Override protected TransactionEntity findById(String id) {
        return TransactionEntity.find(id);
    }

    @Override protected void updateTimestamp(ConfigEntity configEntity, long timestamp) {
        configEntity.setTransactionsUpdateTimestamp(timestamp);
    }
}
