package com.code44.finance.data.backup;

import com.code44.finance.utils.AppError;
import com.code44.finance.utils.EventBus;

public class DataImporterRunnable<T> implements Runnable {
    private final EventBus eventBus;
    private final DataImporter<T> dataImporter;
    private final T source;

    public DataImporterRunnable(EventBus eventBus, DataImporter<T> dataImporter, T source) {
        this.eventBus = eventBus;
        this.dataImporter = dataImporter;
        this.source = source;
    }

    @Override public void run() {
        try {
            dataImporter.importData(source);
            eventBus.post(dataImporter);
        } catch (Exception e) {
            e.printStackTrace();
            eventBus.post(new DataImportError(e));
        }
    }

    public static class DataImportError extends AppError {
        public DataImportError(Throwable throwable) {
            super(throwable);
        }
    }
}
