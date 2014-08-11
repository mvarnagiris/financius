package com.code44.finance.api.requests;

import android.net.Uri;

import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.categories.Categories;
import com.code44.finance.backend.endpoint.categories.model.CategoryEntity;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;

import java.util.List;

import javax.inject.Inject;

public class GetCategoriesRequest extends GetRequest<CategoryEntity> {
    @Inject Categories categoriesService;

    @Override protected long getLastTimestamp(User user) {
        return user.getCategoriesTimestamp();
    }

    @Override protected List<CategoryEntity> performRequest(long timestamp) throws Exception {
        return categoriesService.list(timestamp).execute().getItems();
    }

    @Override protected BaseModel getModelFrom(CategoryEntity entity) {
        return Category.from(entity);
    }

    @Override protected void saveNewTimestamp(User user, long newTimestamp) {
        user.setCategoriesTimestamp(newTimestamp);
    }

    @Override protected Uri getSaveUri() {
        return CategoriesProvider.uriCategories();
    }
}
