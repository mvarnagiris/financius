package com.code44.finance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.data.db.model.BaseModel;

public abstract class ModelListActivity extends BaseActivity implements ModelListFragment.ModelListCallback {
    public static final String RESULT_EXTRA_MODEL_ID = "RESULT_EXTRA_MODEL_ID";
    public static final String RESULT_EXTRA_MODEL = "RESULT_EXTRA_MODEL";

    private static final String EXTRA_MODE = ModelListActivity.class.getName() + ".EXTRA_MODE";

    protected ModelListFragment.Mode mode;

    public static Intent makeIntentView(Context context, Class<? extends ModelListActivity> activityClass) {
        final Intent intent = makeIntent(context, activityClass);
        intent.putExtra(EXTRA_MODE, ModelListFragment.Mode.VIEW);
        return intent;
    }

    public static Intent makeIntentSelect(Context context, Class<? extends ModelListActivity> activityClass) {
        final Intent intent = makeIntent(context, activityClass);
        intent.putExtra(EXTRA_MODE, ModelListFragment.Mode.SELECT);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int containerId = inflateActivity();

        // Get extras
        mode = (ModelListFragment.Mode) getIntent().getSerializableExtra(EXTRA_MODE);
        if (mode == null) {
            throw new IllegalStateException("Activity " + this.getClass().getName() + " must be created with Intent containing " + EXTRA_MODE + " with values from " + ModelListFragment.Mode.class.getName());
        }

        // Setup ActionBar
        if (mode == ModelListFragment.Mode.SELECT) {
            toolbarHelper.setTitle(R.string.select);
        } else {
            toolbarHelper.setTitle(getActionBarTitleResId());
        }

        final boolean addFragmentHere = containerId != 0;
        if (addFragmentHere && savedInstanceState == null) {
            ModelListFragment fragment = createModelsFragment(mode);
            if (fragment != null) {
                getFragmentManager().beginTransaction().add(containerId, fragment).commit();
            }
        }
    }

    @Override
    public void onModelSelected(String modelServerId, BaseModel model) {
        final Intent data = new Intent();
        data.putExtra(RESULT_EXTRA_MODEL_ID, modelServerId);
        data.putExtra(RESULT_EXTRA_MODEL, model);
        setResult(RESULT_OK, data);
        finish();
    }

    protected int inflateActivity() {
        setContentView(R.layout.activity_simple);
        return R.id.content_V;
    }

    protected abstract int getActionBarTitleResId();

    protected abstract ModelListFragment createModelsFragment(ModelListFragment.Mode mode);
}
