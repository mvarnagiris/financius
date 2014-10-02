package com.code44.finance.ui.settings.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.data.backup.DataExporterRunnable;
import com.code44.finance.data.backup.FileDataImporter;
import com.code44.finance.qualifiers.Local;
import com.code44.finance.ui.BaseActivity;
import com.code44.finance.utils.AppError;
import com.squareup.otto.Subscribe;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public abstract class BaseImportActivity extends BaseActivity {
    private static final String EXTRA_IMPORT_TYPE = "EXTRA_IMPORT_TYPE";
    private static final String EXTRA_SOURCE = "EXTRA_SOURCE";

    @Inject @Local Executor localExecutor;

    private CircularProgressBar loading_CPB;

    public static Intent makeIntent(Context context, ImportType importType, Source source) {
        final Intent intent = makeIntent(context, BaseImportActivity.class);
        intent.putExtra(EXTRA_IMPORT_TYPE, importType);
        intent.putExtra(EXTRA_SOURCE, source);
        return intent;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        // Get views
        loading_CPB = (CircularProgressBar) findViewById(R.id.loading_CPB);

        // Setup
        setImporting(true);
        getEventBus().register(this);
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

    protected void setImporting(boolean exporting) {
        if (exporting) {
            loading_CPB.setVisibility(View.VISIBLE);
        } else {
            loading_CPB.setVisibility(View.GONE);
        }
    }

    public static enum ImportType {
        Backup, MergeBackup, CSV
    }

    public static enum Source {
        File, GoogleDrive
    }
}
