package com.code44.finance.ui.settings.data;

import android.os.Bundle;

import com.code44.finance.data.backup.DataExporter;
import com.code44.finance.data.backup.DataExporterRunnable;
import com.code44.finance.qualifiers.Local;
import com.code44.finance.ui.BaseFragment;

import java.util.concurrent.Executor;

import javax.inject.Inject;

public abstract class BaseExportFragment extends BaseFragment {
    @Inject @Local Executor localExecutor;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    protected void exportData(DataExporter dataExporter) {
        localExecutor.execute(new DataExporterRunnable(getEventBus(), dataExporter));
    }

    protected void cancel() {

    }
}
