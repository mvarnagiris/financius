package com.code44.finance.ui.tags.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.util.Pair;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.common.activities.ModelActivity;
import com.code44.finance.ui.reports.trends.TrendsChartView;
import com.code44.finance.ui.tags.edit.TagEditActivity;
import com.code44.finance.utils.analytics.Screens;
import com.code44.finance.utils.interval.CurrentInterval;

import javax.inject.Inject;

public class TagActivity extends ModelActivity<Tag> {
    @Inject CurrentInterval currentInterval;
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    private TagTrendsChartPresenter tagTrendsChartPresenter;
    private TextView titleTextView;

    public static void start(Context context, String tagId) {
        makeActivityStarter(context, TagActivity.class, tagId).start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        // Get views
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        final TrendsChartView trendsChartView = (TrendsChartView) findViewById(R.id.trendsChartView);

        // Setup
        tagTrendsChartPresenter = new TagTrendsChartPresenter(trendsChartView, currenciesManager, amountFormatter, getSupportLoaderManager(), currentInterval);
    }

    @NonNull @Override protected CursorLoader getModelCursorLoader(@NonNull String modelId) {
        return Tables.Tags.getQuery().asCursorLoader(this, TagsProvider.uriTag(modelId));
    }

    @NonNull @Override protected Tag getModelFrom(@NonNull Cursor cursor) {
        return Tag.from(cursor);
    }

    @Override protected void onModelLoaded(@NonNull Tag model) {
        titleTextView.setText(model.getTitle());
        tagTrendsChartPresenter.setTagAndInterval(model, currentInterval);
    }

    @Override protected void startModelEdit(@NonNull String modelId) {
        TagEditActivity.start(this, modelId);
    }

    @Nullable @Override protected Uri getDeleteUri() {
        return TagsProvider.uriTags();
    }

    @Nullable @Override protected Pair<String, String[]> getDeleteSelection(@NonNull String modelId) {
        return Pair.create(Tables.Tags.ID + "=?", new String[]{String.valueOf(modelId)});
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.Tag;
    }
}
