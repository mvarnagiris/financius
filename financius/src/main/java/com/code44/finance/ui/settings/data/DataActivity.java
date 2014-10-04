package com.code44.finance.ui.settings.data;

import android.content.Context;
import android.os.Bundle;

import com.code44.finance.ui.BaseActivity;

public class DataActivity extends BaseActivity {
    public static void start(Context context) {
        start(context, makeIntent(context, DataActivity.class));
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, DataFragment.newInstance()).commit();
        }
    }
}
