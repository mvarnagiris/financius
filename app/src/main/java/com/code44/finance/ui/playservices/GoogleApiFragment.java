package com.code44.finance.ui.playservices;

import android.app.Activity;
import android.app.Dialog;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.code44.finance.ui.common.fragments.BaseFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;

public class GoogleApiFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String ARG_USE_PLUS = "ARG_USE_PLUS";
    private static final String ARG_USE_DRIVE = "ARG_USE_DRIVE";

    private static final String FRAGMENT_ERROR_DIALOG = "FRAGMENT_ERROR_DIALOG";

    private static final int REQUEST_RESOLVE_ERROR = 9000;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 9001;

    private GoogleApiClient client;
    private boolean connectWhenPossible = false;
    private GoogleApiListener googleApiListener;

    public static Builder build() {
        return new Builder();
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof GoogleApiListener) {
            googleApiListener = (GoogleApiListener) activity;
        }
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        buildGoogleApiClient();
        if (connectWhenPossible) {
            connect();
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    @Override public void onDetach() {
        super.onDetach();
        googleApiListener = null;
    }

    @Override public void onConnected(Bundle bundle) {
        if (googleApiListener != null) {
            googleApiListener.onGoogleApiConnected(client);
        }
    }

    @Override public void onConnectionSuspended(int cause) {
    }

    @Override public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                if (getActivity() == null) {
                    return;
                }
                connectionResult.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                client.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());

            if (googleApiListener != null) {
                googleApiListener.onGoogleApiFailed();
            }
        }
    }

    public boolean handleOnActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            if (resultCode == Activity.RESULT_OK) {
                connect();
            }
            return true;
        }

        return false;
    }

    public void connect() {
        if (getActivity() == null || client == null) {
            connectWhenPossible = true;
            return;
        }

        connectWhenPossible = false;
        if (checkGooglePlayServicesAvailable()) {
            if (client.isConnected() || client.isConnecting()) {
                onConnected(null);
            } else {
                client.connect();
            }
        }
    }

    public void disconnect() {
        connectWhenPossible = false;

        if (client != null && (client.isConnected() || client.isConnecting())) {
            client.disconnect();
        } else {
            onConnectionSuspended(0);
        }
    }

    public GoogleApiClient getClient() {
        return client;
    }

    private void buildGoogleApiClient() {
        // Get arguments
        final Bundle args = getArguments();
        final boolean usePlus = args.getBoolean(ARG_USE_PLUS, false);
        final boolean useDrive = args.getBoolean(ARG_USE_DRIVE, false);

        // Init a client
        final GoogleApiClient.Builder builder = new GoogleApiClient.Builder(getActivity(), this, this);

        if (usePlus) {
            builder.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN);
        }

        if (useDrive) {
            builder.addApi(Drive.API).addScope(Drive.SCOPE_FILE);
        }

        client = builder.build();
    }

    private boolean checkGooglePlayServicesAvailable() {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            //noinspection StatementWithEmptyBody
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), REQUEST_GOOGLE_PLAY_SERVICES).show();
            } else {
                if (googleApiListener != null) {
                    googleApiListener.onGoogleApiNotAvailable();
                }
            }
            return false;
        }
        return true;
    }

    private void showErrorDialog(int errorCode) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, activity, REQUEST_GOOGLE_PLAY_SERVICES);
        if (errorDialog != null) {
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();
            errorFragment.setDialog(errorDialog);
            errorFragment.show(getFragmentManager(), FRAGMENT_ERROR_DIALOG);
        }
    }

    public interface GoogleApiListener {
        void onGoogleApiConnected(GoogleApiClient client);

        void onGoogleApiFailed();

        void onGoogleApiNotAvailable();
    }

    public static class Builder {
        boolean usePlus;
        boolean useDrive;

        public Builder setUsePlus(boolean usePlus) {
            this.usePlus = usePlus;
            return this;
        }

        public Builder setUseDrive(boolean useDrive) {
            this.useDrive = useDrive;
            return this;
        }

        public GoogleApiFragment build() {
            final Bundle args = new Bundle();
            args.putBoolean(ARG_USE_PLUS, usePlus);
            args.putBoolean(ARG_USE_DRIVE, useDrive);

            GoogleApiFragment fragment = new GoogleApiFragment();
            fragment.setArguments(args);
            return fragment;
        }
    }

    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog dialog;

        public ErrorDialogFragment() {
            super();
            dialog = null;
        }

        public void setDialog(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override @NonNull public Dialog onCreateDialog(Bundle savedInstanceState) {
            return dialog;
        }
    }
}
