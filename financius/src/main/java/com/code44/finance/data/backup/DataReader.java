package com.code44.finance.data.backup;

import java.io.IOException;
import java.io.InputStream;

public interface DataReader<T> {
    public T readData(InputStream inputStream) throws IOException;
}
