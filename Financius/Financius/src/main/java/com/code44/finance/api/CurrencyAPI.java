package com.code44.finance.api;

import android.content.Context;
import retrofit.RestAdapter;

public class CurrencyAPI
{
    private static CurrencyAPI instance;
    private Context context;
    private CurrencyService service;

    public CurrencyAPI(Context context)
    {
        this.context = context.getApplicationContext();

        final RestAdapter restAdapter = new RestAdapter.Builder().setServer("http://rate-exchange.appspot.com").build();
        service = restAdapter.create(CurrencyService.class);
    }

    public static CurrencyAPI getDefault(Context context)
    {
        if (instance == null)
            instance = new CurrencyAPI(context);
        return instance;
    }

    public CurrencyService getService()
    {
        return service;
    }
}