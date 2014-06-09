package com.code44.finance.ui.currencies;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.ui.ModelActivity;
import com.code44.finance.ui.ModelFragment;

public class CurrencyActivity extends ModelActivity {
    public static void start(Context context, View expandFrom, long currencyId) {
        final Intent intent = makeIntent(context, CurrencyActivity.class, currencyId);
        start(context, intent, expandFrom);
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.currency;
    }

    @Override
    protected ModelFragment createModelFragment(long modelId) {
        return CurrencyFragment.newInstance(modelId);
    }

    @Override
    protected void startEditActivity(long modelId) {
        CurrencyEditActivity.start(this, null, modelId);
    }
}
