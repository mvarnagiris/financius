package com.code44.finance.ui.user;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.code44.finance.R;
import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.EndpointsApi;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.api.endpoints.requests.users.RegisterRequest;
import com.code44.finance.ui.common.activities.ActivityStarter;
import com.code44.finance.ui.common.activities.DrawerActivity;
import com.code44.finance.ui.common.navigation.NavigationScreen;
import com.code44.finance.utils.analytics.Screens;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public abstract class BaseLoginActivity extends DrawerActivity {
    private static final int REQUEST_ACCOUNT = 1;

    private static final String STATE_IS_LOGIN_STARTED = "STATE_IS_LOGIN_STARTED";

    private final Object registrationHandler = new Object() {
        @Subscribe public void onRegisterCompleted(RegisterRequest request) {
            if (!request.getResult().isSuccess()) {
                showError(request.getResult().getError());
                finish();
                return;
            }

            ProfileActivity.start(BaseLoginActivity.this);
            finish();
        }
    };

    @Inject User user;
    @Inject EndpointsApi endpointsApi;
    @Inject EndpointFactory endpointFactory;

    public static void start(Context context) {
        ActivityStarter.begin(context, LoginActivity.class).topLevel().start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setup
        getSupportActionBar().setTitle(null);

        // Start login
        if (savedInstanceState == null || !savedInstanceState.getBoolean(STATE_IS_LOGIN_STARTED)) {
            login();
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT:
                if (data != null && data.getExtras() != null) {
                    final String email = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (email != null) {
                        onAuthorizationSuccessful(email, null, null, null, null);
                    } else {
                        showError(new Throwable("Did not get account name."));
                    }
                }
                break;
        }
    }

    @Override protected void onResume() {
        super.onResume();
        getEventBus().register(registrationHandler);
    }

    @Override protected void onPause() {
        super.onPause();
        getEventBus().unregister(registrationHandler);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_LOGIN_STARTED, true);
    }

    @Override protected NavigationScreen getNavigationScreen() {
        return NavigationScreen.User;
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.Login;
    }

    protected void onAuthorizationSuccessful(String email, String firstName, String lastName, String photoUrl, String coverUrl) {
        user.setEmail(email);
        endpointsApi.register(this, email, firstName, lastName, photoUrl, coverUrl);
    }

    protected void login() {
        startActivityForResult(endpointFactory.getGoogleAccountCredential().newChooseAccountIntent(), REQUEST_ACCOUNT);
    }
}
