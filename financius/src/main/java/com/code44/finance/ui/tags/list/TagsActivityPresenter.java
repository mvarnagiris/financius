package com.code44.finance.ui.tags.list;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.view.View;

import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.ui.tags.detail.TagActivity;

class TagsActivityPresenter extends ModelsActivityPresenter<Tag> {
    public TagsActivityPresenter(OnModelPresenterListener onModelPresenterListener) {
        super(onModelPresenterListener);
    }

    @Override protected ModelsAdapter<Tag> createAdapter(ModelsAdapter.OnModelClickListener<Tag> defaultOnModelClickListener) {
        return new TagsAdapter(defaultOnModelClickListener);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Tags.getQuery().sortOrder("lower(" + Tables.Tags.TITLE + ")").asCursorLoader(context, TagsProvider.uriTags());
    }

    @Override protected void onModelClick(Context context, View view, Tag model, Cursor cursor, int position) {
        TagActivity.start(context, model.getId());
    }
}
