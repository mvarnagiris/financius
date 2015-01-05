package com.code44.finance.ui.tags;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsPresenter;

public class TagsPresenter extends ModelsPresenter<Tag> {
    public TagsPresenter(ActionBarActivity activity) {
        super(activity);
    }

    @Override protected ModelsAdapter<Tag> createAdapter(ModelsAdapter.OnModelClickListener<Tag> defaultOnModelClickListener) {
        return new TagsAdapterV2(defaultOnModelClickListener);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Tags.getQuery().sortOrder("lower(" + Tables.Tags.TITLE + ")").asCursorLoader(context, TagsProvider.uriTags());
    }

    @Override protected void onModelClick(Context context, View view, Tag model, Cursor cursor, int position) {
        TagActivity.start(context, model.getId());
    }
}
