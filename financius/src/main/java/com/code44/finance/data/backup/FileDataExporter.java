package com.code44.finance.data.backup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public abstract class FileDataExporter implements DataExporter {
    private final File file;

    protected FileDataExporter(File file) {
        this.file = file;
    }

    @Override public void exportData() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();
        exportData(new FileOutputStream(file));
    }

    protected abstract void exportData(OutputStream outputStream) throws Exception;
}
