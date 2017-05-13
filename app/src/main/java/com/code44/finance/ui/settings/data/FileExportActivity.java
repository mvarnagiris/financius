package com.code44.finance.ui.settings.data;

import android.content.Context;
import android.content.Intent;

import com.code44.finance.data.backup.DataExporter;
import com.code44.finance.data.backup.DataExporterRunnable;
import com.code44.finance.ui.common.activities.FilePickerActivity;
import com.code44.finance.utils.errors.ExportError;
import com.code44.finance.utils.preferences.GeneralPrefs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.inject.Inject;

public class FileExportActivity extends ExportActivity {
    private static final int REQUEST_LOCAL_DIRECTORY = 1;

    @Inject GeneralPrefs generalPrefs;

    public static void start(Context context, ExportType exportType) {
        makeActivityStarter(context, FileExportActivity.class, exportType).start();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            finish();
            return;
        }

        switch (requestCode) {
            case REQUEST_LOCAL_DIRECTORY:
                final String path = data.getData().getPath();
                generalPrefs.setLastFileExportPath(path);
                generalPrefs.notifyChanged();
                onLocalDirectorySelected(new File(path));
                break;
        }
    }

    @Override protected boolean startProcess() {
        FilePickerActivity.startDir(this, REQUEST_LOCAL_DIRECTORY, generalPrefs.getLastFileExportPath());
        return true;
    }

    private void onLocalDirectorySelected(File directory) {
        final File file = getFile(directory);

        final OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new ExportError("Data export has failed.", e);
        }
        final DataExporter dataExporter = exportType.getDataExporter(outputStream, this);
        exportData(new DataExporterRunnable(getEventBus(), dataExporter));
    }

    private File getFile(File directory) {
        return new File(directory, getFileTitle());
    }
}
