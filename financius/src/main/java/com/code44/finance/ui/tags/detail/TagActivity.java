package com.code44.finance.ui.tags.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.utils.analytics.Analytics;

public class TagActivity extends BaseActivity {
    private TagPresenter tagPresenter;

    public static void start(Context context, String tagId) {
        final Intent intent = makeIntentForActivity(context, TagActivity.class);
        TagPresenter.addExtras(intent, tagId);
        startActivity(context, intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        tagPresenter = new TagPresenter(this);
    }

//    @Override protected Uri getDeleteUri() {
//        return TagsProvider.uriTags();
//    }
//
//    @Override protected Pair<String, String[]> getDeleteSelection() {
//        return Pair.create(Tables.Tags.ID + "=?", new String[]{String.valueOf(modelId)});
//    }
//
//    @Override protected void startModelEdit(String modelId) {
//        TagEditActivity.start(this, modelId);
//    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.Tag;
    }
}
