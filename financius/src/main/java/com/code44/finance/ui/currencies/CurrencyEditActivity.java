package com.code44.finance.ui.currencies;

import android.content.Context;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.ui.ModelEditActivity;
import com.code44.finance.ui.ModelFragment;

public class CurrencyEditActivity extends ModelEditActivity {
    public static void start(Context context, View expandFrom, long currencyId) {
        start(context, makeIntent(context, CurrencyEditActivity.class, currencyId), expandFrom);
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.currency;
    }

    @Override
    protected ModelFragment createModelFragment(long modelId) {
        return CurrencyEditFragment.newInstance(modelId);
    }
}
