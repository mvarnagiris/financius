package com.code44.finance.ui;

import android.os.Bundle;


public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, OverviewFragment.newInstance()).commit();
        }
    }
}
