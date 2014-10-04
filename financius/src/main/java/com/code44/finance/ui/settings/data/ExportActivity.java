package com.code44.finance.ui.settings.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.data.backup.BackupDataExporter;
import com.code44.finance.data.backup.CsvDataExporter;
import com.code44.finance.data.backup.DataExporter;
import com.code44.finance.data.backup.DataExporterRunnable;
import com.code44.finance.data.backup.FileDataExporter;
import com.code44.finance.qualifiers.Local;
import com.code44.finance.ui.BaseActivity;
import com.code44.finance.ui.FilePickerActivity;
import com.code44.finance.utils.GeneralPrefs;
import com.code44.finance.utils.errors.AppError;
import com.squareup.otto.Subscribe;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

import java.io.File;
import java.util.concurrent.Executor;

import javax.inject.Inject;

public class ExportActivity extends BaseActivity {
    private static final String EXTRA_EXPORT_TYPE = "EXTRA_EXPORT_TYPE";
    private static final String EXTRA_DESTINATION = "EXTRA_DESTINATION";

    private static final int REQUEST_LOCAL_DIRECTORY = 1;

    @Inject GeneralPrefs generalPrefs;
    @Inject @Local Executor localExecutor;

    private ExportType exportType;
    private Destination destination;

    public static void start(Context context, ExportType exportType, Destination destination) {
        final Intent intent = makeIntent(context, ExportActivity.class);
        intent.putExtra(EXTRA_EXPORT_TYPE, exportType);
        intent.putExtra(EXTRA_DESTINATION, destination);
        start(context, intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        // Get extras
        exportType = (ExportType) getIntent().getSerializableExtra(EXTRA_EXPORT_TYPE);
        destination = (Destination) getIntent().getSerializableExtra(EXTRA_DESTINATION);

        // Setup
        getEventBus().register(this);
        if (savedInstanceState == null) {
            showDirectoryChooser();
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCAL_DIRECTORY:
                if (resultCode == Activity.RESULT_OK) {
                    final String path = data.getData().getPath();
                    generalPrefs.setLastFileExportPath(path);
                    onDirectorySelected(new File(path));
                } else {
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        getEventBus().unregister(this);
    }

    @Override protected void onHandleError(AppError error) {
        super.onHandleError(error);
        if (error instanceof DataExporterRunnable.DataExportError) {
            finish();
        }
    }

    @Subscribe public void onFileDataExporterFinished(FileDataExporter dataExporter) {
        finish();
    }

    private void onDirectorySelected(File directory) {
        final File file = getFile(directory);
        final DataExporter dataExporter = getFileDataExporter(file);
        exportData(dataExporter);
    }

    private File getFile(File directory) {
        return new File(directory, getFileTitle());
    }

    private String getFileTitle() {
        final String extention;
        if (exportType == ExportActivity.ExportType.Backup) {
            extention = ".json";
        } else {
            extention = ".csv";
        }

        final String date = DateUtils.formatDateTime(this, new DateTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR);
        return getString(R.string.app_name) + " " + date + extention;
    }

    private DataExporter getFileDataExporter(File file) {
        switch (exportType) {
            case Backup:
                return new BackupDataExporter(file, this);
            case CSV:
                return new CsvDataExporter(file, this);
            default:
                throw new IllegalStateException("Type " + exportType + " is not supported.");
        }
    }

    private void exportData(DataExporter dataExporter) {
        localExecutor.execute(new DataExporterRunnable(getEventBus(), dataExporter));
    }

    private void showDirectoryChooser() {
        FilePickerActivity.startDir(this, REQUEST_LOCAL_DIRECTORY, generalPrefs.getLastFileExportPath());
    }

    public static enum ExportType {
        Backup, CSV
    }

    public static enum Destination {
        File, GoogleDrive
    }
}
