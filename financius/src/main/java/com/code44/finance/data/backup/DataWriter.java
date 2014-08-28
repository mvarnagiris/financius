package com.code44.finance.data.backup;

import java.io.IOException;
import java.io.OutputStream;

public interface DataWriter {
    public void writeData(OutputStream outputStream) throws IOException;
}
