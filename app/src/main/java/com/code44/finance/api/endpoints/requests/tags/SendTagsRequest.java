package com.code44.finance.api.endpoints.requests.tags;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.Device;
import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.api.endpoints.requests.SendEntitiesRequest;
import com.code44.finance.backend.financius.model.TagsBody;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.utils.EventBus;

public class SendTagsRequest extends SendEntitiesRequest<Tag, TagsBody> {
    public SendTagsRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull User user, @NonNull DBHelper dbHelper, @NonNull Device device) {
        super(eventBus, endpointFactory, context, user, dbHelper, device);
    }

    @Override protected Query getQuery() {
        return Tables.Tags.getQuery();
    }

    @Override protected Column getSyncStateColumn() {
        return Tables.Tags.SYNC_STATE;
    }

    @Override protected Uri getUri() {
        return TagsProvider.uriTags();
    }

    @Override protected Tag getModel(Cursor cursor) {
        return Tag.from(cursor);
    }

    @Override protected TagsBody createBody() {
        return new TagsBody();
    }

    @Override protected long performRequest(TagsBody body) throws Exception {
        return getEndpoint().updateTags(body).execute().getUpdateTimestamp();
    }

    @Override protected void saveLastUpdateTimestamp(User user, long lastUpdateTimestamp) {
        user.setLastTagsUpdateTimestamp(lastUpdateTimestamp);
    }
}
