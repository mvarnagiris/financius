package com.code44.finance.ui.categories.list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.ui.categories.detail.CategoryActivity;
import com.code44.finance.ui.categories.edit.CategoryEditActivity;
import com.code44.finance.ui.common.activities.ActivityStarter;
import com.code44.finance.ui.common.activities.ModelsActivity;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.recycler.DividerDecoration;
import com.code44.finance.ui.common.recycler.SectionsDecoration;
import com.code44.finance.utils.analytics.Screens;

public class CategoriesActivity extends ModelsActivity<Category, CategoriesAdapter> {
    private static final String EXTRA_TRANSACTION_TYPE = CategoriesActivity.class.getName() + ".EXTRA_TRANSACTION_TYPE";

    private TransactionType transactionType;

    public static void startView(Context context) {
        ActivityStarter.begin(context, CategoriesActivity.class).modelsView().start();
    }

    public static void startSelect(Context context, int requestCode, TransactionType transactionType) {
        ActivityStarter.begin(context, CategoriesActivity.class)
                .modelsSelect()
                .extra(EXTRA_TRANSACTION_TYPE, transactionType)
                .startForResult(requestCode);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get extras
        transactionType = (TransactionType) getIntent().getSerializableExtra(EXTRA_TRANSACTION_TYPE);
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_categories;
    }

    @Override protected CategoriesAdapter createAdapter(ModelsAdapter.OnModelClickListener<Category> defaultOnModelClickListener, Mode mode) {
        return new CategoriesAdapter(defaultOnModelClickListener, mode);
    }

    @Override protected CursorLoader getModelsCursorLoader() {
        return Tables.Categories.getQuery(transactionType)
                .sortOrder(Tables.Categories.TRANSACTION_TYPE.getName())
                .sortOrder(Tables.Categories.SORT_ORDER.getName())
                .asCursorLoader(this, CategoriesProvider.uriCategories());
    }

    @Override protected void onModelClick(Category model) {
        CategoryActivity.start(this, model.getId());
    }

    @Override protected void startModelEdit(String modelId) {
        CategoryEditActivity.start(this, modelId);
    }

    @Override protected void setupRecyclerViewDecorations(RecyclerView recyclerView) {
        final int keylineContent = getResources().getDimensionPixelSize(R.dimen.keyline_content);
        final DividerDecoration dividerDecoration = new DividerDecoration(this, keylineContent, 0, 0, 0, DividerDecoration.DRAW_DIVIDER_MIDDLE | DividerDecoration.DRAW_DIVIDER_END);
        final SectionsDecoration sectionsDecoration = new SectionsDecoration(true);

        recyclerView.addItemDecoration(dividerDecoration);
        recyclerView.addItemDecoration(sectionsDecoration);
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.CategoryList;
    }
}
