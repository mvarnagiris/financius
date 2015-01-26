package com.code44.finance.ui;

import android.os.Bundle;

import com.code44.finance.BuildConfig;
import com.code44.finance.services.StartupService;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.overview.OverviewActivity;
import com.code44.finance.utils.preferences.GeneralPrefs;
import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

public class SplashActivity extends BaseActivity {
    @Inject GeneralPrefs generalPrefs;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        checkVersionUpdate();

        OverviewActivity.start(this);
        StartupService.start(this);
        finish();
    }

    private void checkVersionUpdate() {
        final int lastVersionCode = generalPrefs.getLastVersionCode();
        final int currentVersionCode = BuildConfig.VERSION_CODE;

        if (lastVersionCode < currentVersionCode) {
            // TODO Start upgrade service.
            generalPrefs.setLastVersionCode(currentVersionCode);
        }
    }
}
