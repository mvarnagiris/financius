package com.code44.finance.api.requests;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.Request;
import com.google.api.client.json.GenericJson;

import javax.inject.Inject;

public abstract class PostRequest<T extends GenericJson> extends Request {
    @Inject GcmRegistration gcmRegistration;

    @Override protected void performRequest() throws Exception {
        T body = createBody();
        body.set("deviceRegId", gcmRegistration.getRegistrationId());
        onAddPostData(body);
        performRequest(body);
    }

    protected abstract T createBody();

    protected abstract void onAddPostData(T body);

    protected abstract void performRequest(T body) throws Exception;
}
