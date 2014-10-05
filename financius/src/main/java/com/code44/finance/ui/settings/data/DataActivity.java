package com.code44.finance.ui.settings.data;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;

import com.code44.finance.R;
import com.code44.finance.ui.BaseActivity;

public class DataActivity extends BaseActivity {
    public static void start(Context context) {
        start(context, makeIntent(context, DataActivity.class));
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        toolbarHelper.setTitle(R.string.your_data);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.content_V, DataFragment.newInstance()).commit();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
