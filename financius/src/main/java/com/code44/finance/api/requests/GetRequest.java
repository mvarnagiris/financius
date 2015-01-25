package com.code44.finance.api.requests;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.code44.finance.api.Request;
import com.code44.finance.api.User;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.model.Model;
import com.code44.finance.utils.EventBus;
import com.google.api.client.json.GenericJson;

import java.util.ArrayList;
import java.util.List;

public abstract class GetRequest<T extends GenericJson> extends Request {
    private final Context context;
    private final User user;

    public GetRequest(EventBus eventBus, Context context, User user) {
        super(eventBus);
        Preconditions.notNull(context, "Context cannot be null.");
        Preconditions.notNull(user, "User cannot be null.");

        this.context = context;
        this.user = user;
    }

    @Override protected Object performRequest() throws Exception {
        long timestamp = getLastTimestamp(user);
        final List<T> entities = performRequest(timestamp);
        final List<ContentValues> valuesList = new ArrayList<>();
        for (T entity : entities) {
            final Model model = getModelFrom(entity);
            final ContentValues values = model.asContentValues();
            onValuesCreated(values);
            valuesList.add(values);
            final long entityEditTimestamp = (Long) entity.get("edit_ts");
            if (timestamp < entityEditTimestamp) {
                timestamp = entityEditTimestamp;
            }
        }

        DataStore.bulkInsert().values(valuesList).into(context, getSaveUri());
        saveNewTimestamp(user, timestamp);
        return null;
    }

    protected abstract long getLastTimestamp(User user);

    protected abstract List<T> performRequest(long timestamp) throws Exception;

    protected abstract Model getModelFrom(T entity);

    protected abstract void saveNewTimestamp(User user, long newTimestamp);

    protected abstract Uri getSaveUri();

    protected void onValuesCreated(ContentValues values) {
    }

    protected Context getContext() {
        return context;
    }

    protected User getUser() {
        return user;
    }
}
