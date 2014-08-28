package com.code44.finance.data.backup;

public class DataExporterRunnable implements Runnable {
    private final DataExporter dataExporter;

    public DataExporterRunnable(DataExporter dataExporter) {
        this.dataExporter = dataExporter;
    }

    @Override public void run() {
        try {
            dataExporter.exportData();
        } catch (Exception e) {
            // TODO Handle error
            e.printStackTrace();
        }
    }
}
