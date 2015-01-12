package com.code44.finance.ui.categories.list;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.ui.categories.detail.CategoryActivity;
import com.code44.finance.ui.categories.edit.CategoryEditActivity;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.ui.common.recycler.DividerDecoration;
import com.code44.finance.ui.common.recycler.SectionsDecoration;

class CategoriesActivityPresenter extends ModelsActivityPresenter<Category> {
    private static final String EXTRA_TRANSACTION_TYPE = CategoriesActivityPresenter.class.getName() + ".EXTRA_TRANSACTION_TYPE";

    private TransactionType transactionType;

    public static void addExtras(Intent intent, TransactionType transactionType) {
        intent.putExtra(EXTRA_TRANSACTION_TYPE, transactionType);
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        transactionType = (TransactionType) activity.getIntent().getSerializableExtra(EXTRA_TRANSACTION_TYPE);
        super.onCreate(activity, savedInstanceState);
    }

    @Override protected ModelsAdapter<Category> createAdapter(ModelsAdapter.OnModelClickListener<Category> defaultOnModelClickListener) {
        return new CategoriesAdapter(defaultOnModelClickListener);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Categories
                .getQuery(transactionType)
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
        return new RecyclerView.ItemDecoration[]{new DividerDecoration(context).setPaddingLeft(context.getResources().getDimensionPixelSize(R.dimen.keyline_content)), new SectionsDecoration()};
    }
}