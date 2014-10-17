package com.code44.finance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.ui.dialogs.DeleteDialogFragment;
import com.squareup.otto.Subscribe;

public abstract class ModelActivity extends BaseActivity {
    protected static final String FRAGMENT_MODEL = "FRAGMENT_MODEL";

    private static final String EXTRA_MODEL_SERVER_ID = "EXTRA_MODEL_SERVER_ID";

    private final Object eventHandler = new Object() {
        @Subscribe public void onItemDeleted(DeleteDialogFragment.DeleteDialogEvent event) {
            if (event.getRequestCode() == ModelFragment.REQUEST_DELETE && event.isPositiveClicked()) {
                finish();
            }
        }
    };

    protected String modelServerId;

    public static Intent makeIntent(Context context, Class<? extends ModelActivity> activityClass, String modelServerId) {
        final Intent intent = makeIntent(context, activityClass);
        intent.putExtra(EXTRA_MODEL_SERVER_ID, modelServerId);
        return intent;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readExtras();
        final int contentId = inflateActivity();

        // Setup ActionBar
        getSupportActionBar().setTitle(getActionBarTitleResId());

        // Fragment
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(contentId, createModelFragment(modelServerId), FRAGMENT_MODEL).commit();
        }

        getEventBus().register(eventHandler);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        getEventBus().unregister(eventHandler);
    }

    protected abstract int getActionBarTitleResId();

    protected abstract ModelFragment createModelFragment(String modelServerId);

    protected void readExtras() {
        modelServerId = getIntent().getStringExtra(EXTRA_MODEL_SERVER_ID);
    }

    protected int inflateActivity() {
        setContentView(R.layout.activity_simple);
        return R.id.content_V;
    }
}
