package com.code44.finance.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

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
        final View leftMargin_V = findViewById(R.id.leftMargin_V);
        final View rightMargin_V = findViewById(R.id.rightMargin_V);
        final View leftSeparator_V = findViewById(R.id.leftSeparator_V);
        final View rightSeparator_V = findViewById(R.id.rightSeparator_V);
        final View cancel_V = findViewById(R.id.cancel_B);
        final View save_V = findViewById(R.id.save_B);
        final TextView title_TV = (TextView) findViewById(R.id.title_TV);

        // Setup
        title_TV.setText(getActionBarTitleResId());
        cancel_V.setOnClickListener(this);
        save_V.setOnClickListener(this);
        if (getResources().getDimensionPixelSize(R.dimen.margin_edit_container) == 0) {
            leftMargin_V.setVisibility(View.GONE);
            rightMargin_V.setVisibility(View.GONE);
            leftSeparator_V.setVisibility(View.GONE);
            rightSeparator_V.setVisibility(View.GONE);
        }
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
