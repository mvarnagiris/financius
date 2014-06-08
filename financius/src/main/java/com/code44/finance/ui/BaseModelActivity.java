package com.code44.finance.ui;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.code44.finance.R;

public abstract class BaseModelActivity extends BaseActivity implements View.OnClickListener {
    private static final String EXTRA_ITEM_ID = "EXTRA_ITEM_ID";

    private static final String FRAGMENT_MODEL = "FRAGMENT_MODEL";

    private BaseModelFragment model_F;

    public static Intent makeIntent(Context context, Class modelClass, long itemId) {
        final Intent intent = makeIntent(context, modelClass);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model);

        // Hide ActionBar
        final ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.hide();

        // Get extras
        final long itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, 0);

        // Get views
        final View discard_V = findViewById(R.id.discard_B);
        final View done_V = findViewById(R.id.done_B);

        // Setup
        discard_V.setOnClickListener(this);
        done_V.setOnClickListener(this);

        // Add fragment
        model_F = (BaseModelFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_MODEL);
        if (model_F == null) {
            model_F = createFragment(itemId);
            getSupportFragmentManager().beginTransaction().add(R.id.container_V, model_F, FRAGMENT_MODEL).commit();
        }
    }

    protected abstract BaseModelFragment createFragment(long itemId);

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.discard_B:
                discard();
                break;

            case R.id.done_B:
                done();
                break;
        }
    }

    private void discard() {
        finish();
    }

    private void done() {
        if (model_F.onSave()) {
            finish();
        }
    }
}
