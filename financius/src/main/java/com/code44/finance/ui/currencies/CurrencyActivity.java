package com.code44.finance.ui.currencies;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import com.code44.finance.R;
import com.code44.finance.ui.ModelActivity;
import com.code44.finance.ui.ModelFragment;

public class CurrencyActivity extends ModelActivity {
    public static void start(Context context, String currencyServerId) {
        final Intent intent = makeIntent(context, CurrencyActivity.class, currencyServerId);
        start(context, intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getToolbar().setElevation(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.currency;
    }

    @Override
    protected ModelFragment createModelFragment(String modelServerId) {
        return CurrencyFragment.newInstance(modelServerId);
    }
}
