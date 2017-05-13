package com.code44.finance.backend.endpoints;

import com.code44.finance.backend.endpoints.body.CategoriesBody;
import com.code44.finance.backend.entities.CategoryEntity;
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
public class CategoriesEndpoint extends BaseUserEntityEndpoint<CategoryEntity> {
    private static final String PATH = "categories";

    @ApiMethod(name = "listCategories", httpMethod = "GET", path = PATH) public CollectionResponse<CategoryEntity> list(@Named("timestamp") long timestamp, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException {
        return listEntities(timestamp, user, CategoryEntity.class);
    }

    @ApiMethod(name = "updateCategories", httpMethod = "POST", path = PATH) public UpdateData update(CategoriesBody body, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException, IOException {
        final UpdateData updateData = new UpdateData();
        updateData.setUpdateTimestamp(updateEntities(body, user));
        return updateData;
    }

    @Override protected CategoryEntity findById(String id) {
        return CategoryEntity.find(id);
    }

    @Override protected void updateTimestamp(ConfigEntity configEntity, long timestamp) {
        configEntity.setCategoriesUpdateTimestamp(timestamp);
    }
}
