package com.code44.finance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;

public abstract class BaseModelActivity extends BaseActivity {
    private static final String EXTRA_ITEM_ID = "EXTRA_ITEM_ID";

    public static Intent makeIntent(Context context, Class modelClass, long itemId) {
        final Intent intent = makeIntent(context, modelClass);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model);

        final long itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, 0);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container_V, createFragment(itemId)).commit();
        }
    }

    protected abstract BaseFragment createFragment(long itemId);
}
