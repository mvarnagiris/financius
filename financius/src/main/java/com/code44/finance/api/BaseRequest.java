package com.code44.finance.api;

import java.util.concurrent.Callable;

import de.greenrobot.event.EventBus;

public abstract class BaseRequest<R> implements Callable<R> {
    private final String uniqueId;

    protected BaseRequest(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public R call() throws Exception {
        final EventBus eventBus = EventBus.getDefault();

        final BaseRequestEvent<R, ? extends BaseRequest<R>> workingEvent = createEvent(null, null, BaseRequestEvent.State.WORKING);
        if (workingEvent != null) {
            eventBus.postSticky(workingEvent);
        }

        R result = null;
        Exception error = null;
        try {
            result = performRequest();
        } catch (Exception e) {
            e.printStackTrace();
            error = e;
        }

        if (workingEvent != null) {
            eventBus.removeStickyEvent(workingEvent);
            final BaseRequestEvent<R, ? extends BaseRequest<R>> finishedEvent = createEvent(result, error, BaseRequestEvent.State.FINISHED);
            eventBus.post(finishedEvent);
        }

        return result;
    }

    protected abstract R performRequest() throws Exception;

    public String getUniqueId() {
        return null;
    }

    protected BaseRequestEvent<R, ? extends BaseRequest<R>> createEvent(R result, Exception error, BaseRequestEvent.State state) {
        return null;
    }
}
