package com.code44.finance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;

public abstract class ModelActivity extends BaseActivity {
    protected static final String FRAGMENT_MODEL = "FRAGMENT_MODEL";

    private static final String EXTRA_MODEL_SERVER_ID = "EXTRA_MODEL_SERVER_ID";

    protected String modelServerId;

    public static Intent makeIntent(Context context, Class<? extends ModelActivity> activityClass, String modelServerId) {
        final Intent intent = makeIntent(context, activityClass);
        intent.putExtra(EXTRA_MODEL_SERVER_ID, modelServerId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int contentId = inflateActivity();

        // Setup ActionBar
        toolbarHelper.setTitle(getActionBarTitleResId());

        // Get extras
        modelServerId = getIntent().getStringExtra(EXTRA_MODEL_SERVER_ID);

        // Fragment
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(contentId, createModelFragment(modelServerId), FRAGMENT_MODEL).commit();
        }
    }

    protected abstract int getActionBarTitleResId();

    protected abstract ModelFragment createModelFragment(String modelServerId);

    protected int inflateActivity() {
        setContentView(R.layout.activity_simple);
        return R.id.content_V;
    }
}
