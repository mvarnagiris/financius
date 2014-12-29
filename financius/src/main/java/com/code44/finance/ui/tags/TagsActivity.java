package com.code44.finance.ui.tags;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.view.Menu;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Model;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.ui.common.BaseModelsAdapter;
import com.code44.finance.ui.common.ModelListActivity;
import com.code44.finance.utils.analytics.Analytics;

import java.util.List;

public class TagsActivity extends ModelListActivity {
    public static void start(Context context) {
        startActivity(context, makeViewIntent(context, TagsActivity.class));
    }

    public static void startMultiSelect(Activity activity, int requestCode, List<Tag> selectedTags) {
        startActivityForResult(activity, makeMultiSelectIntent(activity, TagsActivity.class, selectedTags), requestCode);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_tags;
    }

    @Override protected BaseModelsAdapter createAdapter() {
        return new TagsAdapter(this, getMode() == Mode.MULTI_SELECT);
    }

    @Override protected CursorLoader getModelsCursorLoader() {
        return Tables.Tags.getQuery().sortOrder("lower(" + Tables.Tags.TITLE + ")").asCursorLoader(this, TagsProvider.uriTags());
    }

    @Override protected Model modelFrom(Cursor cursor) {
        return Tag.from(cursor);
    }

    @Override protected void onModelClick(View view, int position, String modelId, Model model) {
        TagActivity.start(this, modelId);
    }

    @Override protected void startModelEdit(String modelId) {
        TagEditActivity.start(this, modelId);
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.TagList;
    }
}
