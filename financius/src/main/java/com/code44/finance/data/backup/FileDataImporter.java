package com.code44.finance.data.backup;

import com.code44.finance.common.utils.Preconditions;

import java.io.File;

public abstract class FileDataImporter implements DataImporter {
    private final File file;

    protected FileDataImporter(File file) {
        this.file = Preconditions.checkNotNull(file, "File cannot be null");
        ;
    }

    @Override public void importData() throws Exception {
        importData(file);
    }

    protected abstract void importData(File file) throws Exception;
}
