package com.code44.finance.data.backup;

import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.IOUtils;
import com.code44.finance.utils.errors.ImportError;
import com.crashlytics.android.Crashlytics;

public class DataImporterRunnable implements Runnable {
    private final EventBus eventBus;
    private final DataImporter dataImporter;

    public DataImporterRunnable(EventBus eventBus, DataImporter dataImporter) {
        this.eventBus = eventBus;
        this.dataImporter = dataImporter;
    }

    @Override public void run() {
        try {
            dataImporter.importData();
            eventBus.post(dataImporter);
        } catch (Exception e) {
            e.printStackTrace();
            final ImportError error = new ImportError("Data import has failed. " + e.getMessage(), e);
            Crashlytics.logException(error);
            eventBus.post(error);
        } finally {
            IOUtils.closeQuietly(dataImporter);
        }
    }
}
