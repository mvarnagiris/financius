package com.code44.finance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.code44.finance.R;
import com.code44.finance.data.model.BaseModel;

import java.util.Set;

public abstract class OnModelListActivity extends BaseActivity implements ModelListFragment.OnModelSelectedListener, ModelListFragment.OnModelsSelectedListener {
    public static final String RESULT_EXTRA_MODEL = "RESULT_EXTRA_MODEL";
    public static final String RESULT_EXTRA_MODELS = "RESULT_EXTRA_MODELS";

    private static final String EXTRA_MODE = OnModelListActivity.class.getName() + ".EXTRA_MODE";

    protected ModelListFragment.Mode mode;

    public static Intent makeIntentView(Context context, Class<? extends OnModelListActivity> activityClass) {
        final Intent intent = makeIntent(context, activityClass);
        intent.putExtra(EXTRA_MODE, ModelListFragment.Mode.VIEW);
        return intent;
    }

    public static Intent makeIntentSelect(Context context, Class<? extends OnModelListActivity> activityClass) {
        final Intent intent = makeIntent(context, activityClass);
        intent.putExtra(EXTRA_MODE, ModelListFragment.Mode.SELECT);
        return intent;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readExtras();
        final int containerId = inflateActivity();

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

    @Override public void onModelSelected(BaseModel model) {
        final Intent data = new Intent();
        data.putExtra(RESULT_EXTRA_MODEL, model);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override public void onModelsSelected(Set<BaseModel> models) {
        final Intent data = new Intent();
        final Parcelable[] parcelables = new Parcelable[models.size()];
        int index = 0;
        for (BaseModel model : models) {
            parcelables[index++] = model;
        }
        data.putExtra(RESULT_EXTRA_MODELS, parcelables);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override public void onModelsSelectCanceled() {
        finish();
    }

    protected int inflateActivity() {
        setContentView(R.layout.activity_simple);
        return R.id.content_V;
    }

    protected void readExtras() {
        mode = (ModelListFragment.Mode) getIntent().getSerializableExtra(EXTRA_MODE);
        if (mode == null) {
            throw new IllegalStateException("Activity " + this.getClass().getName() + " must be created with Intent containing " + EXTRA_MODE + " with values from " + ModelListFragment.Mode.class.getName());
        }
    }

    protected abstract int getActionBarTitleResId();

    protected abstract ModelListFragment createModelsFragment(ModelListFragment.Mode mode);
}
