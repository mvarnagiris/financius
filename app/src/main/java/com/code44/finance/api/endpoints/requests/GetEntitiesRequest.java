package com.code44.finance.api.endpoints.requests;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.EndpointRequest;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.model.BaseModel;
import com.code44.finance.utils.EventBus;
import com.google.api.client.json.GenericJson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class GetEntitiesRequest<M extends BaseModel, E extends GenericJson> extends EndpointRequest<List<M>> {
    private final Context context;
    private final User user;

    public GetEntitiesRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull User user) {
        super(checkNotNull(eventBus, "EventBus cannot be null."), endpointFactory);
        this.context = checkNotNull(context, "Context cannot be null.").getApplicationContext();
        this.user = checkNotNull(user, "User cannot be null.");
    }

    @Override protected List<M> performRequest() throws Exception {
        long timestamp = getLastUpdateTimestamp(user);
        final List<E> entities = performRequest(timestamp);
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        final List<M> models = new ArrayList<>();
        for (E entity : entities) {
            final M model = getModelFrom(entity);
            models.add(model);

            final long entityEditTimestamp = (Long) entity.get("edit_ts");
            if (timestamp < entityEditTimestamp) {
                timestamp = entityEditTimestamp;
            }
        }

        DataStore.bulkInsert().models(models).into(context, getSaveUri());
        saveLastUpdateTimestamp(user, timestamp);
        return models;
    }

    protected abstract long getLastUpdateTimestamp(User user);

    protected abstract List<E> performRequest(long timestamp) throws Exception;

    protected abstract M getModelFrom(E entity);

    protected abstract Uri getSaveUri();

    protected abstract void saveLastUpdateTimestamp(User user, long lastUpdateTimestamp);
}
