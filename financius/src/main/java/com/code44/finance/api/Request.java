package com.code44.finance.api;

import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.Logger;

public abstract class Request implements Runnable {
    protected final EventBus eventBus;
    private final Logger logger = Logger.with(Request.class.getSimpleName());
    protected Exception error;

    /**
     * @param eventBus If {@code null}, then event will not be posted.
     */
    protected Request(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override public void run() {
        try {
            performRequest();
        } catch (Exception e) {
            logger.error("Request failed.", e);
            error = e;
        }

        if (eventBus != null) {
            eventBus.post(this);
        }
    }

    public Exception getError() {
        return error;
    }

    public boolean isError() {
        return error != null;
    }

    protected abstract void performRequest() throws Exception;
}
