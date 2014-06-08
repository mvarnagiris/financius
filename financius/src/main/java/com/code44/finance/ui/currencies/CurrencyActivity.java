package com.code44.finance.ui.currencies;

import android.content.Context;
import android.view.View;

import com.code44.finance.ui.BaseModelActivity;
import com.code44.finance.ui.BaseModelFragment;

public class CurrencyActivity extends BaseModelActivity {
    public static void start(Context context, View expandFrom, long currencyId) {
        start(context, makeIntent(context, CurrencyActivity.class, currencyId), expandFrom);
    }

    @Override
    protected BaseModelFragment createFragment(long itemId) {
        return CurrencyFragment.newInstance(itemId);
    }
}
