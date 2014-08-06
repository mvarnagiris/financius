package com.code44.finance.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.code44.finance.R;

import de.greenrobot.event.EventBus;

public abstract class ModelActivity extends BaseActivity {
    protected static final String FRAGMENT_MODEL = "FRAGMENT_MODEL";

    private static final String EXTRA_MODEL_ID = "EXTRA_MODEL_ID";

    protected long modelId;

    public static Intent makeIntent(Context context, Class<? extends ModelActivity> activityClass, long modelId) {
        final Intent intent = makeIntent(context, activityClass);
        intent.putExtra(EXTRA_MODEL_ID, modelId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int contentId = inflateActivity();

        // Register events
        EventBus.getDefault().register(this);

        // Setup ActionBar
        setTitle(getActionBarTitleResId());

        // Get extras
        modelId = getIntent().getLongExtra(EXTRA_MODEL_ID, 0);

        // Fragment
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(contentId, createModelFragment(modelId), FRAGMENT_MODEL).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister events
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.model, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                startEditActivity(modelId);
                return true;

            case R.id.action_delete:
                final Uri deleteUri = getDeleteUri();
                final Pair<String, String[]> deleteSelection = getDeleteSelection();
                DeleteFragment.show(getFragmentManager(), deleteUri, deleteSelection.first, deleteSelection.second);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(DeleteFragment.DeleteEvent event) {
        finish();
    }

    protected abstract int getActionBarTitleResId();

    protected abstract ModelFragment createModelFragment(long modelId);

    protected abstract void startEditActivity(long modelId);

    protected abstract Uri getDeleteUri();

    protected abstract Pair<String, String[]> getDeleteSelection();

    protected int inflateActivity() {
        return android.R.id.content;
    }
}
