package com.code44.finance.ui.categories.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.ui.categories.edit.CategoryEditActivity;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.ModelActivityPresenter;
import com.code44.finance.ui.reports.trends.TrendsChartView;
import com.code44.finance.utils.BaseInterval;
import com.code44.finance.utils.EventBus;

class CategoryActivityPresenter extends ModelActivityPresenter<Category> {
    private final BaseInterval interval;
    private final Currency mainCurrency;

    private CategoryTrendsChartPresenter categoryTrendsChartPresenter;
    private TextView titleTextView;
    private ImageView colorImageView;

    public CategoryActivityPresenter(EventBus eventBus, BaseInterval interval, Currency mainCurrency) {
        super(eventBus);
        this.interval = interval;
        this.mainCurrency = mainCurrency;
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        titleTextView = findView(activity, R.id.titleTextView);
        colorImageView = findView(activity, R.id.colorImageView);

        final TrendsChartView trendsChartView = findView(activity, R.id.trendsChartView);
        categoryTrendsChartPresenter = new CategoryTrendsChartPresenter(trendsChartView, mainCurrency, activity.getSupportLoaderManager(), interval);
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Categories.getQuery(null).asCursorLoader(context, CategoriesProvider.uriCategory(modelId));
    }

    @Override protected Category getModelFrom(Cursor cursor) {
        return Category.from(cursor);
    }

    @Override protected void onModelLoaded(Category model) {
        titleTextView.setText(model.getTitle());
        colorImageView.setColorFilter(model.getColor());
        categoryTrendsChartPresenter.setCategoryAndInterval(model, interval);
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        CategoryEditActivity.start(context, modelId);
    }

    @Override protected Uri getDeleteUri() {
        return CategoriesProvider.uriCategories();
    }

    @Override protected Pair<String, String[]> getDeleteSelection(String modelId) {
        return Pair.create(Tables.Categories.ID + "=?", new String[]{String.valueOf(modelId)});
    }
}
