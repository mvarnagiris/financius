package com.code44.finance.api.requests;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.Request;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.utils.EventBus;
import com.google.api.client.json.GenericJson;

public abstract class PostRequest<T extends GenericJson> extends Request {
    private final GcmRegistration gcmRegistration;

    public PostRequest(EventBus eventBus, GcmRegistration gcmRegistration) {
        super(eventBus);

        Preconditions.checkNotNull(gcmRegistration, "GCM registration cannot be null.");

        this.gcmRegistration = gcmRegistration;
    }

    @Override protected void performRequest() throws Exception {
        T body = createBody();
        body.set("deviceRegId", gcmRegistration.getRegistrationId());
        onAddPostData(body);
        if (!isPostDataEmpty(body)) {
            performRequest(body);
        }
    }

    protected abstract T createBody();

    protected abstract void onAddPostData(T body);

    protected abstract boolean isPostDataEmpty(T body);

    protected abstract void performRequest(T body) throws Exception;
}
