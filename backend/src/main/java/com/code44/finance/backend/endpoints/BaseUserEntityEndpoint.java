package com.code44.finance.backend.endpoints;

import com.code44.finance.backend.endpoints.body.EntitiesBody;
import com.code44.finance.backend.entities.BaseUserEntity;
import com.code44.finance.backend.entities.ConfigEntity;
import com.code44.finance.backend.entities.UserEntity;
import com.code44.finance.backend.utils.EndpointUtils;
import com.code44.finance.common.gcm.CollapseKey;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.code44.finance.backend.OfyService.ofy;

public abstract class BaseUserEntityEndpoint<E extends BaseUserEntity> {
    private static final Logger log = Logger.getLogger(BaseUserEntityEndpoint.class.getName());

    protected CollectionResponse<E> listEntities(long timestamp, User user, Class<E> entityClass) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException {
        log.entering(getClass().getSimpleName(), "listEntities", "timestamp=" + timestamp);
        final UserEntity userEntity = EndpointUtils.getUserEntityAndVerifyPermissions(user);

        final List<E> entities = ofy().load()
                .type(entityClass)
                .filter("userEntity", Key.create(UserEntity.class, userEntity.getId()))
                .filter("editTimestamp >", timestamp)
                .list();

        final CollectionResponse<E> response = CollectionResponse.<E>builder().setItems(entities).build();
        log.exiting(getClass().getSimpleName(), "listEntities", "size=" + response.getItems().size());
        return response;
    }

    protected long updateEntities(EntitiesBody<E> body, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException, IOException {
        // Verify parameters
        final UserEntity userEntity = EndpointUtils.getUserEntityAndVerifyPermissions(user);
        EndpointUtils.verifyBodyNotNull(body);
        body.verifyRequiredFields();

        final Key<UserEntity> userEntityKey = Key.create(UserEntity.class, userEntity.getId());
        //noinspection unchecked
        final List<E> entities = body.getEntities();
        final Objectify ofy = ofy();
        long lastUpdateTimestamp = 0;
        for (E entity : entities) {
            final E storedEntity = findById(entity.getId());
            if (storedEntity == null) {
                entity.onCreate();
            } else {
                entity.setCreateTimestamp(storedEntity.getCreateTimestamp());
                entity.onUpdate();
            }
            entity.setUserEntity(userEntityKey);
            if (entity.getEditTimestamp() > lastUpdateTimestamp) {
                lastUpdateTimestamp = entity.getEditTimestamp();
            }
        }
        ofy.save().entities(entities).now();

        // Update timestamp
        final ConfigEntity configEntity = ConfigEntity.find(userEntity);
        if (configEntity != null) {
            updateTimestamp(configEntity, lastUpdateTimestamp);
            ofy().save().entity(configEntity).now();
        }

        EndpointUtils.notifyDataChanged(userEntity, body.getDeviceRegistrationId(), CollapseKey.DataChanged);
        return lastUpdateTimestamp;
    }

    protected abstract E findById(String id);

    protected abstract void updateTimestamp(ConfigEntity configEntity, long timestamp);

    static {
        log.setLevel(Level.ALL);
    }
}
