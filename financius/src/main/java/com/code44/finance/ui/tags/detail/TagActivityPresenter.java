package com.code44.finance.ui.tags.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.ui.common.presenters.ModelActivityPresenter;
import com.code44.finance.ui.tags.edit.TagEditActivity;

class TagActivityPresenter extends ModelActivityPresenter<Tag> {
    private final TextView titleTextView;

    protected TagActivityPresenter(ActionBarActivity activity) {
        super(activity, callbacks);
        titleTextView = findView(activity, R.id.titleTextView);
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Tags.getQuery().asCursorLoader(context, TagsProvider.uriTag(modelId));
    }

    @Override protected Tag getModelFrom(Cursor cursor) {
        return Tag.from(cursor);
    }

    @Override protected void onModelLoaded(Tag model) {
        titleTextView.setText(model.getTitle());
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
