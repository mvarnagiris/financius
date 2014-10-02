package com.code44.finance.ui.settings.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.data.backup.BackupDataImporter;
import com.code44.finance.data.backup.DataExporterRunnable;
import com.code44.finance.data.backup.DataImporter;
import com.code44.finance.data.backup.DataImporterRunnable;
import com.code44.finance.data.backup.FileDataImporter;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.qualifiers.Local;
import com.code44.finance.ui.BaseActivity;
import com.code44.finance.ui.FilePickerActivity;
import com.code44.finance.utils.AppError;
import com.code44.finance.utils.GeneralPrefs;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.concurrent.Executor;

import javax.inject.Inject;

public class ImportActivity extends BaseActivity {
    private static final String EXTRA_IMPORT_TYPE = "EXTRA_IMPORT_TYPE";
    private static final String EXTRA_SOURCE = "EXTRA_SOURCE";

    private static final int REQUEST_LOCAL_FILE = 1;

    @Inject @Local Executor localExecutor;
    @Inject GeneralPrefs generalPrefs;
    @Inject DBHelper dbHelper;

    private ImportType importType;
    private Source source;

    public static void start(Context context, ImportType importType, Source source) {
        final Intent intent = makeIntent(context, ImportActivity.class);
        intent.putExtra(EXTRA_IMPORT_TYPE, importType);
        intent.putExtra(EXTRA_SOURCE, source);
        start(context, intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        // Get extras
        importType = (ImportType) getIntent().getSerializableExtra(EXTRA_IMPORT_TYPE);
        source = (Source) getIntent().getSerializableExtra(EXTRA_SOURCE);

        // Setup
        getEventBus().register(this);
        showFileChooser();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCAL_FILE:
                if (resultCode == RESULT_OK) {
                    final String path = data.getData().getPath();
                    onFileSelected(new File(path));
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

    @Subscribe public void onFileDataImporterFinished(FileDataImporter dataImporter) {
        finish();
    }

    private void showFileChooser() {
        FilePickerActivity.startFile(this, REQUEST_LOCAL_FILE, generalPrefs.getLastFileExportPath());
    }

    private void onFileSelected(File file) {
        final DataImporter dataImporter = getFileDataImporter(file);
        importData(dataImporter);
    }

    private DataImporter getFileDataImporter(File file) {
        switch (importType) {
            case Backup:
                return new BackupDataImporter(file, this, dbHelper, false);
            case MergeBackup:
                return new BackupDataImporter(file, this, dbHelper, true);
            default:
                throw new IllegalStateException("Type " + importType + " is not supported.");
        }
    }

    protected void importData(DataImporter dataImporter) {
        localExecutor.execute(new DataImporterRunnable(getEventBus(), dataImporter));
    }

    public static enum ImportType {
        Backup, MergeBackup, CSV
    }

    public static enum Source {
        File, GoogleDrive
    }
}
