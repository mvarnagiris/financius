package com.code44.finance.ui.settings.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.data.backup.DataExporterRunnable;
import com.code44.finance.data.backup.FileDataImporter;
import com.code44.finance.ui.BaseActivity;
import com.code44.finance.utils.AppError;
import com.squareup.otto.Subscribe;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class ImportActivity extends BaseActivity implements BaseImportFragment.ImportCallbacks {
    private static final String EXTRA_IMPORT_TYPE = "EXTRA_IMPORT_TYPE";
    private static final String EXTRA_SOURCE = "EXTRA_SOURCE";

    private CircularProgressBar loading_CPB;

    public static void start(Context context, ImportType importType, Source source) {
        final Intent intent = makeIntent(context, ImportActivity.class);
        intent.putExtra(EXTRA_IMPORT_TYPE, importType);
        intent.putExtra(EXTRA_SOURCE, source);
        start(context, intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        getEventBus().register(this);

        // Get views
        loading_CPB = (CircularProgressBar) findViewById(R.id.loading_CPB);

        if (savedInstanceState == null) {
            // Get extras
            final ImportType importType = (ImportType) getIntent().getSerializableExtra(EXTRA_IMPORT_TYPE);
            final Source source = (Source) getIntent().getSerializableExtra(EXTRA_SOURCE);

            final BaseImportFragment<?> fragment;
            switch (source) {
                case File:
                    fragment = FileImportFragment.newInstance(importType);
                    break;
//                case GoogleDrive:
//                    break;
                default:
                    throw new IllegalArgumentException("Destination " + source + " is not supported.");
            }

            getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
        }

        setImporting(true);
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

    @Override public void onImportCanceled() {
        finish();
    }

    @Subscribe public void onFileDataImporterFinished(FileDataImporter dataImporter) {
        finish();
    }

    private void setImporting(boolean exporting) {
        if (exporting) {
            loading_CPB.setVisibility(View.VISIBLE);
        } else {
            loading_CPB.setVisibility(View.GONE);
        }
    }

    public static enum ImportType {
        Backup, CSV
    }

    public static enum Source {
        File, GoogleDrive
    }
}
