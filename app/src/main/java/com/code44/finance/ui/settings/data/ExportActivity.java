package com.code44.finance.ui.settings.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.code44.finance.R;
import com.code44.finance.data.backup.BackupDataExporter;
import com.code44.finance.data.backup.CsvDataExporter;
import com.code44.finance.data.backup.DataExporter;
import com.code44.finance.ui.common.activities.ActivityStarter;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.utils.analytics.Screens;
import com.code44.finance.utils.errors.ExportError;
import com.code44.finance.utils.executors.Local;
import com.squareup.otto.Subscribe;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

abstract class ExportActivity extends BaseActivity {
    private static final String EXTRA_EXPORT_TYPE = ExportActivity.class.getName() + ".EXTRA_EXPORT_TYPE";

    private static final String STATE_IS_PROCESS_STARTED = "STATE_IS_PROCESS_STARTED";
    private static final String STATE_IS_DIRECTORY_REQUESTED = "STATE_IS_DIRECTORY_REQUESTED";

    private final Object exportHandler = new Object() {
        @Subscribe public void onDataExporterFinished(DataExporter dataExporter) {
            Toast.makeText(ExportActivity.this, R.string.done, Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    protected ExportType exportType;
    protected boolean isProcessStarted = false;
    protected boolean isDirectoryRequested = false;

    @Inject @Local ExecutorService localExecutor;

    protected static ActivityStarter makeActivityStarter(Context context, Class<? extends ExportActivity> activityClass, ExportType exportType) {
        return ActivityStarter.begin(context, activityClass).extra(EXTRA_EXPORT_TYPE, exportType);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        // Get extras
        exportType = (ExportType) getIntent().getSerializableExtra(EXTRA_EXPORT_TYPE);

        // Restore state
        if (savedInstanceState != null) {
            isProcessStarted = savedInstanceState.getBoolean(STATE_IS_PROCESS_STARTED, false);
            isDirectoryRequested = savedInstanceState.getBoolean(STATE_IS_DIRECTORY_REQUESTED, false);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        getEventBus().register(exportHandler);
        if (!isProcessStarted) {
            isProcessStarted = true;
            isDirectoryRequested = startProcess();
        }
    }

    @Override protected void onPause() {
        super.onPause();
        getEventBus().unregister(exportHandler);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_PROCESS_STARTED, isProcessStarted);
        outState.putBoolean(STATE_IS_DIRECTORY_REQUESTED, isDirectoryRequested);
    }

    @Override protected void showError(@NonNull Throwable error) {
        super.showError(error);
        if (error instanceof ExportError) {
            finish();
        }
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.Export;
    }

    protected abstract boolean startProcess();

    protected String getFileTitle() {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        return getString(R.string.app_name) + " " + dateFormat.format(new Date()) + exportType.getExtension();
    }

    protected void exportData(Runnable exportRunnable) {
        localExecutor.execute(exportRunnable);
    }

    public enum ExportType {
        Backup {
            @Override public DataExporter getDataExporter(OutputStream outputStream, Context context) {
                return new BackupDataExporter(outputStream, context);
            }

            @Override public String getExtension() {
                return ".json";
            }

            @Override public String getMimeType() {
                return "application/json";
            }
        },

        CSV {
            @Override public DataExporter getDataExporter(OutputStream outputStream, Context context) {
                return new CsvDataExporter(outputStream, context);
            }

            @Override public String getExtension() {
                return ".csv";
            }

            @Override public String getMimeType() {
                return "text/csv";
            }
        };

        public abstract DataExporter getDataExporter(OutputStream outputStream, Context context);

        public abstract String getExtension();

        public abstract String getMimeType();
    }
}
