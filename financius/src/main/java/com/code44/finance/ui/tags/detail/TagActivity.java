package com.code44.finance.ui.tags.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.code44.finance.R;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.utils.analytics.Analytics;

public class TagActivity extends BaseActivity {
    private TagActivityPresenter tagPresenter;

    public static void start(Context context, String tagId) {
        final Intent intent = makeIntentForActivity(context, TagActivity.class);
        TagActivityPresenter.addExtras(intent, tagId);
        startActivity(context, intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        tagPresenter = new TagActivityPresenter(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.model, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                tagPresenter.startModelEdit();
                return true;

            case R.id.action_delete:
                tagPresenter.deleteModel();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.Tag;
    }
}
