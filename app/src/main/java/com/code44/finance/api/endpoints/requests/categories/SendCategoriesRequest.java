package com.code44.finance.api.endpoints.requests.categories;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.Device;
import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.api.endpoints.requests.SendEntitiesRequest;
import com.code44.finance.backend.financius.model.CategoriesBody;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.utils.EventBus;

public class SendCategoriesRequest extends SendEntitiesRequest<Category, CategoriesBody> {
    public SendCategoriesRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull User user, @NonNull DBHelper dbHelper, @NonNull Device device) {
        super(eventBus, endpointFactory, context, user, dbHelper, device);
    }

    @Override protected Query getQuery() {
        return Tables.Categories.getQuery(null);
    }

    @Override protected Column getSyncStateColumn() {
        return Tables.Categories.SYNC_STATE;
    }

    @Override protected Uri getUri() {
        return CategoriesProvider.uriCategories();
    }

    @Override protected Category getModel(Cursor cursor) {
        return Category.from(cursor);
    }

    @Override protected CategoriesBody createBody() {
        return new CategoriesBody();
    }

    @Override protected long performRequest(CategoriesBody body) throws Exception {
        return getEndpoint().updateCategories(body).execute().getUpdateTimestamp();
    }

    @Override protected void saveLastUpdateTimestamp(User user, long lastUpdateTimestamp) {
        user.setLastCategoriesUpdateTimestamp(lastUpdateTimestamp);
    }
}
