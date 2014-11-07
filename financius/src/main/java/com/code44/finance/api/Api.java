package com.code44.finance.api;

import android.content.Context;

import com.code44.finance.BuildConfig;
import com.code44.finance.api.requests.RegisterDeviceRequest;
import com.code44.finance.api.requests.RegisterRequest;
import com.code44.finance.api.requests.SyncRequest;
import com.code44.finance.backend.endpoint.accounts.Accounts;
import com.code44.finance.backend.endpoint.categories.Categories;
import com.code44.finance.backend.endpoint.currencies.Currencies;
import com.code44.finance.backend.endpoint.tags.Tags;
import com.code44.finance.backend.endpoint.transactions.Transactions;
import com.code44.finance.backend.endpoint.users.Users;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.utils.EventBus;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

import java.util.concurrent.Executor;

public final class Api {
    private final Executor executor;
    private final Context context;
    private final EventBus eventBus;
    private final DBHelper dbHelper;
    private final User user;
    private final GcmRegistration gcmRegistration;
    private final HttpTransport httpTransport;
    private final JsonFactory jsonFactory;
    private final HttpRequestInitializerFactory httpRequestInitializerFactory;

    public Api(Executor executor, Context context, EventBus eventBus, DBHelper dbHelper, User user, GcmRegistration gcmRegistration, HttpTransport httpTransport, JsonFactory jsonFactory, HttpRequestInitializerFactory httpRequestInitializerFactory) {
        Preconditions.notNull(executor, "Executor cannot be null.");
        Preconditions.notNull(context, "Context cannot be null.");
        Preconditions.notNull(eventBus, "EventBus cannot be null.");
        Preconditions.notNull(dbHelper, "DBHelper cannot be null.");
        Preconditions.notNull(user, "User cannot be null.");
        Preconditions.notNull(gcmRegistration, "GCM registration cannot be null.");
        Preconditions.notNull(httpTransport, "Http transport cannot be null.");
        Preconditions.notNull(jsonFactory, "Json factory cannot be null.");
        Preconditions.notNull(httpRequestInitializerFactory, "Http request initializer factory cannot be null.");

        this.executor = executor;
        this.context = context;
        this.eventBus = eventBus;
        this.dbHelper = dbHelper;
        this.user = user;
        this.gcmRegistration = gcmRegistration;
        this.httpTransport = httpTransport;
        this.jsonFactory = jsonFactory;
        this.httpRequestInitializerFactory = httpRequestInitializerFactory;
    }

    public void register(String email, String googleId, String firstName, String lastName, String photoUrl, String coverUrl) {
        final RegisterRequest request = new RegisterRequest(eventBus, context, getUsersService(), user, dbHelper, email, googleId, firstName, lastName, photoUrl, coverUrl);
        execute(request);
    }

    public void registerDevice() {
        final RegisterDeviceRequest request = new RegisterDeviceRequest(context, getUsersService(), gcmRegistration);
        execute(request);
    }

    public void sync() {
        if (!user.isPremium()) {
            return;
        }

        final SyncRequest request = new SyncRequest(eventBus, context, dbHelper, user, gcmRegistration, getCurrenciesService(), getCategoriesService(), getTagsService(), getAccountsService(), getTransactionsService());
        execute(request);
    }

    private void execute(Request request) {
        executor.execute(request);
    }

    private Users getUsersService() {
        final Users.Builder builder = new Users.Builder(httpTransport, jsonFactory, getHttpRequestInitializer());
        prepareRootUrl(builder);
        return builder.build();
    }

    private Currencies getCurrenciesService() {
        final Currencies.Builder builder = new Currencies.Builder(httpTransport, jsonFactory, getHttpRequestInitializer());
        prepareRootUrl(builder);
        return builder.build();
    }

    private Accounts getAccountsService() {
        final Accounts.Builder builder = new Accounts.Builder(httpTransport, jsonFactory, getHttpRequestInitializer());
        prepareRootUrl(builder);
        return builder.build();
    }

    private Categories getCategoriesService() {
        final Categories.Builder builder = new Categories.Builder(httpTransport, jsonFactory, getHttpRequestInitializer());
        prepareRootUrl(builder);
        return builder.build();
    }

    private Tags getTagsService() {
        final Tags.Builder builder = new Tags.Builder(httpTransport, jsonFactory, getHttpRequestInitializer());
        prepareRootUrl(builder);
        return builder.build();
    }

    private Transactions getTransactionsService() {
        final Transactions.Builder builder = new Transactions.Builder(httpTransport, jsonFactory, getHttpRequestInitializer());
        prepareRootUrl(builder);
        return builder.build();
    }

    private HttpRequestInitializer getHttpRequestInitializer() {
        return httpRequestInitializerFactory.newHttpRequestInitializer(context, user);
    }

    private void prepareRootUrl(AbstractGoogleJsonClient.Builder builder) {
        if (BuildConfig.USE_LOCAL_SERVER) {
            builder.setRootUrl("http://" + BuildConfig.LOCAL_SERVER_IP + ":8080/_ah/api");
        }
    }

    public interface HttpRequestInitializerFactory {
        public HttpRequestInitializer newHttpRequestInitializer(Context context, User user);
    }
}
