package com.code44.finance.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.code44.finance.R;
import com.code44.finance.api.financius.FinanciusApi;
import com.code44.finance.api.financius.requests.RegisterRequest;
import com.code44.finance.ui.BaseActivity;
import com.code44.finance.ui.GoogleApiFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import de.greenrobot.event.EventBus;

public class LoginActivity extends BaseActivity {
    private static final String FRAGMENT_GOOGLE_PLUS = "FRAGMENT_GOOGLE_PLUS";

    private static final String STATE_CLEAR_DEFAULT_ACCOUNT = "STATE_CLEAR_DEFAULT_ACCOUNT";

    private static final String UNIQUE_GOOGLE_CLIENT_ID = LoginActivity.class.getName();

    private GoogleApiFragment googleApi_F;

    private boolean clearDefaultAccount;

    public static void start(Context context, View expandFrom) {
        startScaleUp(context, makeIntent(context, LoginActivity.class), expandFrom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().registerSticky(this);

        // Setup ActionBar
        //noinspection ConstantConditions
        getActionBar().setDisplayHomeAsUpEnabled(false);
        setActionBarTitle(R.string.login);


        // Restore state
        clearDefaultAccount = savedInstanceState == null || savedInstanceState.getBoolean(STATE_CLEAR_DEFAULT_ACCOUNT);

        googleApi_F = (GoogleApiFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_GOOGLE_PLUS);
        if (googleApi_F == null) {
            googleApi_F = new GoogleApiFragment.Builder(UNIQUE_GOOGLE_CLIENT_ID).setUsePlus(true).build();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(googleApi_F, FRAGMENT_GOOGLE_PLUS)
                    .commit();
            googleApi_F.connect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_CLEAR_DEFAULT_ACCOUNT, clearDefaultAccount);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            finish();
            return;
        }

        googleApi_F.handleOnActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(RegisterRequest.RegisterRequestEvent event) {
        if (event.isFinished()) {
            if (event.isError()) {
                GoogleApiClient client = googleApi_F.getClient();
                if (client != null && client.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(client);
                }

                //noinspection ThrowableResultOfMethodCallIgnored
                Toast.makeText(this, event.getError().getMessage(), Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(GoogleApiFragment.GoogleApiConnectedEvent event) {
        if (!UNIQUE_GOOGLE_CLIENT_ID.equals(event.getUniqueClientId())) {
            return;
        }

        GoogleApiClient client = event.getClient();
        if (clearDefaultAccount) {
            clearDefaultAccount = false;
            Plus.AccountApi.clearDefaultAccount(client);
            googleApi_F.disconnect();
            googleApi_F.connect();
        } else {
            Person person = Plus.PeopleApi.getCurrentPerson(client);
            String email = Plus.AccountApi.getAccountName(client);
            login(person, email);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(GoogleApiFragment.GoogleApiFailedEvent event) {
        if (!UNIQUE_GOOGLE_CLIENT_ID.equals(event.getUniqueClientId())) {
            return;
        }

        clearDefaultAccount = false;
    }

    public void login(Person person, String email) {
        final String googleId = person.getId();
        final String firstName = person.getName().getGivenName();
        final String lastName = person.getName().getFamilyName();
        final String photoUrl = person.getImage().getUrl();
        final String coverUrl = person.getCover().getCoverPhoto().getUrl();
        FinanciusApi.get().register(email, googleId, firstName, lastName, photoUrl, coverUrl);
    }
}
