package com.code44.finance.utils.errors;

public class AppError extends RuntimeException {
    public AppError() {
    }

    public AppError(String detailMessage) {
        super(detailMessage);
    }

    public AppError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AppError(Throwable throwable) {
        super(throwable);
    }
}
