package com.code44.finance.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import com.code44.finance.API;

public class ExchangeRatesHelper
{
    private static final long EXCHANGE_RATE_VALID_INTERVAL = DateUtils.DAY_IN_MILLIS;
    // --------------------------------------------------------------------------------------------------------------------------------
    private static ExchangeRatesHelper instance = null;
    private Context context;
    // --------------------------------------------------------------------------------------------------------------------------------
    private String exchangeRatesValue;
    private long exchangeRatesTimestamp;

    private ExchangeRatesHelper(Context context)
    {
        this.context = context.getApplicationContext();

        // Read exchange rates preferences
        final SharedPreferences prefs = PrefsHelper.getPrefs(context);
        exchangeRatesValue = prefs.getString(PrefsHelper.PREF_EXCHANGE_RATES_UPDATE_EXCHANGE_RATES, "0");
        exchangeRatesTimestamp = prefs.getLong(PrefsHelper.PREFIX_EXCHANGE_RATES, 0);
    }

    public static ExchangeRatesHelper getDefault(Context context)
    {
        if (instance == null)
            instance = new ExchangeRatesHelper(context);
        return instance;
    }

    public void setExchangeRatesValue(String newValue)
    {
        this.exchangeRatesValue = newValue;
    }

    public void updateExchangeRatesTimestamp()
    {
        this.exchangeRatesTimestamp = System.currentTimeMillis();
        PrefsHelper.storeLong(context, PrefsHelper.PREF_EXCHANGE_RATES_TIMESTAMP, exchangeRatesTimestamp);
    }

    public boolean needUpdateExchangeRates()
    {
        //noinspection SimplifiableIfStatement
        if ((System.currentTimeMillis() - exchangeRatesTimestamp <= EXCHANGE_RATE_VALID_INTERVAL) || exchangeRatesValue.equals("2"))
            return false;

        return !(exchangeRatesValue.equals("1") && !isWifi());

    }

    public void startExchangeRateUpdatesIfNecessary()
    {
        if (needUpdateExchangeRates())
            API.updateExchangeRates(context, true);
    }

    private boolean isWifi()
    {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}