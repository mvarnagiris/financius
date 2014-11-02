package com.code44.finance.ui.categories;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Pair;
import android.view.Menu;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.ui.common.ModelActivity;

public class CategoryActivity extends ModelActivity<Category> {
    private TextView titleTextView;

    public static void start(Context context, String categoryId) {
        final Intent intent = makeIntent(context, CategoryActivity.class, categoryId);
        startActivity(context, intent);
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_category;
    }

    @Override protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        // Get views
        titleTextView = (TextView) findViewById(R.id.titleTextView);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override protected CursorLoader getModelCursorLoader(String modelId) {
        return Tables.Categories.getQuery(null).asCursorLoader(this, CategoriesProvider.uriCategory(modelId));
    }

    @Override protected Category getModelFrom(Cursor cursor) {
        return Category.from(cursor);
    }

    @Override protected void onModelLoaded(Category model) {
        titleTextView.setText(model.getTitle());
        titleTextView.setBackgroundColor(model.getColor());
        getToolbar().setBackgroundColor(model.getColor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(model.getColor());
            getWindow().setNavigationBarColor(model.getColor());
        }
    }

    @Override protected Uri getDeleteUri() {
        return CategoriesProvider.uriCategories();
    }

    @Override protected Pair<String, String[]> getDeleteSelection() {
        return Pair.create(Tables.Categories.ID + "=?", new String[]{String.valueOf(modelId)});
    }

    @Override protected void startModelEdit(String modelId) {
        CategoryEditActivity.start(this, modelId);
    }
}
