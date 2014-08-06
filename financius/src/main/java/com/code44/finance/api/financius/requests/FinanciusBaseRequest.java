package com.code44.finance.api.financius.requests;

import android.content.Context;

import com.code44.finance.BuildConfig;
import com.code44.finance.api.BaseRequest;
import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.accounts.Accounts;
import com.code44.finance.backend.endpoint.categories.Categories;
import com.code44.finance.backend.endpoint.currencies.Currencies;
import com.code44.finance.backend.endpoint.transactions.Transactions;
import com.code44.finance.backend.endpoint.users.Users;
import com.code44.finance.common.Constants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
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

    protected HttpTransport getHttpTransport() {
        return AndroidHttp.newCompatibleTransport();
    }

    protected JsonFactory getJsonFactory() {
        return new AndroidJsonFactory();
    }

    protected HttpRequestInitializer getHttpRequestInitializer() {
        final GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context, "server:client_id:" + Constants.ANDROID_AUDIENCE);
        credential.setSelectedAccountName(getAccountName());
        return credential;
    }

    protected String getAccountName() {
        return user.getEmail();
    }

    protected Users getUsersService() {
        final Users.Builder builder = new Users.Builder(getHttpTransport(), getJsonFactory(), getHttpRequestInitializer());
        prepareRootUrl(builder);
        return builder.build();
    }

    protected Currencies getCurrenciesService() {
        final Currencies.Builder builder = new Currencies.Builder(getHttpTransport(), getJsonFactory(), getHttpRequestInitializer());
        prepareRootUrl(builder);
        return builder.build();
    }

    protected Categories getCategoriesService() {
        final Categories.Builder builder = new Categories.Builder(getHttpTransport(), getJsonFactory(), getHttpRequestInitializer());
        prepareRootUrl(builder);
        return builder.build();
    }

    protected Accounts getAccountsService() {
        final Accounts.Builder builder = new Accounts.Builder(getHttpTransport(), getJsonFactory(), getHttpRequestInitializer());
        prepareRootUrl(builder);
        return builder.build();
    }

    protected Transactions getTransactionsService() {
        final Transactions.Builder builder = new Transactions.Builder(getHttpTransport(), getJsonFactory(), getHttpRequestInitializer());
        prepareRootUrl(builder);
        return builder.build();
    }

    private void prepareRootUrl(AbstractGoogleJsonClient.Builder builder) {
        if (BuildConfig.USE_LOCAL_SERVER) {
            builder.setRootUrl("http://" + BuildConfig.LOCAL_SERVER_IP + ":8080/_ah/api");
        }
    }
}
