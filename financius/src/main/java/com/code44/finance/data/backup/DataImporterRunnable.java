package com.code44.finance.data.backup;

import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.IOUtils;
import com.code44.finance.utils.errors.ImportError;

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
            eventBus.post(new ImportError("Data import has failed.", e));
        } finally {
            IOUtils.closeQuietly(dataImporter);
        }
    }
}
