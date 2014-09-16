package com.code44.finance.ui.settings.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.data.backup.DataExporterRunnable;
import com.code44.finance.data.backup.FileDataExporter;
import com.code44.finance.ui.BaseActivity;
import com.code44.finance.utils.AppError;
import com.squareup.otto.Subscribe;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class ExportActivity extends BaseActivity {
    private static final String EXTRA_EXPORT_TYPE = "EXTRA_EXPORT_TYPE";
    private static final String EXTRA_DESTINATION = "EXTRA_DESTINATION";

    private CircularProgressBar loading_CPB;

    public static void start(Context context, ExportType exportType, Destination destination) {
        final Intent intent = makeIntent(context, ExportActivity.class);
        intent.putExtra(EXTRA_EXPORT_TYPE, exportType);
        intent.putExtra(EXTRA_DESTINATION, destination);
        start(context, intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        getEventBus().register(this);

        // Get views
        loading_CPB = (CircularProgressBar) findViewById(R.id.loading_CPB);

        if (savedInstanceState == null) {
            // Get extras
            final ExportType exportType = (ExportType) getIntent().getSerializableExtra(EXTRA_EXPORT_TYPE);
            final Destination destination = (Destination) getIntent().getSerializableExtra(EXTRA_DESTINATION);

            final BaseExportFragment fragment;
            switch (destination) {
                case File:
                    fragment = FileExportFragment.newInstance(exportType);
                    break;
//                case GoogleDrive:
//                    break;
                default:
                    throw new IllegalArgumentException("Destination " + destination + " is not supported.");
            }

            getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
        }

        setExporting(true);
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

    private void setExporting(boolean exporting) {
        if (exporting) {
            loading_CPB.setVisibility(View.VISIBLE);
        } else {
            loading_CPB.setVisibility(View.GONE);
        }
    }

    public static enum ExportType {
        Backup, CSV
    }

    public static enum Destination {
        File, GoogleDrive
    }
}
