package com.code44.finance.ui.overview;

import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.ui.BaseActivity;

public class OverviewActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        toolbarHelper.setTitle(R.string.overview);
    }
}
