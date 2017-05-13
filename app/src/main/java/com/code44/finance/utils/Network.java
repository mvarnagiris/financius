package com.code44.finance.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

import com.code44.finance.ApplicationContext;
import com.code44.finance.receivers.ConnectivityChangeReceiver;
import com.squareup.otto.Produce;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class Network {
    private final Context context;
    private final EventBus eventBus;

    private NetworkState previousNetworkState = null;
    private NetworkState networkState = null;

    @Inject public Network(@NonNull @ApplicationContext Context context, @NonNull EventBus eventBus) {
        this.context = checkNotNull(context, "Context cannot be null.");
        this.eventBus = checkNotNull(eventBus, "EventBus cannot be null.");
        networkState = getNetworkStateFromSystem();
        previousNetworkState = networkState;
        eventBus.register(this);
    }

    public static void enableConnectivityChangeReceiver(Context context) {
        final ComponentName receiver = new ComponentName(context, ConnectivityChangeReceiver.class);
        final PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void disableConnectivityChangeReceiver(Context context) {
        final ComponentName receiver = new ComponentName(context, ConnectivityChangeReceiver.class);
        final PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    @Produce public Network produceNetworkState() {
        return this;
    }

    public NetworkState getNetworkState() {
        return networkState;
    }

    public NetworkState getPreviousNetworkState() {
        return previousNetworkState;
    }

    public void updateNetworkState() {
        final NetworkState newNetworkState = getNetworkStateFromSystem();
        if (!newNetworkState.equals(networkState)) {
            previousNetworkState = networkState;
            networkState = newNetworkState;
            eventBus.post(this);
        }
    }

    private NetworkState getNetworkStateFromSystem() {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = cm.getActiveNetworkInfo();

        if (info == null || !info.isConnectedOrConnecting()) {
            return NetworkState.OFFLINE;
        }

        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                return NetworkState.ONLINE_WIFI;

            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        return NetworkState.ONLINE_4G;

                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        return NetworkState.ONLINE_3G;

                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return NetworkState.ONLINE_2G;

                    default:
                        // If it's unknown network, then assume the worst speed.
                        return NetworkState.ONLINE_2G;
                }

            default:
                // If it's unknown network, then assume the worst speed.
                return NetworkState.ONLINE_2G;
        }
    }

    public enum NetworkState {
        OFFLINE {
            @Override public boolean isOnline() {
                return false;
            }
        }, ONLINE_2G, ONLINE_3G, ONLINE_4G, ONLINE_WIFI;

        public boolean isOnline() {
            return true;
        }
    }
}
