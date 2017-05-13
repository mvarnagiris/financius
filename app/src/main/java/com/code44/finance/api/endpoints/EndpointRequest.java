package com.code44.finance.api.endpoints;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.code44.finance.api.Request;
import com.code44.finance.backend.financius.Financius;
import com.code44.finance.utils.EventBus;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class EndpointRequest<T> extends Request<T> {
    protected final EndpointFactory endpointFactory;

    protected EndpointRequest(@Nullable EventBus eventBus, @NonNull EndpointFactory endpointFactory) {
        super(eventBus);
        this.endpointFactory = checkNotNull(endpointFactory, "EndpointFactory cannot be null.");
    }

    protected Financius getEndpoint() {
        return endpointFactory.getEndpoint();
    }
}
