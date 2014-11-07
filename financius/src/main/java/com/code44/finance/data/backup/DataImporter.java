package com.code44.finance.data.backup;

import com.code44.finance.common.utils.Preconditions;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public abstract class DataImporter implements Closeable {
    private final InputStream inputStream;

    public DataImporter(InputStream inputStream) {
        this.inputStream = Preconditions.notNull(inputStream, "InputStream cannot be null.");
    }

    @Override public void close() throws IOException {
        inputStream.close();
    }

    public void importData() throws Exception {
        importData(inputStream);
    }

    protected abstract void importData(InputStream inputStream) throws Exception;
}
