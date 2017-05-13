package com.code44.finance.ui.tags.list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;

import com.code44.finance.R;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.ui.common.activities.ActivityStarter;
import com.code44.finance.ui.common.activities.ModelsActivity;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.tags.detail.TagActivity;
import com.code44.finance.ui.tags.edit.TagEditActivity;
import com.code44.finance.utils.analytics.Screens;

import java.util.Collection;

public class TagsActivity extends ModelsActivity<Tag, TagsAdapter> {
    private static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";

    private Category category;

    public static void startView(Context context) {
        ActivityStarter.begin(context, TagsActivity.class).modelsView().start();
    }

    public static void startMultiSelect(Context context, int requestCode, Collection<Tag> selectedTags, Category category) {
        ActivityStarter.begin(context, TagsActivity.class)
                .modelsMultiSelect(selectedTags)
                .extra(EXTRA_CATEGORY, category)
                .startForResult(requestCode);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get extras
        category = getIntent().getParcelableExtra(EXTRA_CATEGORY);
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_tags;
    }

    @Override protected TagsAdapter createAdapter(ModelsAdapter.OnModelClickListener<Tag> defaultOnModelClickListener, Mode mode) {
        return new TagsAdapter(defaultOnModelClickListener, mode);
    }

    @Override protected CursorLoader getModelsCursorLoader() {
        final Query query = Tables.Tags.getQuery();
        if (category != null) {
            // TODO Sort tags for this category at the top
        }
        query.sortOrder("lower(" + Tables.Tags.TITLE + ")");
        return query.asCursorLoader(this, TagsProvider.uriTags());
    }

    @Override protected void onModelClick(Tag model) {
        TagActivity.start(this, model.getId());
    }

    @Override protected void startModelEdit(String modelId) {
        TagEditActivity.start(this, modelId);
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.TagList;
    }
}
