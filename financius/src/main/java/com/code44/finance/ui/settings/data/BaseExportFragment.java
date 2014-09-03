package com.code44.finance.ui.settings.data;

import android.os.Bundle;

import com.code44.finance.data.backup.DataExporter;
import com.code44.finance.data.backup.DataExporterRunnable;
import com.code44.finance.ui.BaseFragment;
import com.code44.finance.utils.LocalExecutor;

public abstract class BaseExportFragment extends BaseFragment {
    protected final LocalExecutor localExecutor = LocalExecutor.get();

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    protected void exportData(DataExporter dataExporter) {
        localExecutor.execute(new DataExporterRunnable(eventBus, dataExporter));
    }

    protected void cancel() {

    }
}
