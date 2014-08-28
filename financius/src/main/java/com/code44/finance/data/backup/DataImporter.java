package com.code44.finance.data.backup;

public interface DataImporter<T> {
    public void readData(T source) throws Exception;
}
