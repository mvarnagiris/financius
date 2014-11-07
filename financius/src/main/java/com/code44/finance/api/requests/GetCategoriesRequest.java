package com.code44.finance.api.requests;

import android.content.Context;
import android.net.Uri;

import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.categories.Categories;
import com.code44.finance.backend.endpoint.categories.model.CategoryEntity;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Model;
import com.code44.finance.data.providers.CategoriesProvider;

import java.util.List;

public class GetCategoriesRequest extends GetRequest<CategoryEntity> {
    private final Categories categoriesService;

    public GetCategoriesRequest(Context context, User user, Categories categoriesService) {
        super(null, context, user);
        Preconditions.notNull(categoriesService, "Categories cannot be null.");

        this.categoriesService = categoriesService;
    }

    @Override protected long getLastTimestamp(User user) {
        return user.getCategoriesTimestamp();
    }

    @Override protected List<CategoryEntity> performRequest(long timestamp) throws Exception {
        return categoriesService.list(timestamp).execute().getItems();
    }

    @Override protected Model getModelFrom(CategoryEntity entity) {
        return Category.from(entity);
    }

    @Override protected void saveNewTimestamp(User user, long newTimestamp) {
        user.setCategoriesTimestamp(newTimestamp);
    }

    @Override protected Uri getSaveUri() {
        return CategoriesProvider.uriCategories();
    }
}
