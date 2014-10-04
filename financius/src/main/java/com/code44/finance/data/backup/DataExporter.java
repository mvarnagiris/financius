package com.code44.finance.data.backup;

import com.code44.finance.common.utils.Preconditions;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public abstract class DataExporter implements Closeable {
    private final OutputStream outputStream;

    protected DataExporter(OutputStream outputStream) {
        this.outputStream = Preconditions.checkNotNull(outputStream, "OutputStream cannot be null.");
        ;
    }

    @Override public void close() throws IOException {
        outputStream.close();
    }

    public void exportData() throws Exception {
        exportData(outputStream);
    }

    public abstract void exportData(OutputStream outputStream) throws Exception;
}
