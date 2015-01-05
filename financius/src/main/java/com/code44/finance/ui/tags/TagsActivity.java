package com.code44.finance.ui.tags;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.data.model.Tag;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.presenters.ModelsPresenter;
import com.code44.finance.utils.analytics.Analytics;

import java.util.List;

public class TagsActivity extends BaseActivity implements ModelsPresenter.OnModelPresenterListener {
    private TagsPresenter tagsPresenter;

    public static void start(Context context) {
        final Intent intent = makeIntentForActivity(context, TagsActivity.class);
        TagsPresenter.addViewExtras(intent);
        startActivity(context, intent);
    }

    public static void startMultiSelect(Activity activity, int requestCode, List<Tag> selectedTags) {
        final Intent intent = makeIntentForActivity(activity, TagsActivity.class);
        TagsPresenter.addMultiSelectExtras(intent, selectedTags);
        startActivityForResult(activity, intent, requestCode);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        tagsPresenter = new TagsPresenter(this, savedInstanceState, this);
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        tagsPresenter.onSaveInstanceState(outState);
    }

    @Override public void onModelsSelected(Intent data) {
        setResult(RESULT_OK, data);
        finish();
    }

    @Override public void onCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.TagList;
    }
}
