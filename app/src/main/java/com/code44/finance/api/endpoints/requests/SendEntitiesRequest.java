package com.code44.finance.api.endpoints.requests;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.Device;
import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.EndpointRequest;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.model.Model;
import com.code44.finance.data.model.SyncState;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.IOUtils;
import com.google.api.client.json.GenericJson;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class SendEntitiesRequest<M extends Model, B extends GenericJson> extends EndpointRequest<Void> {
    private final Context context;
    private final User user;
    private final DBHelper dbHelper;
    private final Device device;

    protected SendEntitiesRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull User user, @NonNull DBHelper dbHelper, @NonNull Device device) {
        super(checkNotNull(eventBus, "EventBus cannot be null."), endpointFactory);
        this.context = checkNotNull(context, "Context cannot be null.").getApplicationContext();
        this.user = checkNotNull(user, "User cannot be null.");
        this.dbHelper = checkNotNull(dbHelper, "DBHelper cannot be null.");
        this.device = checkNotNull(device, "Device cannot be null.");
    }

    @Override protected Void performRequest() throws Exception {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        markInProgress(database, getSyncStateColumn());

        final Cursor cursor = getQuery().clearSort()
                .clearSelection()
                .clearArgs()
                .selection(getSyncStateColumn() + "=?", SyncState.InProgress.asString())
                .from(context, getUri())
                .execute();
        if (cursor == null || !cursor.moveToFirst()) {
            IOUtils.closeQuietly(cursor);
            return null;
        }

        final List<Object> entities = new ArrayList<>();
        do {
            final M model = getModel(cursor);
            entities.add(model.asEntity());
        } while (cursor.moveToNext());

        final B body = createBody();
        body.set("device_registration_id", device.getRegistrationId());
        body.set("entities", entities);

        final long lastUpdateTimestamp = performRequest(body);
        markSynced(database, getSyncStateColumn());
        saveLastUpdateTimestamp(user, lastUpdateTimestamp);
        return null;
    }

    protected abstract Query getQuery();

    protected abstract Column getSyncStateColumn();

    protected abstract Uri getUri();

    protected abstract M getModel(Cursor cursor);

    protected abstract B createBody();

    protected abstract long performRequest(B body) throws Exception;

    protected abstract void saveLastUpdateTimestamp(User user, long lastUpdateTimestamp);

    private void markInProgress(SQLiteDatabase database, Column syncStateColumn) {
        final ContentValues values = new ContentValues();
        values.put(syncStateColumn.getName(), SyncState.InProgress.asInt());
        DataStore.update()
                .values(values)
                .withSelection(syncStateColumn.getName() + "<>?", SyncState.Synced.asString())
                .into(database, syncStateColumn.getTableName());
    }

    private void markSynced(SQLiteDatabase database, Column syncStateColumn) {
        final ContentValues values = new ContentValues();
        values.put(syncStateColumn.getName(), SyncState.Synced.asInt());
        DataStore.update()
                .values(values)
                .withSelection(syncStateColumn.getName() + "==?", SyncState.InProgress.asString())
                .into(database, syncStateColumn.getTableName());
    }
}
