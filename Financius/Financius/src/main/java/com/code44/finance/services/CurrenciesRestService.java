package com.code44.finance.services;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import com.code44.finance.api.CurrencyAPI;
import com.code44.finance.api.Financius;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.providers.CurrenciesProvider;
import com.code44.finance.utils.CurrencyHelper;
import com.code44.finance.utils.ExchangeRatesHelper;
import org.json.JSONObject;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CurrenciesRestService extends AbstractService
{
    public static final String EXTRA_FROM_CODE = CurrenciesRestService.class.getName() + ".EXTRA_FROM_CODE";
    public static final String EXTRA_TO_CODE = CurrenciesRestService.class.getName() + ".EXTRA_TO_CODE";
    public static final String EXTRA_ONLY_USED = CurrenciesRestService.class.getName() + ".EXTRA_ONLY_USED";
    // -----------------------------------------------------------------------------------------------------------------
    public static final int RT_GET_EXCHANGE_RATE_TO_MAIN = 1;
    public static final int RT_GET_EXCHANGE_RATE = 2;
    public static final int RT_UPDATE_EXCHANGE_RATES = 3;
    // -----------------------------------------------------------------------------------------------------------------
    private double exchangeRate;

    @Override
    protected void handleRequest(Intent intent, int requestType, long startTime, long lastSuccessfulWorkTime) throws Exception
    {
        switch (requestType)
        {
            case RT_GET_EXCHANGE_RATE_TO_MAIN:
                rtGetExchangeRateToMain(intent);
                break;

            case RT_GET_EXCHANGE_RATE:
                rtGetExchangeRate(intent);
                break;

            case RT_UPDATE_EXCHANGE_RATES:
                rtUpdateExchangeRates(intent);
                break;
        }
    }

    @Override
    protected ServiceEvent getServiceEvent(Intent intent, int requestType, boolean force)
    {
        switch (requestType)
        {
            case RT_GET_EXCHANGE_RATE_TO_MAIN:
                return new GetExchangeRateEvent(requestType, force, intent.getStringExtra(EXTRA_FROM_CODE), CurrencyHelper.get().getMainCurrencyCode());

            case RT_GET_EXCHANGE_RATE:
                return new GetExchangeRateEvent(requestType, force, intent.getStringExtra(EXTRA_FROM_CODE), intent.getStringExtra(EXTRA_TO_CODE));
        }
        return super.getServiceEvent(intent, requestType, force);
    }

    @Override
    protected void onBeforePostEvent(Intent intent, int requestType, ServiceEvent.State state, ServiceEvent outEvent)
    {
        super.onBeforePostEvent(intent, requestType, state, outEvent);

        if ((requestType == RT_GET_EXCHANGE_RATE_TO_MAIN || requestType == RT_GET_EXCHANGE_RATE) && state == ServiceEvent.State.SUCCEEDED)
        {
            ((GetExchangeRateEvent) outEvent).setExchangeRate(exchangeRate);
        }
    }

    private void rtGetExchangeRateToMain(Intent intent) throws Exception
    {
        // Get values
        final String formCode = intent.getStringExtra(EXTRA_FROM_CODE);
        final String toCode = CurrencyHelper.get().getMainCurrencyCode();

        exchangeRate = getExchangeRate(formCode, toCode);
    }

    private void rtGetExchangeRate(Intent intent) throws Exception
    {
        // Get values
        final String fromCode = intent.getStringExtra(EXTRA_FROM_CODE);
        final String toCode = intent.getStringExtra(EXTRA_TO_CODE);

        exchangeRate = getExchangeRate(fromCode, toCode);
    }

    private void rtUpdateExchangeRates(Intent intent) throws Exception
    {
        final boolean onlyUsed = intent.getBooleanExtra(EXTRA_ONLY_USED, false);

        // Check if we need to update exchange rates
        if (onlyUsed && !ExchangeRatesHelper.getDefault(this).needUpdateExchangeRates())
            return;

        // Get default currency
        String defaultCurrency = null;
        Cursor c = null;
        try
        {
            c = getContentResolver().query(CurrenciesProvider.uriCurrencies(), new String[]{Tables.Currencies.CODE}, Tables.Currencies.IS_DEFAULT + "=?", new String[]{"1"}, null);
            if (c != null && c.moveToFirst())
            {
                defaultCurrency = c.getString(0);
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        // Get currencies
        final Set<String> usedCurrencies = new HashSet<String>();
        c = null;
        try
        {
            c = getContentResolver().query(onlyUsed ? AccountsProvider.uriAccounts() : CurrenciesProvider.uriCurrencies(), new String[]{Tables.Currencies.CODE, Tables.Currencies.IS_DEFAULT}, null, null, null);
            if (c != null && c.moveToFirst())
            {
                do
                {
                    if (!onlyUsed && c.getInt(1) == 0)
                        usedCurrencies.add(c.getString(0));
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        // There is nothing to update
        if (TextUtils.isEmpty(defaultCurrency) || usedCurrencies.size() == 0)
            return;

        // Update exchange rates
        final List<ContentValues> valuesList = new ArrayList<ContentValues>();
        ContentValues values;
        double exchangeRate;
        for (String currency : usedCurrencies)
        {
            try
            {
                exchangeRate = getExchangeRate(currency, defaultCurrency);
            }
            catch (Exception e)
            {
                continue;
            }

            values = new ContentValues();
            values.put(Tables.Currencies.CODE, currency);
            values.put(Tables.Currencies.EXCHANGE_RATE, exchangeRate);
            valuesList.add(values);
        }

        // Store to database
        for (ContentValues currencyValues : valuesList)
            getContentResolver().update(CurrenciesProvider.uriCurrencies(), currencyValues, Tables.Currencies.CODE + "=?", new String[]{currencyValues.getAsString(Tables.Currencies.CODE)});

        ExchangeRatesHelper.getDefault(this).updateExchangeRatesTimestamp();
    }

    private double getExchangeRate(String from, String to) throws Exception
    {
        final Response response = CurrencyAPI.getDefault(this).getService().getExchangeRate(from, to);
        return new JSONObject(Financius.parseResponseBody(response)).getDouble("rate");
    }

    public static class GetExchangeRateEvent extends ServiceEvent
    {
        private String fromCode;
        private String toCode;
        private double exchangeRate;

        public GetExchangeRateEvent(int requestType, boolean force, String fromCode, String toCode)
        {
            super(requestType, force);
            this.fromCode = fromCode;
            this.toCode = toCode;
        }

        public String getFromCode()
        {
            return fromCode;
        }

        public String getToCode()
        {
            return toCode;
        }

        public double getExchangeRate()
        {
            return exchangeRate;
        }

        public void setExchangeRate(double exchangeRate)
        {
            this.exchangeRate = exchangeRate;
        }
    }
}