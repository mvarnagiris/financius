package com.code44.finance.api;

import com.code44.finance.utils.LogUtils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public abstract class Request implements Runnable {
    private static final String TAG = LogUtils.makeLogTag(Request.class);

    @Inject public Bus bus;

    private Exception error;

    @Override public void run() {
        try {
            performRequest();
        } catch (Exception e) {
            LogUtils.e(TAG, "Request failed.", e);
            error = e;
        }

        bus.post(this);
    }

    public Exception getError() {
        return error;
    }

    public boolean isError() {
        return error != null;
    }

    protected abstract void performRequest() throws Exception;
}
