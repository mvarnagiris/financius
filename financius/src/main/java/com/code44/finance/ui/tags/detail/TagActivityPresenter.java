package com.code44.finance.ui.tags.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Pair;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.ModelActivityPresenter;
import com.code44.finance.ui.reports.trends.TrendsChartView;
import com.code44.finance.ui.tags.edit.TagEditActivity;
import com.code44.finance.utils.BaseInterval;
import com.code44.finance.utils.EventBus;

class TagActivityPresenter extends ModelActivityPresenter<Tag> {
    private final BaseInterval interval;
    private final CurrencyFormat mainCurrencyFormat;

    private TagTrendsChartPresenter tagTrendsChartPresenter;
    private TextView titleTextView;

    public TagActivityPresenter(EventBus eventBus, BaseInterval interval, CurrencyFormat mainCurrencyFormat) {
        super(eventBus);
        this.interval = interval;
        this.mainCurrencyFormat = mainCurrencyFormat;
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        titleTextView = findView(activity, R.id.titleTextView);

        final TrendsChartView trendsChartView = findView(activity, R.id.trendsChartView);
        tagTrendsChartPresenter = new TagTrendsChartPresenter(trendsChartView, mainCurrencyFormat, activity.getSupportLoaderManager(), interval);
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Tags.getQuery().asCursorLoader(context, TagsProvider.uriTag(modelId));
    }

    @Override protected Tag getModelFrom(Cursor cursor) {
        return Tag.from(cursor);
    }

    @Override protected void onModelLoaded(Tag model) {
        titleTextView.setText(model.getTitle());
        tagTrendsChartPresenter.setTagAndInterval(model, interval);
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        TagEditActivity.start(context, modelId);
    }

    @Override protected Uri getDeleteUri() {
        return TagsProvider.uriTags();
    }

    @Override protected Pair<String, String[]> getDeleteSelection(String modelId) {
        return Pair.create(Tables.Tags.ID + "=?", new String[]{String.valueOf(modelId)});
    }
}
