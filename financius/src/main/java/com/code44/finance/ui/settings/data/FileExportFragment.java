package com.code44.finance.ui.settings.data;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.data.backup.BackupDataExporter;
import com.code44.finance.data.backup.DataExporter;
import com.code44.finance.ui.FilePickerActivity;

import java.io.File;

public class FileExportFragment extends BaseExportFragment {
    private static final String ARG_EXPORT_TYPE = "ARG_TYPE";

    private static final int REQUEST_DIRECTORY = 1;

    private ExportActivity.ExportType type;

    public static FileExportFragment newInstance(ExportActivity.ExportType exportType) {
        final Bundle args = new Bundle();
        args.putSerializable(ARG_EXPORT_TYPE, exportType);

        final FileExportFragment fragment = new FileExportFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments
        type = (ExportActivity.ExportType) getArguments().getSerializable(ARG_EXPORT_TYPE);

        // Show directory selector if necessary
        if (savedInstanceState == null) {
            FilePickerActivity.startDir(this, REQUEST_DIRECTORY);
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DIRECTORY:
                if (resultCode == Activity.RESULT_OK) {
                    onDirectorySelected(new File(data.getData().getPath()));
                } else {
                    cancel();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onDirectorySelected(File directory) {
        final File file = getFile(directory);
        final DataExporter dataExporter = getDataExporter(file);
        exportData(dataExporter);
    }

    private File getFile(File directory) {
        return new File(directory, getFileTitle());
    }

    private String getFileTitle() {
        return getString(R.string.app_name) + "_" + System.currentTimeMillis() + ".json";
    }

    private DataExporter getDataExporter(File file) {
        switch (type) {
            case Backup:
                return new BackupDataExporter(file, getActivity());
//            case CSV:
//                break;
            default:
                throw new IllegalStateException("Type " + type + " is not supported.");
        }
    }
}
