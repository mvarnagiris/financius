package com.code44.finance.ui.currencies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.ui.ModelActivity;
import com.code44.finance.ui.ModelFragment;

public class CurrencyActivity extends ModelActivity {
    public static void start(Context context, long currencyId) {
        final Intent intent = makeIntent(context, CurrencyActivity.class, currencyId);
        start(context, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbarHelper.setElevation(0);
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
        CurrencyEditActivity.start(this, modelId);
    }

    @Override
    protected Uri getDeleteUri() {
        return CurrenciesProvider.uriCurrencies();
    }

    @Override
    protected Pair<String, String[]> getDeleteSelection() {
        return Pair.create(Tables.Currencies.ID + "=?", new String[]{String.valueOf(modelId)});
    }
}
