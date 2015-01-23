package com.code44.finance.api;

public class Result<T> {
    private final T data;
    private final Exception error;

    public Result(T data, Exception error) {
        this.data = data;
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public Exception getError() {
        return error;
    }

    public boolean isSuccess() {
        return error == null;
    }
}
