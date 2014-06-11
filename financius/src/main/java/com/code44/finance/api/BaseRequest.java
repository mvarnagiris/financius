package com.code44.finance.api;

import java.util.concurrent.Callable;

import de.greenrobot.event.EventBus;
import retrofit.client.Response;

public abstract class BaseRequest<R, S> implements Callable<R> {
    private final S requestService;

    protected BaseRequest(S requestService) {
        this.requestService = requestService;
    }

    @Override
    public R call() throws Exception {
        final EventBus eventBus = EventBus.getDefault();

        final BaseRequestEvent<R, ? extends BaseRequest<R, S>> workingEvent = createEvent(null, null, null, BaseRequestEvent.State.WORKING);
        if (workingEvent != null) {
            eventBus.postSticky(workingEvent);
        }

        Response rawResponse = null;
        R parsedResponse = null;
        Exception error = null;
        try {
            rawResponse = performRequest(requestService);
            parsedResponse = parseResponse(rawResponse);
        } catch (Exception e) {
            error = e;
        }

        if (workingEvent != null) {
            eventBus.removeStickyEvent(workingEvent);
            final BaseRequestEvent<R, ? extends BaseRequest<R, S>> finishedEvent = createEvent(rawResponse, parsedResponse, error, BaseRequestEvent.State.FINISHED);
            eventBus.post(finishedEvent);
        }

        return parsedResponse;
    }

    protected abstract Response performRequest(S requestService) throws Exception;

    protected abstract R parseResponse(Response rawResponse) throws Exception;

    protected String getUniqueId() {
        return null;
    }

    protected BaseRequestEvent<R, ? extends BaseRequest<R, S>> createEvent(Response rawResponse, R parsedResponse, Exception error, BaseRequestEvent.State state) {
        return null;
    }
}
