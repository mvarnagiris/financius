package com.code44.finance.ui.tags.edit;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.ModelEditActivityPresenter;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.ThemeUtils;

class TagEditActivityPresenter extends ModelEditActivityPresenter<Tag> implements TextWatcher {
    private static final String STATE_TITLE = "STATE_TITLE";

    private EditText titleEditText;
    private String title;

    public TagEditActivityPresenter(EventBus eventBus) {
        super(eventBus);
    }

    @Override public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        titleEditText = findView(activity, R.id.titleEditText);
        titleEditText.addTextChangedListener(this);

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(STATE_TITLE);
            onDataChanged(getStoredModel());
        }
    }

    @Override public void onActivitySaveInstanceState(BaseActivity activity, Bundle outState) {
        super.onActivitySaveInstanceState(activity, outState);
        outState.putString(STATE_TITLE, title);
    }

    @Override protected void onDataChanged(Tag storedModel) {
        titleEditText.setText(getTitle());
        titleEditText.setSelection(titleEditText.getText().length());
    }

    @Override protected boolean onSave() {
        boolean canSave = true;

        final String title = getTitle();
        if (TextUtils.isEmpty(title)) {
            canSave = false;
            titleEditText.setHintTextColor(ThemeUtils.getColor(titleEditText.getContext(), R.attr.textColorNegative));
        }

        if (canSave) {
            final Tag tag = new Tag();
            tag.setId(getId());
            tag.setTitle(title);

            DataStore.insert().model(tag).into(titleEditText.getContext(), TagsProvider.uriTags());
        }

        return canSave;
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Tags.getQuery().asCursorLoader(context, TagsProvider.uriTag(modelId));
    }

    @Override protected Tag getModelFrom(Cursor cursor) {
        return Tag.from(cursor);
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override public void afterTextChanged(Editable s) {
        title = titleEditText.getText().toString();
    }

    private String getId() {
        return getStoredModel() != null ? getStoredModel().getId() : null;
    }

    private String getTitle() {
        if (title != null) {
            return title;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getTitle();
        }

        return null;
    }
}
