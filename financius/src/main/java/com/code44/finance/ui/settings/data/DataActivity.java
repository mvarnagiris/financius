package com.code44.finance.ui.settings.data;

import android.content.Context;
import android.os.Bundle;

import com.code44.finance.ui.BaseActivity;
import com.code44.finance.ui.GoogleApiFragment;

public class DataActivity extends BaseActivity {
    private static final String FRAGMENT_GOOGLE_API = "FRAGMENT_GOOGLE_API";

    private static final String UNIQUE_GOOGLE_CLIENT_ID = DataActivity.class.getName();

    private GoogleApiFragment googleApi_F;

    public static void start(Context context) {
        start(context, makeIntent(context, DataActivity.class));
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleApi_F = (GoogleApiFragment) getFragmentManager().findFragmentByTag(FRAGMENT_GOOGLE_API);
        if (googleApi_F == null) {
            googleApi_F = new GoogleApiFragment.Builder(UNIQUE_GOOGLE_CLIENT_ID).setUseDrive(true).build();
            getFragmentManager().beginTransaction().add(googleApi_F, FRAGMENT_GOOGLE_API).commit();
            connectToGoogleApiIfNecessary();
        }
    }

    private void connectToGoogleApiIfNecessary() {
        if (getGeneralPrefs().isGoogleDriveBackupEnabled()) {
            googleApi_F.connect();
        }
    }
}
