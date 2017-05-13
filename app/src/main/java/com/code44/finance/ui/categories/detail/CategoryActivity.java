package com.code44.finance.ui.categories.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.categories.edit.CategoryEditActivity;
import com.code44.finance.ui.common.activities.ModelActivity;
import com.code44.finance.ui.reports.trends.TrendsChartView;
import com.code44.finance.utils.analytics.Screens;
import com.code44.finance.utils.interval.CurrentInterval;

import javax.inject.Inject;

public class CategoryActivity extends ModelActivity<Category> {
    @Inject CurrentInterval currentInterval;
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    private CategoryTrendsChartPresenter categoryTrendsChartPresenter;
    private TextView titleTextView;
    private ImageView colorImageView;

    public static void start(Context context, String categoryId) {
        makeActivityStarter(context, CategoryActivity.class, categoryId).start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Get views
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        colorImageView = (ImageView) findViewById(R.id.colorImageView);
        final TrendsChartView trendsChartView = (TrendsChartView) findViewById(R.id.trendsChartView);

        // Setup
        categoryTrendsChartPresenter = new CategoryTrendsChartPresenter(trendsChartView, currenciesManager, amountFormatter, getSupportLoaderManager(), currentInterval);
    }

    @NonNull @Override protected CursorLoader getModelCursorLoader(@NonNull String modelId) {
        return Tables.Categories.getQuery(null).asCursorLoader(this, CategoriesProvider.uriCategory(modelId));
    }

    @NonNull @Override protected Category getModelFrom(@NonNull Cursor cursor) {
        return Category.from(cursor);
    }

    @Override protected void onModelLoaded(@NonNull Category model) {
        titleTextView.setText(model.getTitle());
        colorImageView.setColorFilter(model.getColor());
        categoryTrendsChartPresenter.setCategoryAndInterval(model, currentInterval);
    }

    @Override protected void startModelEdit(@NonNull String modelId) {
        CategoryEditActivity.start(this, modelId);
    }

    @Nullable @Override protected Uri getDeleteUri() {
        return CategoriesProvider.uriCategories();
    }

    @Nullable @Override protected Pair<String, String[]> getDeleteSelection(@NonNull String modelId) {
        return Pair.create(Tables.Categories.ID + "=?", new String[]{String.valueOf(modelId)});
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.Category;
    }
}
