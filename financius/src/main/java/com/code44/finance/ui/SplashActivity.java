package com.code44.finance.ui;

import android.os.Bundle;

import com.code44.finance.ui.overview.OverviewActivity;

public class SplashActivity extends BaseActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OverviewActivity.start(this);
        finish();
    }
}
