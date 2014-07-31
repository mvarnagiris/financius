package com.code44.finance.api.financius.requests;

import android.content.Context;

import com.code44.finance.api.User;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

public class RegisterRequest extends FinanciusBaseRequest<User> {
    public RegisterRequest(Context context, User user) {
        super(null, context, user);
    }

    @Override
    protected User performRequest(HttpTransport httpTransport, JsonFactory jsonFactory, HttpRequestInitializer httpRequestInitializer) throws Exception {
//        final RegisterBody body = new RegisterBody()
//        Users.Register new Users.Builder(httpTransport, jsonFactory, httpRequestInitializer).build().register(new ).set;
        return null;
    }
}
