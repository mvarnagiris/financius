package com.code44.finance.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.code44.finance.App;
import com.code44.finance.utils.Network;

import javax.inject.Inject;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    @Inject Network network;

    @Override public void onReceive(Context context, Intent intent) {
        App.with(context).inject(this);
        network.updateNetworkState();
    }
}
