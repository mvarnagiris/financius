package com.code44.finance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.data.db.model.BaseModel;

public abstract class ModelListActivity extends BaseActivity implements ModelListFragment.ModelListFragmentCallbacks {
    public static final String RESULT_EXTRA_MODEL_ID = "RESULT_EXTRA_MODEL_ID";
    public static final String RESULT_EXTRA_MODEL = "RESULT_EXTRA_MODEL";

    private static final String EXTRA_MODE = ModelListActivity.class.getName() + ".EXTRA_MODE";

    protected Mode mode;

    public static Intent makeIntentView(Context context, Class<? extends ModelListActivity> activityClass) {
        final Intent intent = makeIntent(context, activityClass);
        intent.putExtra(EXTRA_MODE, Mode.VIEW);
        return intent;
    }

    public static Intent makeIntentSelect(Context context, Class<? extends ModelListActivity> activityClass) {
        final Intent intent = makeIntent(context, activityClass);
        intent.putExtra(EXTRA_MODE, Mode.SELECT);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int containerId = inflateActivity();

        // Get extras
        mode = (Mode) getIntent().getSerializableExtra(EXTRA_MODE);
        if (mode == null) {
            throw new IllegalStateException("Activity " + this.getClass().getName() + " must be created with Intent containing " + EXTRA_MODE + " with values from " + Mode.class.getName());
        }

        // Setup ActionBar
        if (mode == Mode.SELECT) {
            setActionBarTitle(R.string.select);
        } else {
            setActionBarTitle(getActionBarTitleResId());
        }

        final boolean addFragmentHere = containerId != 0;
        if (addFragmentHere && savedInstanceState == null) {
            ModelListFragment fragment = createModelsFragment(mode);
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().add(containerId, fragment).commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.models, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                startModelEditActivity(null, 0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onModelClickListener(View view, int position, long modelId, BaseModel model) {
        if (mode == Mode.SELECT) {
            final Intent data = new Intent();
            data.putExtra(RESULT_EXTRA_MODEL_ID, modelId);
            data.putExtra(RESULT_EXTRA_MODEL, model);
            setResult(RESULT_OK, data);
            finish();
        } else {
            startModelActivity(view, modelId);
        }
    }

    protected int inflateActivity() {
        return android.R.id.content;
    }

    protected abstract int getActionBarTitleResId();

    protected abstract ModelListFragment createModelsFragment(Mode mode);

    protected abstract void startModelActivity(View expandFrom, long modelId);

    protected abstract void startModelEditActivity(View expandFrom, long modelId);

    public static enum Mode {
        VIEW, SELECT
    }
}
