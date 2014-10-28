package com.code44.finance.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.api.Api;
import com.code44.finance.ui.GoogleApiFragment;
import com.code44.finance.ui.common.BaseActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import javax.inject.Inject;


// TODO http://android-developers.blogspot.co.uk/2014/10/tips-for-integrating-with-google.html
public class LoginActivity extends BaseActivity {
    private static final String FRAGMENT_GOOGLE_API = "FRAGMENT_GOOGLE_API";

    private static final String STATE_CLEAR_DEFAULT_ACCOUNT = "STATE_CLEAR_DEFAULT_ACCOUNT";

    private static final String UNIQUE_GOOGLE_CLIENT_ID = LoginActivity.class.getName();

    @Inject Api api;

    private GoogleApiFragment googleApi_F;
    private boolean clearDefaultAccount;

    public static void start(Context context) {
        startActivity(context, makeIntentForActivity(context, LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup ActionBar
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle(R.string.login);


        // Restore state
        clearDefaultAccount = savedInstanceState == null || savedInstanceState.getBoolean(STATE_CLEAR_DEFAULT_ACCOUNT);

        googleApi_F = (GoogleApiFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_GOOGLE_API);
        if (googleApi_F == null) {
            googleApi_F = new GoogleApiFragment.Builder(UNIQUE_GOOGLE_CLIENT_ID).setUsePlus(true).build();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(googleApi_F, FRAGMENT_GOOGLE_API)
                    .commit();
            googleApi_F.connect();
        }
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

        googleApi_F.handleOnActivityResult(requestCode, resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

//    @SuppressWarnings("UnusedDeclaration")
// TODO     public void onEventMainThread(RegisterRequest.RegisterRequestEvent event) {
//        if (event.isFinished()) {
//            if (event.isError()) {
//                GoogleApiClient client = googleApi_F.getClient();
//                if (client != null && client.isConnected()) {
//                    Plus.AccountApi.clearDefaultAccount(client);
//                }
//
//                //noinspection ThrowableResultOfMethodCallIgnored
//                Toast.makeText(this, event.getError().getMessage(), Toast.LENGTH_LONG).show();
//            }
//            finish();
//        }
//    }

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
        api.register(email, googleId, firstName, lastName, photoUrl, coverUrl);
    }
}
