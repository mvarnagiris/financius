package com.code44.finance.api;

public class Result<T> {
    private final T data;
    private final Throwable error;

    public Result(T data, Throwable error) {
        this.data = data;
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public Throwable getError() {
        return error;
    }

    public boolean isSuccess() {
        return error == null;
    }
}
