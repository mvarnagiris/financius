package com.code44.finance.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;

import com.code44.finance.R;

public abstract class ModelEditActivity extends ModelActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide ActionBar
        final ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.hide();

        // Get views
        final View cancel_V = findViewById(R.id.cancel_B);
        final View save_V = findViewById(R.id.save_B);

        // Setup
        cancel_V.setOnClickListener(this);
        save_V.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_B:
                cancel();
                break;

            case R.id.save_B:
                save();
                break;
        }
    }

    @Override
    protected int getActionBarTitleResId() {
        return 0;
    }

    @Override
    protected void startEditActivity(long modelId) {
        // Do nothing
    }

    @Override
    protected int inflateActivity() {
        setContentView(R.layout.activity_model_edit);
        return R.id.container_V;
    }

    private void cancel() {
        finish();
    }

    private void save() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_MODEL);
        if (fragment instanceof ModelEditFragment) {
            if (((ModelEditFragment) fragment).save()) {
                finish();
            }
        }
    }
}
