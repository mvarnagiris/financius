package com.code44.finance.ui.settings.data;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.code44.finance.data.backup.DriveDataImporterRunnable;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.ui.playservices.GoogleApiFragment;
import com.code44.finance.utils.analytics.Screens;
import com.code44.finance.utils.errors.ImportError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import javax.inject.Inject;

public class DriveImportActivity extends ImportActivity implements GoogleApiFragment.GoogleApiListener {
    private static final int REQUEST_DRIVE_FILE = 1;

    private static final String FRAGMENT_GOOGLE_API = "FRAGMENT_GOOGLE_API";

    @Inject DBHelper dbHelper;

    private GoogleApiFragment googleApiFragment;

    public static void start(Context context, ImportActivity.ImportType importType) {
        makeActivityStarter(context, DriveImportActivity.class, importType).start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore state
        googleApiFragment = (GoogleApiFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_GOOGLE_API);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (googleApiFragment != null) {
            if (googleApiFragment.handleOnActivityResult(requestCode, resultCode)) {
                return;
            }
        }

        if (resultCode != RESULT_OK) {
            finish();
            return;
        }

        switch (requestCode) {
            case REQUEST_DRIVE_FILE:
                final DriveId driveId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                onDriveFileSelected(driveId);
                break;
        }
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.Import;
    }

    @Override public void onGoogleApiConnected(GoogleApiClient client) {
        if (isFileRequested) {
            return;
        }

        final IntentSender intentSender = Drive.DriveApi.newOpenFileActivityBuilder()
                .setMimeType(new String[]{"application/json"})
                .build(googleApiFragment.getClient());
        try {
            startIntentSenderForResult(intentSender, REQUEST_DRIVE_FILE, null, 0, 0, 0);
            isFileRequested = true;
        } catch (IntentSender.SendIntentException e) {
            throw new ImportError("Unable to show Google Drive.", e);
        }
    }

    @Override public void onGoogleApiFailed() {
        showError(new Throwable("Failed to connect to Google Drive."));
    }

    @Override public void onGoogleApiNotAvailable() {
        showError(new Throwable("Google Drive is not available."));
    }

    @Override protected boolean startProcess() {
        googleApiFragment = (GoogleApiFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_GOOGLE_API);
        if (googleApiFragment == null) {
            googleApiFragment = GoogleApiFragment.build().setUseDrive(true).build();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, googleApiFragment, FRAGMENT_GOOGLE_API).commit();
        }
        googleApiFragment.connect();
        return false;
    }

    private void onDriveFileSelected(DriveId driveId) {
        importData(new DriveDataImporterRunnable(googleApiFragment.getClient(), driveId, importType, this, dbHelper, getEventBus()));
    }
}
