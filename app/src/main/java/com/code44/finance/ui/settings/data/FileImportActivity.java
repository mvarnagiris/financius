package com.code44.finance.ui.settings.data;

import android.content.Context;
import android.content.Intent;

import com.code44.finance.data.backup.DataImporter;
import com.code44.finance.data.backup.DataImporterRunnable;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.ui.common.activities.FilePickerActivity;
import com.code44.finance.utils.errors.ImportError;
import com.code44.finance.utils.preferences.GeneralPrefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

public class FileImportActivity extends ImportActivity {
    private static final int REQUEST_LOCAL_FILE = 1;

    @Inject GeneralPrefs generalPrefs;
    @Inject DBHelper dbHelper;

    public static void start(Context context, ImportType importType) {
        makeActivityStarter(context, FileImportActivity.class, importType).start();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            finish();
            return;
        }

        switch (requestCode) {
            case REQUEST_LOCAL_FILE:
                final String path = data.getData().getPath();
                onLocalFileSelected(new File(path));
                break;
        }
    }

    @Override protected boolean startProcess() {
        FilePickerActivity.startFile(this, REQUEST_LOCAL_FILE, generalPrefs.getLastFileExportPath());
        return true;
    }

    private void onLocalFileSelected(File file) {
        final InputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ImportError("Data import failed.", e);
        }
        final DataImporter dataImporter = importType.getDataImporter(inputStream, this, dbHelper);
        importData(new DataImporterRunnable(getEventBus(), dataImporter));
    }
}
