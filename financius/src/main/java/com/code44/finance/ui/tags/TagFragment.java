package com.code44.finance.ui.tags;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.ui.ModelFragment;

public class TagFragment extends ModelFragment<Tag> {
    private TextView title_TV;

    public static TagFragment newInstance(String tagServerId) {
        final Bundle args = makeArgs(tagServerId);

        final TagFragment fragment = new TagFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tag, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        title_TV = (TextView) view.findViewById(R.id.titleTextView);
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelServerId) {
        return Tables.Tags.getQuery().asCursorLoader(context, TagsProvider.uriTag(modelServerId));
    }

    @Override protected Tag getModelFrom(Cursor cursor) {
        return Tag.from(cursor);
    }

    @Override protected void onModelLoaded(Tag model) {
        title_TV.setText(model.getTitle());
    }

    @Override protected Uri getDeleteUri() {
        return TagsProvider.uriTags();
    }

    @Override protected Pair<String, String[]> getDeleteSelection() {
        return Pair.create(Tables.Tags.ID + "=?", new String[]{String.valueOf(modelId)});
    }

    @Override protected void startModelEdit(Context context, String modelServerId) {
        TagEditActivity.start(context, modelServerId);
    }
}
