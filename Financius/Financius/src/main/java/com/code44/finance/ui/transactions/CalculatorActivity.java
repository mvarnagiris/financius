package com.code44.finance.ui.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.code44.finance.ui.BaseActivity;

public class CalculatorActivity extends BaseActivity implements CalculatorFragment.CalculatorListener
{
    public static final String RESULT_EXTRA_AMOUNT = CalculatorActivity.class.getName() + ".RESULT_EXTRA_AMOUNT";
    // -----------------------------------------------------------------------------------------------------------------
    private static final String EXTRA_AMOUNT = CalculatorActivity.class.getName() + ".EXTRA_AMOUNT";
    private static final String EXTRA_ALLOW_NEGATIVE = CalculatorActivity.class.getName() + ".EXTRA_ALLOW_NEGATIVE";
    private static final String EXTRA_RETURN_TWO_DECIMALS = CalculatorActivity.class.getName() + ".EXTRA_RETURN_TWO_DECIMALS";

    public static void startCalculator(Fragment f, int requestCode, double amount, boolean allowNegative, boolean returnTwoDecimals)
    {
        Intent intent = new Intent(f.getActivity(), CalculatorActivity.class);
        intent.putExtra(EXTRA_AMOUNT, amount);
        intent.putExtra(EXTRA_ALLOW_NEGATIVE, allowNegative);
        intent.putExtra(EXTRA_RETURN_TWO_DECIMALS, returnTwoDecimals);
        f.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get extras
        final Intent extras = getIntent();
        final double amount = extras.getDoubleExtra(EXTRA_AMOUNT, 0);
        final boolean allowNegative = extras.getBooleanExtra(EXTRA_ALLOW_NEGATIVE, true);
        final boolean returnTwoDecimals = extras.getBooleanExtra(EXTRA_RETURN_TWO_DECIMALS, true);

        // Setup ActionBar
        getActionBar().hide();

        // Add fragment
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, CalculatorFragment.newInstance(amount, allowNegative, returnTwoDecimals)).commit();
    }

    @Override
    public void onAmountSet(double amount)
    {
        Intent data = new Intent();
        data.putExtra(RESULT_EXTRA_AMOUNT, amount);
        setResult(RESULT_OK, data);
        finish();
    }
}