package com.code44.finance.ui;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

public class DriveApiFragment extends GoogleApiFragment
{
    public static DriveApiFragment newInstance()
    {
        return new DriveApiFragment();
    }

    @Override
    protected GoogleApiClient createClient()
    {
        return new GoogleApiClient.Builder(getActivity())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected boolean handleActionEvent(GoogleApiActionEvent event)
    {
        return event instanceof DriveApiConnectEvent || event instanceof DriveApiDisconnectEvent;
    }

    @Override
    protected Class<? extends GoogleApiConnectedEvent> getConnectedEventClass()
    {
        return DriveApiConnectedEvent.class;
    }

    @Override
    protected Class<? extends GoogleApiDisconnectedEvent> getDisconnectedEventClass()
    {
        return DriveApiDisconnectedEvent.class;
    }

    @Override
    protected Class<? extends GoogleApiFailedEvent> getFailedEventClass()
    {
        return DriveApiFailedEvent.class;
    }

    public static class DriveApiConnectedEvent extends GoogleApiConnectedEvent
    {
        public DriveApiConnectedEvent(GoogleApiClient client)
        {
            super(client);
        }
    }

    public static class DriveApiDisconnectedEvent extends GoogleApiDisconnectedEvent
    {
        public DriveApiDisconnectedEvent(GoogleApiClient client)
        {
            super(client);
        }
    }

    public static class DriveApiFailedEvent extends GoogleApiFailedEvent
    {
        public DriveApiFailedEvent(GoogleApiClient client, ConnectionResult connectionResult)
        {
            super(client, connectionResult);
        }
    }

    public static class DriveApiConnectEvent extends GoogleApiConnectEvent
    {
    }

    public static class DriveApiDisconnectEvent extends GoogleApiDisconnectEvent
    {
    }
}
