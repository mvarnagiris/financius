package com.code44.finance.api;

import android.support.annotation.Nullable;

import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.Logger;
import com.crashlytics.android.Crashlytics;

import java.util.concurrent.Callable;

public abstract class Request<T> implements Callable<Result<T>> {
    protected final EventBus eventBus;

    private final Logger logger = Logger.with(Request.class.getSimpleName());

    private Result<T> result;

    /**
     * @param eventBus If {@code null}, then event will not be posted.
     */
    protected Request(@Nullable EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override public Result<T> call() throws Exception {
        try {
            final T data = performRequest();
            result = new Result<>(data, null);
        } catch (Exception error) {
            Crashlytics.logException(error);
            logger.error("Request failed.", error);
            result = new Result<>(null, error);
        }

        if (eventBus != null) {
            eventBus.post(this);
        }

        return result;
    }

    public Result<T> getResult() {
        return result;
    }

    public boolean isFinished() {
        return result != null;
    }

    public boolean isSuccess() {
        return isFinished() && result.isSuccess();
    }

    protected abstract T performRequest() throws Exception;
}
