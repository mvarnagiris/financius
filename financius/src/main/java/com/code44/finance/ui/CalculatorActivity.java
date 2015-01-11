package com.code44.finance.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.ui.common.activities.BaseActivity;

public class CalculatorActivity extends BaseActivity implements CalculatorFragment.CalculatorListener {
    public static final String RESULT_EXTRA_RESULT = "RESULT_EXTRA_RESULT";
    public static final String RESULT_EXTRA_RAW_RESULT = "RESULT_EXTRA_RAW_RESULT";

    private static final String EXTRA_VALUE = "EXTRA_VALUE";
    private static final String EXTRA_RAW_VALUE = "EXTRA_RAW_VALUE";

    public static void start(Activity activity, int requestCode, long value) {
        final Intent intent = makeIntentForActivity(activity, CalculatorActivity.class);
        intent.putExtra(EXTRA_VALUE, value);
        startActivityForResult(activity, intent, requestCode);
    }

    public static void start(Activity activity, int requestCode, double value) {
        final Intent intent = makeIntentForActivity(activity, CalculatorActivity.class);
        intent.putExtra(EXTRA_RAW_VALUE, value);
        startActivityForResult(activity, intent, requestCode);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get extras
        final long value = getIntent().getLongExtra(EXTRA_VALUE, 0);
        final double rawValue = getIntent().getDoubleExtra(EXTRA_RAW_VALUE, 0);

        // Fragment
        if (savedInstanceState == null) {
            final CalculatorFragment fragment = getIntent().hasExtra(EXTRA_VALUE) ? CalculatorFragment.newInstance(value) : CalculatorFragment.newInstance(rawValue);
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
        }
    }

    @Override public void onCalculatorResult(long result, double rawResult) {
        final Intent data = new Intent();
        data.putExtra(RESULT_EXTRA_RESULT, result);
        data.putExtra(RESULT_EXTRA_RAW_RESULT, rawResult);
        setResult(RESULT_OK, data);
        finish();
    }
}
