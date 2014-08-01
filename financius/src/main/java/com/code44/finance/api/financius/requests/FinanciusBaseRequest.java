package com.code44.finance.api.financius.requests;

import android.content.Context;

import com.code44.finance.api.BaseRequest;
import com.code44.finance.api.User;
import com.code44.finance.common.Constants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

public abstract class FinanciusBaseRequest<R> extends BaseRequest<R> {
    protected final Context context;
    protected final User user;

    public FinanciusBaseRequest(String uniqueId, Context context, User user) {
        super(uniqueId);
        this.context = context;
        this.user = user;
    }

    protected abstract R performRequest(HttpTransport httpTransport, JsonFactory jsonFactory, HttpRequestInitializer httpRequestInitializer) throws Exception;

    @Override
    protected R performRequest() throws Exception {
        return performRequest(getHttpTransport(), getJsonFactory(), getHttpRequestInitializer());
    }

    protected HttpTransport getHttpTransport() {
        return AndroidHttp.newCompatibleTransport();
    }

    protected JsonFactory getJsonFactory() {
        return new AndroidJsonFactory();
    }

    protected HttpRequestInitializer getHttpRequestInitializer() {
        final GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context, Constants.ANDROID_AUDIENCE);
        credential.setSelectedAccountName(getAccountName());
        return credential;
    }

    protected String getAccountName() {
        return user.getEmail();
    }
}
