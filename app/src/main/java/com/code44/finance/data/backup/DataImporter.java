package com.code44.finance.data.backup;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class DataImporter implements Closeable {
    private final InputStream inputStream;

    public DataImporter(InputStream inputStream) {
        this.inputStream = checkNotNull(inputStream, "InputStream cannot be null.");
    }

    @Override public void close() throws IOException {
        inputStream.close();
    }

    public void importData() throws Exception {
        importData(inputStream);
    }

    protected abstract void importData(InputStream inputStream) throws Exception;
}
