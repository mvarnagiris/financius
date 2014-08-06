package com.code44.finance.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;

public class CalculatorActivity extends BaseActivity implements CalculatorFragment.CalculatorListener {
    public static final String RESULT_EXTRA_RESULT = "RESULT_EXTRA_RESULT";

    private static final String EXTRA_VALUE = "EXTRA_VALUE";

    public static void start(Fragment fragment, int requestCode, long value) {
        final Intent intent = makeIntent(fragment.getActivity(), CalculatorActivity.class);
        intent.putExtra(EXTRA_VALUE, value);
        startForResult(fragment, intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup ActionBar
        setTitle(R.string.calculator);
        //noinspection ConstantConditions
        getActionBar().hide();

        // Get extras
        final long value = getIntent().getLongExtra(EXTRA_VALUE, 0);


        // Fragment
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(android.R.id.content, CalculatorFragment.newInstance(value)).commit();
        }
    }

    @Override
    public void onCalculatorResult(long result) {
        final Intent data = new Intent();
        data.putExtra(RESULT_EXTRA_RESULT, result);
        setResult(RESULT_OK, data);
        finish();
    }
}
