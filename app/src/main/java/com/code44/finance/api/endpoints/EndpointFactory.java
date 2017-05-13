package com.code44.finance.api.endpoints;

import android.content.Context;

import com.code44.finance.ApplicationContext;
import com.code44.finance.BuildConfig;
import com.code44.finance.R;
import com.code44.finance.backend.financius.Financius;
import com.code44.finance.common.Constants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class EndpointFactory {
    private final Context context;
    private final User user;

    @Inject public EndpointFactory(@ApplicationContext Context context, User user) {
        this.context = checkNotNull(context, "Context cannot be null.");
        this.user = checkNotNull(user, "User cannot be null.");
    }

    public Financius getEndpoint() {
        final Financius.Builder builder = new Financius.Builder(getHttpTransport(), getJsonFactory(), getGoogleAccountCredential());
        prepare(builder);
        return builder.build();
    }

    public GoogleAccountCredential getGoogleAccountCredential() {
        final GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context, "server:client_id:" + Constants.WEB_CLIENT_ID);
        credential.setSelectedAccountName(user.getEmail());
        return credential;
    }

    private HttpTransport getHttpTransport() {
        return AndroidHttp.newCompatibleTransport();
    }

    private JsonFactory getJsonFactory() {
        return new AndroidJsonFactory();
    }

    private void prepare(AbstractGoogleJsonClient.Builder builder) {
        builder.setApplicationName(context.getString(R.string.app_name));
        if (BuildConfig.USE_LOCAL_SERVER) {
            builder.setRootUrl("http://" + BuildConfig.LOCAL_SERVER_IP + ":8080/_ah/api");
        }
    }
}
