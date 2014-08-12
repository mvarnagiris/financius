package com.code44.finance.api;

import android.content.Context;

import com.code44.finance.App;
import com.code44.finance.BuildConfig;
import com.code44.finance.api.requests.RegisterDeviceRequest;
import com.code44.finance.api.requests.RegisterRequest;
import com.code44.finance.api.requests.SyncRequest;
import com.code44.finance.backend.endpoint.accounts.Accounts;
import com.code44.finance.backend.endpoint.categories.Categories;
import com.code44.finance.backend.endpoint.currencies.Currencies;
import com.code44.finance.backend.endpoint.transactions.Transactions;
import com.code44.finance.backend.endpoint.users.Users;
import com.code44.finance.common.Constants;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.utils.EventBus;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

public final class Api {
    private static Api singleton;

    private final NetworkExecutor executor;
    private final Context context;
    private final EventBus eventBus;
    private final DBHelper dbHelper;
    private final User user;
    private final GcmRegistration gcmRegistration;
    private final HttpTransport httpTransport;
    private final JsonFactory jsonFactory;
    private final HttpRequestInitializerFactory httpRequestInitializerFactory;

    public Api(NetworkExecutor executor, Context context, EventBus eventBus, DBHelper dbHelper, User user, GcmRegistration gcmRegistration, HttpTransport httpTransport, JsonFactory jsonFactory, HttpRequestInitializerFactory httpRequestInitializerFactory) {
        Preconditions.checkNotNull(executor, "Executor cannot be null.");
        Preconditions.checkNotNull(context, "Context cannot be null.");
        Preconditions.checkNotNull(eventBus, "EventBus cannot be null.");
        Preconditions.checkNotNull(dbHelper, "DBHelper cannot be null.");
        Preconditions.checkNotNull(user, "User cannot be null.");
        Preconditions.checkNotNull(gcmRegistration, "GCM registration cannot be null.");
        Preconditions.checkNotNull(httpTransport, "Http transport cannot be null.");
        Preconditions.checkNotNull(jsonFactory, "Json factory cannot be null.");
        Preconditions.checkNotNull(httpRequestInitializerFactory, "Http request initializer factory cannot be null.");

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

    public static synchronized Api get() {
        if (singleton == null) {
            final NetworkExecutor executor = DefaultNetworkExecutor.get();
            final Context context = App.getContext();
            final EventBus eventBus = EventBus.get();
            final DBHelper dbHelper = DBHelper.get();
            final User user = User.get();
            final GcmRegistration gcmRegistration = GcmRegistration.get();
            final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            final JsonFactory jsonFactory = new AndroidJsonFactory();
            final HttpRequestInitializerFactory httpRequestInitializerFactory = new DefaultHttpRequestInitializerFactory();
            singleton = new Api(executor, context, eventBus, dbHelper, user, gcmRegistration, httpTransport, jsonFactory, httpRequestInitializerFactory);
        }
        return singleton;
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
        final SyncRequest request = new SyncRequest(eventBus, dbHelper, user, gcmRegistration, getCurrenciesService(), getCategoriesService(), getAccountsService(), getTransactionsService());
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

    private Transactions getTransactionsService() {
        final Transactions.Builder builder = new Transactions.Builder(httpTransport, jsonFactory, getHttpRequestInitializer());
        prepareRootUrl(builder);
        return builder.build();
    }

    private HttpRequestInitializer getHttpRequestInitializer() {
        final GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context, "server:client_id:" + Constants.ANDROID_AUDIENCE);
        credential.setSelectedAccountName(user.getEmail());
        return credential;
    }

    private void prepareRootUrl(AbstractGoogleJsonClient.Builder builder) {
        if (BuildConfig.USE_LOCAL_SERVER) {
            builder.setRootUrl("http://" + BuildConfig.LOCAL_SERVER_IP + ":8080/_ah/api");
        }
    }

    public interface HttpRequestInitializerFactory {
        public HttpRequestInitializer newHttpRequestInitializer(Context context, User user);
    }

    private static class DefaultHttpRequestInitializerFactory implements HttpRequestInitializerFactory {
        @Override public HttpRequestInitializer newHttpRequestInitializer(Context context, User user) {
            final GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context, "server:client_id:" + Constants.ANDROID_AUDIENCE);
            credential.setSelectedAccountName(user.getEmail());
            return credential;
        }
    }
}
