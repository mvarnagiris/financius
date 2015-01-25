package com.code44.finance.modules;

import android.content.Context;

import com.code44.finance.api.Api;
import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.User;
import com.code44.finance.common.Constants;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.qualifiers.ApplicationContext;
import com.code44.finance.qualifiers.Network;
import com.code44.finance.services.StartupService;
import com.code44.finance.utils.EventBus;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                StartupService.class
        }
)
public class ApiModule {
    @Provides @Singleton public Api providesApi(@Network ExecutorService executor, @ApplicationContext Context context, EventBus eventBus, DBHelper dbHelper, User user, GcmRegistration gcmRegistration, HttpTransport httpTransport, JsonFactory jsonFactory, Api.HttpRequestInitializerFactory httpRequestInitializerFactory) {
        return new Api(executor, context, eventBus, dbHelper, user, gcmRegistration, httpTransport, jsonFactory, httpRequestInitializerFactory);
    }

    @Provides public HttpTransport provideHttpTransport() {
        return AndroidHttp.newCompatibleTransport();
    }

    @Provides public JsonFactory provideJsonFactory() {
        return new AndroidJsonFactory();
    }

    @Provides public Api.HttpRequestInitializerFactory provideHttpRequestInitializerFactory(User user) {
        return new DefaultHttpRequestInitializerFactory();
    }

    private static class DefaultHttpRequestInitializerFactory implements Api.HttpRequestInitializerFactory {
        @Override public HttpRequestInitializer newHttpRequestInitializer(Context context, User user) {
            final GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context, "server:client_id:" + Constants.ANDROID_AUDIENCE);
            credential.setSelectedAccountName(user.getEmail());
            return credential;
        }
    }
}
