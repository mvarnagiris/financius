package com.code44.finance.ui;

import android.os.Bundle;

import com.code44.finance.R;

public class CalculatorActivity extends BaseActivity {
    public static final String RESULT_EXTRA_RESULT = "RESULT_EXTRA_RESULT";

    private static final String EXTRA_VALUE = "EXTRA_VALUE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup ActionBar
        setActionBarTitle(R.string.calculator);
        //noinspection ConstantConditions
        getActionBar().hide();

        // Get extras
        final long value = getIntent().getLongExtra(EXTRA_VALUE, 0);


        // Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, CalculatorFragment.newInstance(value)).commit();
        }
    }
}
