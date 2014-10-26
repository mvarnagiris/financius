package com.code44.finance.ui.overview;

import android.content.Context;
import android.content.Intent;

import com.code44.finance.ui.DrawerActivity;

public class OverviewActivity extends DrawerActivity {
    public static void start(Context context) {
        final Intent intent = makeIntent(context, OverviewActivity.class);
        start(context, intent);
    }
}
