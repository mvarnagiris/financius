package com.code44.finance.ui.settings.data;

import android.os.Bundle;

import com.code44.finance.data.backup.DataExporter;
import com.code44.finance.data.backup.DataExporterRunnable;
import com.code44.finance.ui.BaseFragment;
import com.code44.finance.utils.LocalExecutor;

import javax.inject.Inject;

public abstract class BaseExportFragment extends BaseFragment {
    @Inject LocalExecutor localExecutor;

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
