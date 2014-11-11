package com.code44.finance.data.backup;

import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.errors.ExportError;
import com.crashlytics.android.Crashlytics;

public class DataExporterRunnable implements Runnable {
    private final EventBus eventBus;
    private final DataExporter dataExporter;

    public DataExporterRunnable(EventBus eventBus, DataExporter dataExporter) {
        this.eventBus = eventBus;
        this.dataExporter = dataExporter;
    }

    @Override public void run() {
        try {
            dataExporter.exportData();
            eventBus.post(dataExporter);
        } catch (Exception e) {
            e.printStackTrace();
            final ExportError error = new ExportError("Data export has failed. " + e.getMessage(), e);
            Crashlytics.logException(error);
            eventBus.post(error);
        }
    }
}
