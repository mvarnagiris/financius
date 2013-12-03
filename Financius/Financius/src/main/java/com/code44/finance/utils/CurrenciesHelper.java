package com.code44.finance.utils;

import android.database.Cursor;
import com.code44.finance.FinanciusApp;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CurrenciesProvider;

public class CurrenciesHelper
{
    private static CurrenciesHelper instance;
    private long mainCurrencyId;
    private String mainCurrencyCode;

    private CurrenciesHelper()
    {
        update();
    }

    public static CurrenciesHelper getDefault()
    {
        if (instance == null)
            instance = new CurrenciesHelper();
        return instance;
    }

    public long getMainCurrencyId()
    {
        return mainCurrencyId;
    }

    public void setMainCurrencyId(long mainCurrencyId)
    {
        this.mainCurrencyId = mainCurrencyId;
    }

    public String getMainCurrencyCode()
    {
        return mainCurrencyCode;
    }

    public void setMainCurrencyCode(String mainCurrencyCode)
    {
        this.mainCurrencyCode = mainCurrencyCode;
    }

    public void update()
    {
        Cursor c = null;
        try
        {
            c = FinanciusApp.getAppContext().getContentResolver().query(CurrenciesProvider.uriCurrencies(), new String[]{Tables.Currencies.T_ID, Tables.Currencies.CODE}, Tables.Currencies.IS_DEFAULT + "=?", new String[]{"1"}, null);
            if (c != null && c.moveToFirst())
            {
                mainCurrencyId = c.getLong(0);
                mainCurrencyCode = c.getString(1);
            }
            else
            {
                mainCurrencyId = 0;
                mainCurrencyCode = null;
            }
        }
        catch (Exception e)
        {
            mainCurrencyId = 0;
            mainCurrencyCode = null;
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }
    }
}