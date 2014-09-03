package com.code44.finance.data.backup;

public interface DataImporter<T> {
    public void importData(T source) throws Exception;
}
