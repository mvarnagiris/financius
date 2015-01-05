package com.code44.finance.ui.tags;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.data.model.Tag;
import com.code44.finance.ui.common.BaseActivity;

import java.util.List;

public class TagsActivityV2 extends BaseActivity {
    private TagsPresenter tagsPresenter;

    public static void start(Context context) {
        final Intent intent = makeIntentForActivity(context, TagsActivityV2.class);
        TagsPresenter.addViewExtras(intent);
        startActivity(context, intent);
    }

    public static void startMultiSelect(Activity activity, int requestCode, List<Tag> selectedTags) {
        final Intent intent = makeIntentForActivity(activity, TagsActivityV2.class);
        TagsPresenter.addMultiSelectExtras(intent, selectedTags);
        startActivityForResult(activity, intent, requestCode);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags_v2);

        tagsPresenter = new TagsPresenter(this);
    }
}
