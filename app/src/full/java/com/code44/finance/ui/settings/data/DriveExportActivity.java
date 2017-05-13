package com.code44.finance.ui.settings.data;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.code44.finance.data.backup.DriveDataExporterRunnable;
import com.code44.finance.ui.playservices.GoogleApiFragment;
import com.code44.finance.utils.errors.ExportError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

public class DriveExportActivity extends ExportActivity implements GoogleApiFragment.GoogleApiListener {
    private static final int REQUEST_DRIVE_DIRECTORY = 1;

    private static final String FRAGMENT_GOOGLE_API = "FRAGMENT_GOOGLE_API";

    private GoogleApiFragment googleApiFragment;

    public static void start(Context context, ExportType exportType) {
        makeActivityStarter(context, DriveExportActivity.class, exportType).start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore state
        googleApiFragment = (GoogleApiFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_GOOGLE_API);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            case REQUEST_DRIVE_DIRECTORY:
                final DriveId driveId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                // TODO Maybe store this driveId to open Google Drive in this folder next time.
                onDriveDirectorySelected(driveId);
                break;
        }
    }

    @Override public void onGoogleApiConnected(GoogleApiClient client) {
        if (isDirectoryRequested) {
            return;
        }

        final IntentSender intentSender = Drive.DriveApi.newOpenFileActivityBuilder()
                .setMimeType(new String[]{"application/vnd.google-apps.folder"})
                .build(googleApiFragment.getClient());

        try {
            startIntentSenderForResult(intentSender, REQUEST_DRIVE_DIRECTORY, null, 0, 0, 0);
            isDirectoryRequested = true;
        } catch (IntentSender.SendIntentException e) {
            throw new ExportError("Unable to show Google Drive.", e);
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

    private void onDriveDirectorySelected(DriveId driveId) {
        exportData(new DriveDataExporterRunnable(googleApiFragment.getClient(), driveId, exportType, this, getEventBus(), getFileTitle()));
    }
}
