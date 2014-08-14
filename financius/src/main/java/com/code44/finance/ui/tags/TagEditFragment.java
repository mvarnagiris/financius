package com.code44.finance.ui.tags;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.ui.ModelEditFragment;

public class TagEditFragment extends ModelEditFragment<Tag> {
    private EditText title_ET;

    public static TagEditFragment newInstance(String tagServerId) {
        final Bundle args = makeArgs(tagServerId);

        final TagEditFragment fragment = new TagEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tag_edit, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        title_ET = (EditText) view.findViewById(R.id.title_ET);
    }

    @Override public boolean onSave(Context context, Tag model) {
        boolean canSave = true;

        if (TextUtils.isEmpty(model.getTitle())) {
            canSave = false;
            // TODO Show error
        }

        if (canSave) {
            DataStore.insert().model(model).into(context, TagsProvider.uriTags());
        }

        return canSave;
    }

    @Override protected void ensureModelUpdated(Tag model) {
        model.setTitle(title_ET.getText().toString());
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelServerId) {
        return Tables.Tags.getQuery().asCursorLoader(context, TagsProvider.uriTag(modelServerId));
    }

    @Override protected Tag getModelFrom(Cursor cursor) {
        return Tag.from(cursor);
    }

    @Override protected void onModelLoaded(Tag model) {
        title_ET.setText(model.getTitle());
    }
}
