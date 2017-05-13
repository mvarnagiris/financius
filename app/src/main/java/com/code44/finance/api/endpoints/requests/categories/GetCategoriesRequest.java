package com.code44.finance.api.endpoints.requests.categories;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.api.endpoints.requests.GetEntitiesRequest;
import com.code44.finance.backend.financius.model.CategoryEntity;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.utils.EventBus;

import java.util.List;

public class GetCategoriesRequest extends GetEntitiesRequest<Category, CategoryEntity> {
    public GetCategoriesRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull User user) {
        super(eventBus, endpointFactory, context, user);
    }

    @Override protected long getLastUpdateTimestamp(User user) {
        return user.getLastCategoriesUpdateTimestamp();
    }

    @Override protected List<CategoryEntity> performRequest(long timestamp) throws Exception {
        return getEndpoint().listCategories(timestamp).execute().getItems();
    }

    @Override protected Category getModelFrom(CategoryEntity entity) {
        return Category.from(entity);
    }

    @Override protected Uri getSaveUri() {
        return CategoriesProvider.uriCategories();
    }

    @Override protected void saveLastUpdateTimestamp(User user, long lastUpdateTimestamp) {
        user.setLastCategoriesUpdateTimestamp(lastUpdateTimestamp);
    }
}