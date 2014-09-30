package com.code44.finance.ui.settings.data;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.data.backup.BackupDataImporter;
import com.code44.finance.data.backup.DataImporter;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.ui.FilePickerActivity;
import com.code44.finance.utils.GeneralPrefs;

import java.io.File;

import javax.inject.Inject;

public class FileImportFragment extends BaseImportFragment<File> {
    private static final String ARG_IMPORT_TYPE = "ARG_TYPE";

    private static final int REQUEST_FILE = 1;

    @Inject GeneralPrefs generalPrefs;
    @Inject DBHelper dbHelper;

    private ImportActivity.ImportType type;
    private boolean requestFile = false;

    public static FileImportFragment newInstance(ImportActivity.ImportType importType) {
        final Bundle args = new Bundle();
        args.putSerializable(ARG_IMPORT_TYPE, importType);

        final FileImportFragment fragment = new FileImportFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments
        type = (ImportActivity.ImportType) getArguments().getSerializable(ARG_IMPORT_TYPE);

        // Show directory selector if necessary
        if (savedInstanceState == null) {
            requestFile = true;
        }
    }

    @Override public void onResume() {
        super.onResume();
        if (requestFile) {
            requestFile = false;
            FilePickerActivity.startFile(this, REQUEST_FILE, generalPrefs.getLastFileExportPath());
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    final String path = data.getData().getPath();
                    onFileSelected(new File(path));
                } else {
                    cancel();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onFileSelected(File file) {
        final DataImporter<File> dataImporter = getDataExporter();
        importData(dataImporter, file);
    }

    private DataImporter<File> getDataExporter() {
        switch (type) {
            case Backup:
                return new BackupDataImporter(getActivity(), dbHelper, false);
            case MergeBackup:
                return new BackupDataImporter(getActivity(), dbHelper, true);
//            case CSV:
//                break;
            default:
                throw new IllegalStateException("Type " + type + " is not supported.");
        }
    }
}
