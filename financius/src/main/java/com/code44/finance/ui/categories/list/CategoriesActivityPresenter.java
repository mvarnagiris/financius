package com.code44.finance.ui.categories.list;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.ui.categories.CategoryActivity;
import com.code44.finance.ui.categories.CategoryEditActivity;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;

class CategoriesActivityPresenter extends ModelsActivityPresenter<Category> {
    @Override protected ModelsAdapter<Category> createAdapter(ModelsAdapter.OnModelClickListener<Category> defaultOnModelClickListener) {
        return new CategoriesAdapter(defaultOnModelClickListener);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Categories
                .getQuery(null)
                .sortOrder(Tables.Categories.TRANSACTION_TYPE.getName())
                .sortOrder(Tables.Categories.SORT_ORDER.getName())
                .asCursorLoader(context, CategoriesProvider.uriCategories());
    }

    @Override protected void onModelClick(Context context, View view, Category model, Cursor cursor, int position) {
        CategoryActivity.start(context, model.getId());
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        CategoryEditActivity.start(context, modelId);
    }

    @Override protected RecyclerView.ItemDecoration[] getItemDecorations() {
        final Context context = getActivity();
        return new RecyclerView.ItemDecoration[]{new DividerDecoration(context).setPaddingLeft(context.getResources().getDimensionPixelSize(R.dimen.keyline_content))};
    }
}
