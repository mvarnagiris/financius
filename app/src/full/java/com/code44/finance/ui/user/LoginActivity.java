package com.code44.finance.ui.user;

import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.ui.playservices.GoogleApiFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.common.base.Splitter;

public class LoginActivity extends BaseLoginActivity implements GoogleApiFragment.GoogleApiListener {
    private static final String FRAGMENT_GOOGLE_API = "FRAGMENT_GOOGLE_API";

    private static final String STATE_CLEAR_DEFAULT_ACCOUNT = "STATE_CLEAR_DEFAULT_ACCOUNT";

    private GoogleApiFragment googleApiFragment;
    private boolean clearDefaultAccount;

    @Override protected void onCreate(Bundle savedInstanceState) {
        clearDefaultAccount = savedInstanceState == null || savedInstanceState.getBoolean(STATE_CLEAR_DEFAULT_ACCOUNT);
        super.onCreate(savedInstanceState);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            finish();
            return;
        }

        if (googleApiFragment != null) {
            clearDefaultAccount = !googleApiFragment.handleOnActivityResult(requestCode, resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_CLEAR_DEFAULT_ACCOUNT, clearDefaultAccount);
    }

    @Override protected void login() {
        googleApiFragment = (GoogleApiFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_GOOGLE_API);
        loginGooglePlus();
    }

    @Override public void onGoogleApiConnected(GoogleApiClient client) {
        if (clearDefaultAccount) {
            clearDefaultAccount = false;
            Plus.AccountApi.clearDefaultAccount(client);
            googleApiFragment.disconnect();
            googleApiFragment.connect();
        } else {
            final Person person = Plus.PeopleApi.getCurrentPerson(client);
            final String email = Plus.AccountApi.getAccountName(client);
            final String photoUrl = person.hasImage() ? Splitter.on("?sz=").split(person.getImage().getUrl()).iterator().next() : null;
            final String coverUrl = person.hasCover() ? person.getCover().getCoverPhoto().getUrl() : null;
            onAuthorizationSuccessful(email, person.getName().getGivenName(), person.getName().getFamilyName(), photoUrl, coverUrl);
        }
    }

    @Override public void onGoogleApiFailed() {
        clearDefaultAccount = false;
        super.login();
    }

    @Override public void onGoogleApiNotAvailable() {
        super.login();
    }

    private void loginGooglePlus() {
        if (googleApiFragment != null) {
            googleApiFragment.connect();
            return;
        }

        googleApiFragment = GoogleApiFragment.build().setUsePlus(true).build();
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, googleApiFragment, FRAGMENT_GOOGLE_API).commit();
        googleApiFragment.connect();
    }
}
