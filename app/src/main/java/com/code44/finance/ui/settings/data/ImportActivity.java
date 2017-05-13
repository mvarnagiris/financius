package com.code44.finance.ui.settings.data;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.code44.finance.R;
import com.code44.finance.data.backup.BackupDataImporter;
import com.code44.finance.data.backup.DataImporter;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.ui.common.activities.ActivityStarter;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.utils.analytics.Screens;
import com.code44.finance.utils.errors.ImportError;
import com.code44.finance.utils.executors.Local;
import com.squareup.otto.Subscribe;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public abstract class ImportActivity extends BaseActivity {
    private static final String EXTRA_IMPORT_TYPE = ImportActivity.class.getName() + ".EXTRA_IMPORT_TYPE";

    private static final String STATE_IS_PROCESS_STARTED = "STATE_IS_PROCESS_STARTED";
    private static final String STATE_IS_FILE_REQUESTED = "STATE_IS_FILE_REQUESTED";

    private final Object importHandler = new Object() {
        @Subscribe public void onDataImporterFinished(DataImporter dataImporter) {
            Toast.makeText(ImportActivity.this, R.string.done, Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    protected ImportType importType;
    protected boolean isProcessStarted = false;
    protected boolean isFileRequested = false;

    @Inject @Local ExecutorService localExecutor;

    protected static ActivityStarter makeActivityStarter(Context context, Class<? extends ImportActivity> activityClass, ImportType importType) {
        return ActivityStarter.begin(context, activityClass).extra(EXTRA_IMPORT_TYPE, importType);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        // Get extras
        importType = (ImportType) getIntent().getSerializableExtra(EXTRA_IMPORT_TYPE);

        // Restore state
        if (savedInstanceState != null) {
            isProcessStarted = savedInstanceState.getBoolean(STATE_IS_PROCESS_STARTED, false);
            isFileRequested = savedInstanceState.getBoolean(STATE_IS_FILE_REQUESTED, false);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        getEventBus().register(importHandler);
        if (!isProcessStarted) {
            isProcessStarted = true;
            isFileRequested = startProcess();
        }
    }

    @Override protected void onPause() {
        super.onPause();
        getEventBus().unregister(importHandler);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_PROCESS_STARTED, isProcessStarted);
        outState.putBoolean(STATE_IS_FILE_REQUESTED, isFileRequested);
    }

    @Override protected void showError(@NonNull Throwable error) {
        super.showError(error);
        if (error instanceof ImportError) {
            finish();
        }
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.Import;
    }

    protected abstract boolean startProcess();

    protected void importData(Runnable importRunnable) {
        localExecutor.execute(importRunnable);
    }

    public enum ImportType {
        Backup {
            @Override public DataImporter getDataImporter(InputStream inputStream, Context context, DBHelper dbHelper) {
                return new BackupDataImporter(inputStream, context, dbHelper, false);
            }
        },

        MergeBackup {
            @Override public DataImporter getDataImporter(InputStream inputStream, Context context, DBHelper dbHelper) {
                return new BackupDataImporter(inputStream, context, dbHelper, true);
            }
        },

        CSV {
            @Override public DataImporter getDataImporter(InputStream inputStream, Context context, DBHelper dbHelper) {
                throw new IllegalStateException("CSV import is not supported.");
            }
        };

        public abstract DataImporter getDataImporter(InputStream inputStream, Context context, DBHelper dbHelper);
    }
}
