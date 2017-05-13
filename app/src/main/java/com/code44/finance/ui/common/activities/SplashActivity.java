package com.code44.finance.ui.common.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.BuildConfig;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.data.Migration;
import com.code44.finance.services.StartupService;
import com.code44.finance.ui.overview.OverviewActivity;
import com.code44.finance.utils.preferences.GeneralPrefs;
import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

public class SplashActivity extends BaseActivity {
    @Inject GeneralPrefs generalPrefs;
    @Inject User user;

    public static void start(Context context) {
        ActivityStarter.begin(context, SplashActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
    }

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
            onUpgrade(lastVersionCode);
            // TODO Start upgrade service.
            generalPrefs.setLastVersionCode(currentVersionCode);
        }
    }

    private void onUpgrade(int oldVersion) {
        oldVersion = oldVersion < 96 ? 95 : oldVersion;
        switch (oldVersion) {
            case 95:
                Migration.upgradeTo96(this, user, getSecurity(), generalPrefs);
        }
    }
}
