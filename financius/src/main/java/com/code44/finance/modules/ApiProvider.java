package com.code44.finance.modules;

import android.content.Context;

import com.code44.finance.BuildConfig;
import com.code44.finance.api.Api;
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

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                Api.class,
                Users.class,
                Currencies.class,
                Accounts.class,
                Categories.class,
                Transactions.class
        },
        library = true,
        complete = false
)
public class ApiProvider {
    @Provides public Users provideUsersService(HttpTransport httpTransport, JsonFactory jsonFactory, HttpRequestInitializer httpRequestInitializer) {
        final Users.Builder builder = new Users.Builder(httpTransport, jsonFactory, httpRequestInitializer);
        prepareRootUrl(builder);
        return builder.build();
    }

    @Provides public Currencies provideCurrenciesService(HttpTransport httpTransport, JsonFactory jsonFactory, HttpRequestInitializer httpRequestInitializer) {
        final Currencies.Builder builder = new Currencies.Builder(httpTransport, jsonFactory, httpRequestInitializer);
        prepareRootUrl(builder);
        return builder.build();
    }

    @Provides public Accounts provideAccountsService(HttpTransport httpTransport, JsonFactory jsonFactory, HttpRequestInitializer httpRequestInitializer) {
        final Accounts.Builder builder = new Accounts.Builder(httpTransport, jsonFactory, httpRequestInitializer);
        prepareRootUrl(builder);
        return builder.build();
    }

    @Provides public Categories provideCategoriesService(HttpTransport httpTransport, JsonFactory jsonFactory, HttpRequestInitializer httpRequestInitializer) {
        final Categories.Builder builder = new Categories.Builder(httpTransport, jsonFactory, httpRequestInitializer);
        prepareRootUrl(builder);
        return builder.build();
    }

    @Provides public Transactions provideTransactionsService(HttpTransport httpTransport, JsonFactory jsonFactory, HttpRequestInitializer httpRequestInitializer) {
        final Transactions.Builder builder = new Transactions.Builder(httpTransport, jsonFactory, httpRequestInitializer);
        prepareRootUrl(builder);
        return builder.build();
    }

    @Provides public HttpTransport getHttpTransport() {
        return AndroidHttp.newCompatibleTransport();
    }

    @Provides public JsonFactory getJsonFactory() {
        return new AndroidJsonFactory();
    }

    @Provides public HttpRequestInitializer getHttpRequestInitializer(Context context, User user) {
        final GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context, "server:client_id:" + Constants.ANDROID_AUDIENCE);
        credential.setSelectedAccountName(user.getEmail());
        return credential;
    }

    private void prepareRootUrl(AbstractGoogleJsonClient.Builder builder) {
        if (BuildConfig.USE_LOCAL_SERVER) {
            builder.setRootUrl("http://" + BuildConfig.LOCAL_SERVER_IP + ":8080/_ah/api");
        }
    }
}
