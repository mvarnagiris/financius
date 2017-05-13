package com.code44.finance.backend.endpoints;

import com.code44.finance.backend.endpoints.body.TagsBody;
import com.code44.finance.backend.entities.ConfigEntity;
import com.code44.finance.backend.entities.TagEntity;
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
public class TagsEndpoint extends BaseUserEntityEndpoint<TagEntity> {
    private static final String PATH = "tags";

    @ApiMethod(name = "listTags", httpMethod = "GET", path = PATH) public CollectionResponse<TagEntity> list(@Named("timestamp") long timestamp, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException {
        return listEntities(timestamp, user, TagEntity.class);
    }

    @ApiMethod(name = "updateTags", httpMethod = "POST", path = PATH) public UpdateData update(TagsBody body, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException, IOException {
        final UpdateData updateData = new UpdateData();
        updateData.setUpdateTimestamp(updateEntities(body, user));
        return updateData;
    }

    @Override protected TagEntity findById(String id) {
        return TagEntity.find(id);
    }

    @Override protected void updateTimestamp(ConfigEntity configEntity, long timestamp) {
        configEntity.setTagsUpdateTimestamp(timestamp);
    }
}
