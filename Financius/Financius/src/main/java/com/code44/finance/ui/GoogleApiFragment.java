package com.code44.finance.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.reflect.InvocationTargetException;

import de.greenrobot.event.EventBus;

public abstract class GoogleApiFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final String FRAGMENT_ERROR_DIALOG = "FRAGMENT_ERROR_DIALOG";
    // ---------------------------------------------------------------------------------------------
    private static final int REQUEST_RESOLVE_ERROR = 9000;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 9001;
    // ---------------------------------------------------------------------------------------------
    protected GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // Init a client
        client = createClient();

        // Register events
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        checkGooglePlayServicesAvailable();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // Unregister events
        EventBus.getDefault().unregister(this);

        // Disconnect Plus Client
        if (client.isConnecting() || client.isConnected())
            client.disconnect();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(GoogleApiActionEvent event)
    {
        if (handleActionEvent(event))
        {
            if (event instanceof GoogleApiConnectEvent)
            {
                connect();
            }
            else if (event instanceof GoogleApiDisconnectEvent)
            {
                disconnect();
            }
        }
    }

    protected abstract GoogleApiClient createClient();

    protected abstract boolean handleActionEvent(GoogleApiActionEvent event);

    @Override
    public void onConnected(Bundle bundle)
    {
        sendEventConnected();
    }

    @Override
    public void onDisconnected()
    {
        sendEventDisconnected();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        sendEventFailed(connectionResult);
        /*
         * Google Play services can resolve some errors it detects. If the error has a resolution,
         * try sending an Intent to start a Google Play services activity that can resolve error.
         */
        if (connectionResult.hasResolution())
        {
            try
            {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            }
            catch (IntentSender.SendIntentException e)
            {
                // Thrown if Google Play services canceled the original PendingIntent.
                e.printStackTrace();
            }
        }
        else
        {
            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    public boolean handleOnActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_RESOLVE_ERROR)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                connect();
            }
            return true;
        }

        return false;
    }

    public void connect()
    {
        if (!client.isConnected() || !client.isConnecting())
        {
            client.connect();
        }
        else
        {
            sendEventConnected();
        }
    }

    public void disconnect()
    {
        if (client.isConnected() || client.isConnecting())
        {
            client.disconnect();
        }
        else
        {
            sendEventDisconnected();
        }
    }

    public GoogleApiClient getClient()
    {
        return client;
    }

    protected Class<? extends GoogleApiConnectedEvent> getConnectedEventClass()
    {
        return GoogleApiConnectedEvent.class;
    }

    protected Class<? extends GoogleApiDisconnectedEvent> getDisconnectedEventClass()
    {
        return GoogleApiDisconnectedEvent.class;
    }

    protected Class<? extends GoogleApiFailedEvent> getFailedEventClass()
    {
        return GoogleApiFailedEvent.class;
    }

    protected void sendEventConnected()
    {
        try
        {
            EventBus.getDefault()
                    .post(getConnectedEventClass().getConstructor(client.getClass())
                            .newInstance(client));
        }
        catch (NoSuchMethodException | InvocationTargetException | java.lang.InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    protected void sendEventDisconnected()
    {
        try
        {
            EventBus.getDefault()
                    .post(getDisconnectedEventClass().getConstructor(client.getClass())
                            .newInstance(client));
        }
        catch (NoSuchMethodException | InvocationTargetException | java.lang.InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    protected void sendEventFailed(ConnectionResult connectionResult)
    {
        try
        {
            EventBus.getDefault()
                    .post(getFailedEventClass().getConstructor(client.getClass(), ConnectionResult.class)
                            .newInstance(client, connectionResult));
        }
        catch (NoSuchMethodException | InvocationTargetException | java.lang.InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private boolean checkGooglePlayServicesAvailable()
    {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS)
        {
            //noinspection StatementWithEmptyBody
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), REQUEST_GOOGLE_PLAY_SERVICES)
                        .show();
            }
            else
            {
                // TODO Notify user that device is not supported
            }
            return false;
        }
        return true;
    }

    private void showErrorDialog(int errorCode)
    {
        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(), REQUEST_GOOGLE_PLAY_SERVICES);

        // If Google Play services can provide an error dialog
        if (errorDialog != null)
        {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getFragmentManager(), FRAGMENT_ERROR_DIALOG);
        }
    }

    public static abstract class GoogleApiEvent
    {
        private final GoogleApiClient client;

        public GoogleApiEvent(GoogleApiClient client)
        {
            this.client = client;
        }

        public GoogleApiClient getClient()
        {
            return client;
        }
    }

    public static class GoogleApiConnectedEvent extends GoogleApiEvent
    {
        public GoogleApiConnectedEvent(GoogleApiClient client)
        {
            super(client);
        }
    }

    public static class GoogleApiDisconnectedEvent extends GoogleApiEvent
    {
        public GoogleApiDisconnectedEvent(GoogleApiClient client)
        {
            super(client);
        }
    }

    public static class GoogleApiFailedEvent extends GoogleApiEvent
    {
        private final ConnectionResult connectionResult;

        public GoogleApiFailedEvent(GoogleApiClient client, ConnectionResult connectionResult)
        {
            super(client);
            this.connectionResult = connectionResult;
        }

        public ConnectionResult getConnectionResult()
        {
            return connectionResult;
        }
    }

    public static abstract class GoogleApiActionEvent
    {
    }

    public static class GoogleApiConnectEvent extends GoogleApiActionEvent
    {
    }

    public static class GoogleApiDisconnectEvent extends GoogleApiActionEvent
    {
    }

    public static class ErrorDialogFragment extends DialogFragment
    {
        private Dialog dialog;

        public ErrorDialogFragment()
        {
            super();
            dialog = null;
        }

        public void setDialog(Dialog dialog)
        {
            this.dialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            return dialog;
        }
    }
}