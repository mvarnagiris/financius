package com.code44.finance.utils;

import android.database.Cursor;
import com.code44.finance.App;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CurrenciesProvider;

public class CurrencyHelper
{
    private static CurrencyHelper instance;
    private long mainCurrencyId;
    private String mainCurrencyCode;

    private CurrencyHelper()
    {
        update();
    }

    public static CurrencyHelper get()
    {
        if (instance == null)
            instance = new CurrencyHelper();
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
            c = App.getAppContext().getContentResolver().query(CurrenciesProvider.uriCurrencies(), new String[]{Tables.Currencies.T_ID, Tables.Currencies.CODE}, Tables.Currencies.IS_DEFAULT + "=?", new String[]{"1"}, null);
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