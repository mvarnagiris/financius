package com.code44.finance.ui.tags;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.ui.common.ModelEditActivity;

public class TagEditActivity extends ModelEditActivity<Tag> {
    private EditText titleEditText;

    public static void start(Context context, String tagId) {
        startActivity(context, makeIntent(context, TagEditActivity.class, tagId));
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_tag_edit;
    }

    @Override protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        // Get views
        titleEditText = (EditText) findViewById(R.id.titleEditText);
    }

    @Override protected boolean onSave(Tag model) {
        boolean canSave = true;

        if (TextUtils.isEmpty(model.getTitle())) {
            canSave = false;
            // TODO Show error
        }

        if (canSave) {
            DataStore.insert().model(model).into(this, TagsProvider.uriTags());
        }

        return canSave;
    }

    @Override protected void ensureModelUpdated(Tag model) {
        model.setTitle(titleEditText.getText().toString());
    }

    @Override protected CursorLoader getModelCursorLoader(String modelId) {
        return Tables.Tags.getQuery().asCursorLoader(this, TagsProvider.uriTag(modelId));
    }

    @Override protected Tag getModelFrom(Cursor cursor) {
        return Tag.from(cursor);
    }

    @Override protected void onModelLoaded(Tag model) {
        titleEditText.setText(model.getTitle());
    }
}
