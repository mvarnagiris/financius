package com.code44.finance.ui.settings.data;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.code44.finance.BuildConfig;
import com.code44.finance.R;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.ui.common.activities.ActivityStarter;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.dialogs.ListDialogFragment;
import com.code44.finance.utils.analytics.Screens;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DataActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_BACKUP_DESTINATION = 1;
    private static final int REQUEST_RESTORE_DESTINATION = 2;
    private static final int REQUEST_RESTORE_AND_MERGE_DESTINATION = 3;
    private static final int REQUEST_EXPORT_CSV_DESTINATION = 4;

    private static final String FRAGMENT_DESTINATION = "FRAGMENT_DESTINATION";

    private static final String ARG_EXPORT_TYPE = "ARG_EXPORT_TYPE";

    @Inject User user;

    public static void start(Context context) {
        ActivityStarter.begin(context, DataActivity.class).start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        // Get views
        final Button backupButton = (Button) findViewById(R.id.backupButton);
        final Button restoreButton = (Button) findViewById(R.id.restoreButton);
        final Button restoreAndMergeButton = (Button) findViewById(R.id.restoreAndMergeButton);
        final Button exportCsvButton = (Button) findViewById(R.id.exportCsvButton);

        // Setup
        backupButton.setOnClickListener(this);
        restoreButton.setOnClickListener(this);
        restoreAndMergeButton.setOnClickListener(this);
        exportCsvButton.setOnClickListener(this);
        if (user.isLoggedIn()) {
            restoreButton.setVisibility(View.GONE);
            restoreAndMergeButton.setVisibility(View.GONE);
        }
    }

    @Override public void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        return true;
    }

    @Override public void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.YourData;
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backupButton:
                chooseSourceOrDestination(REQUEST_BACKUP_DESTINATION, R.string.create_backup);
                break;
            case R.id.restoreButton:
                chooseSourceOrDestination(REQUEST_RESTORE_DESTINATION, R.string.clear_and_restore);
                break;
            case R.id.restoreAndMergeButton:
                chooseSourceOrDestination(REQUEST_RESTORE_AND_MERGE_DESTINATION, R.string.import_backup);
                break;
            case R.id.exportCsvButton:
                chooseSourceOrDestination(REQUEST_EXPORT_CSV_DESTINATION, R.string.export_csv);
                break;
        }
    }

    @Subscribe public void onBackupDestinationSelected(ListDialogFragment.ListDialogEvent event) {
        final int requestCode = event.getRequestCode();
        if ((requestCode != REQUEST_BACKUP_DESTINATION && requestCode != REQUEST_RESTORE_DESTINATION && requestCode != REQUEST_RESTORE_AND_MERGE_DESTINATION && requestCode != REQUEST_EXPORT_CSV_DESTINATION) || event
                .isActionButtonClicked()) {
            return;
        }

        event.dismiss();

        if (event.getRequestCode() == REQUEST_BACKUP_DESTINATION) {
            if (event.getPosition() == 0) {
                FileExportActivity.start(this, ExportActivity.ExportType.Backup);
            } else {
                DriveExportActivity.start(this, ExportActivity.ExportType.Backup);
            }
        } else if (event.getRequestCode() == REQUEST_RESTORE_DESTINATION) {
            if (event.getPosition() == 0) {
                FileImportActivity.start(this, ImportActivity.ImportType.Backup);
            } else {
                DriveImportActivity.start(this, ImportActivity.ImportType.Backup);
            }
        } else if (event.getRequestCode() == REQUEST_RESTORE_AND_MERGE_DESTINATION) {
            if (event.getPosition() == 0) {
                FileImportActivity.start(this, ImportActivity.ImportType.MergeBackup);
            } else {
                DriveImportActivity.start(this, ImportActivity.ImportType.MergeBackup);
            }
        } else {
            if (event.getPosition() == 0) {
                FileExportActivity.start(this, ExportActivity.ExportType.CSV);
            } else {
                DriveExportActivity.start(this, ExportActivity.ExportType.CSV);
            }
        }
    }

    private void chooseSourceOrDestination(int requestCode, int titleResId) {
        final List<ListDialogFragment.ListDialogItem> items = new ArrayList<>();
        items.add(new ListDialogFragment.ListDialogItem(getString(R.string.file)));
        if (!BuildConfig.FLAVOR.equals("noPlay")) {
            items.add(new ListDialogFragment.ListDialogItem(getString(R.string.google_drive)));
        }

        final Bundle args = new Bundle();
        args.putSerializable(ARG_EXPORT_TYPE, DriveExportActivity.ExportType.Backup);

        ListDialogFragment.build(requestCode)
                .setTitle(getString(titleResId))
                .setArgs(args)
                .setNegativeButtonText(getString(R.string.cancel))
                .setItems(items)
                .build()
                .show(getSupportFragmentManager(), FRAGMENT_DESTINATION);
    }
}
