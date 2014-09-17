package com.code44.finance.ui.settings.data;

import android.app.Activity;
import android.os.Bundle;

import com.code44.finance.data.backup.DataImporter;
import com.code44.finance.data.backup.DataImporterRunnable;
import com.code44.finance.qualifiers.Local;
import com.code44.finance.ui.BaseFragment;

import java.util.concurrent.Executor;

import javax.inject.Inject;

public abstract class BaseImportFragment<T> extends BaseFragment {
    @Inject @Local Executor localExecutor;

    private ImportCallbacks importCallbacks;

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ImportCallbacks) {
            importCallbacks = (ImportCallbacks) activity;
        } else {
            throw new IllegalArgumentException("Activity " + activity.getClass().getName() + " must implement " + ImportCallbacks.class.getName());
        }
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    protected void importData(DataImporter<T> dataImporter, T source) {
        localExecutor.execute(new DataImporterRunnable<>(getEventBus(), dataImporter, source));
    }

    protected void cancel() {
        importCallbacks.onImportCanceled();
    }

    public static interface ImportCallbacks {
        public void onImportCanceled();
    }
}
