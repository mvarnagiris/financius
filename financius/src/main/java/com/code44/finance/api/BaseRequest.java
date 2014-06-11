package com.code44.finance.api;

import java.util.concurrent.Callable;

import de.greenrobot.event.EventBus;
import retrofit.client.Response;

public abstract class BaseRequest<R, S extends RequestService> implements Callable<R> {
    private final S requestService;

    protected BaseRequest(S requestService) {
        this.requestService = requestService;
    }

    @Override
    public R call() throws Exception {
        final EventBus eventBus = EventBus.getDefault();

        final RequestEvent workingEvent = new RequestEvent<>(this, null, null, null, RequestEvent.State.WORKING);
        eventBus.postSticky(workingEvent);

        Response rawResponse = null;
        R parsedResponse = null;
        Exception error = null;
        try {
            rawResponse = performRequest(requestService);
            parsedResponse = parseResponse(rawResponse);
        } catch (Exception e) {
            error = e;
        }

        eventBus.removeStickyEvent(workingEvent);
        final RequestEvent finishedEvent = new RequestEvent<R>(this, rawResponse, parsedResponse, error, RequestEvent.State.FINISHED);
        eventBus.post(finishedEvent);

        return parsedResponse;
    }

    protected abstract Response performRequest(S requestService) throws Exception;

    protected abstract R parseResponse(Response rawResponse) throws Exception;

    public static class RequestEvent<R> {
        private final BaseRequest request;
        private final Response rawResponse;
        private final R parsedResponse;
        private final Exception error;
        private final State state;

        public RequestEvent(BaseRequest<R, ?> request, Response rawResponse, R parsedResponse, Exception error, State state) {
            this.request = request;
            this.rawResponse = rawResponse;
            this.parsedResponse = parsedResponse;
            this.error = error;
            this.state = state;
        }

        public static enum State {
            WORKING, FINISHED
        }
    }
}
