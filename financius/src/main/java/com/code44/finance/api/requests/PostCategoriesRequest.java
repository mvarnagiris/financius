package com.code44.finance.api.requests;

import com.code44.finance.backend.endpoint.categories.Categories;
import com.code44.finance.backend.endpoint.categories.model.CategoriesBody;
import com.code44.finance.backend.endpoint.categories.model.CategoryEntity;
import com.code44.finance.data.db.model.Category;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PostCategoriesRequest extends PostRequest<CategoriesBody> {
    private final List<Category> categories;
    @Inject Categories categoriesService;

    public PostCategoriesRequest(List<Category> categories) {
        this.categories = categories;
    }

    @Override protected CategoriesBody createBody() {
        return new CategoriesBody();
    }

    @Override protected void onAddPostData(CategoriesBody body) {
        final List<CategoryEntity> categoryEntities = new ArrayList<>();
        for (Category category : categories) {
            categoryEntities.add(category.toEntity());
        }
        body.setCategories(categoryEntities);
    }

    @Override protected void performRequest(CategoriesBody body) throws Exception {
        categoriesService.save(body);
    }
}
