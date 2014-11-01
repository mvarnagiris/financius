package com.code44.finance.ui.tags;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.adapters.TagsAdapter;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.BaseModel;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.ui.common.BaseModelsAdapter;

public class TagsFragment extends ModelListFragment {
    public static TagsFragment newInstance(Mode mode, Parcelable[] selectedTags) {
        final Bundle args = makeArgs(mode, selectedTags);

        final TagsFragment fragment = new TagsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tags, container, false);
    }

    @Override protected BaseModelsAdapter createAdapter(Context context) {
        return new TagsAdapter(context, getMode() == Mode.MULTI_SELECT);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Tags.getQuery().sortOrder(Tables.Tags.TITLE.getName()).asCursorLoader(context, TagsProvider.uriTags());
    }

    @Override protected BaseModel modelFrom(Cursor cursor) {
        return Tag.from(cursor);
    }

    @Override protected void onModelClick(Context context, View view, int position, String modelServerId, BaseModel model) {
        TagActivity.start(context, modelServerId);
    }

    @Override protected void startModelEdit(Context context, String modelServerId) {
        TagEditActivity.start(context, modelServerId);
    }
}
