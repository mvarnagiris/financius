package com.code44.finance.ui.tags.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.ActivityPresenter;
import com.code44.finance.utils.analytics.Analytics;

public class TagEditActivity extends BaseActivity {
    public static void start(Context context, String tagId) {
        final Intent intent = makeIntentForActivity(context, TagEditActivity.class);
        TagEditActivityPresenter.addExtras(intent, tagId);
        startActivity(context, intent);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_tag_edit);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new TagEditActivityPresenter(getEventBus());
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.TagEdit;
    }
}
