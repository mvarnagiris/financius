package com.code44.finance.ui.tags.edit;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.ui.common.activities.ModelEditActivity;
import com.code44.finance.utils.analytics.Screens;

public class TagEditActivity extends ModelEditActivity<Tag, TagEditData> implements TextWatcher {
    private EditText titleEditText;

    public static void start(Context context, String tagId) {
        makeActivityStarter(context, TagEditActivity.class, tagId).start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_edit);

        // Get views
        titleEditText = (EditText) findViewById(R.id.titleEditText);

        // Setup
        titleEditText.addTextChangedListener(this);
    }

    @NonNull @Override protected CursorLoader getModelCursorLoader(@NonNull String modelId) {
        return Tables.Tags.getQuery().asCursorLoader(this, TagsProvider.uriTag(modelId));
    }

    @NonNull @Override protected Tag getModelFrom(@NonNull Cursor cursor) {
        return Tag.from(cursor);
    }

    @NonNull @Override protected TagEditData createModelEditData() {
        return new TagEditData();
    }

    @NonNull @Override protected ModelEditValidator<TagEditData> createModelEditValidator() {
        return new TagEditValidator(titleEditText);
    }

    @Override protected void onDataChanged(@NonNull TagEditData modelEditData) {
        titleEditText.setText(getModelEditData().getTitle());
        titleEditText.setSelection(titleEditText.getText().length());
    }

    @NonNull @Override protected Uri getSaveUri() {
        return TagsProvider.uriTags();
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.TagEdit;
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override public void afterTextChanged(Editable s) {
        getModelEditData().setTitle(titleEditText.getText().toString());
    }
}
