package com.code44.finance.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class DataLoader<T> extends AsyncTaskLoader<T> {
    private T data;

    public DataLoader(Context context) {
        super(context);
    }

    @Override public void deliverResult(T data) {
        if (isReset()) {
            this.data = null;
            return;
        }

        this.data = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override protected void onStartLoading() {
        if (data != null) {
            deliverResult(data);
        }

        if (takeContentChanged() || data == null) {
            forceLoad();
        }
    }

    @Override protected void onStopLoading() {
        cancelLoad();
    }

    @Override public void onCanceled(T data) {
        this.data = null;
    }

    @Override protected void onReset() {
        super.onReset();
        onStopLoading();
        data = null;
    }
}
