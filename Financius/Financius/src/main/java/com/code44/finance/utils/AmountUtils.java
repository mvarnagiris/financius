package com.code44.finance.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import com.code44.finance.App;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CurrenciesProvider;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class AmountUtils
{
    private static final LongSparseArray<CurrencyFormat> formatArray = new LongSparseArray<CurrencyFormat>();
    private static int primaryColor;
    private static int secondaryColor;
    private static int negativeBalanceColor;
    private static NumberFormat decimalFormat;

    public static String formatAmount(long currencyId, double amount)
    {
        CurrencyFormat format = formatArray.get(currencyId);
        if (format == null)
        {
            format = initCurrency(currencyId);
            formatArray.put(currencyId, format);
        }

        return format.format(amount);
    }

    public static void onCurrencyUpdated(long currencyId)
    {
        formatArray.remove(currencyId);
        formatArray.put(currencyId, initCurrency(currencyId));
    }

    public static CurrencyFormat initCurrency(long currencyId)
    {
        char groupSeparator = ',';
        char decimalSeparator = '.';
        int decimals = 2;
        String symbol = "";
        String symbolFormat = Tables.Currencies.SymbolFormat.RIGHT_CLOSE;

        if (currencyId > 0)
        {
            Cursor c = null;
            try
            {
                c = App.getAppContext().getContentResolver().query(CurrenciesProvider.uriCurrency(currencyId), null, null, null, null);
                if (c == null || !c.moveToFirst())
                {
                    if (c != null && !c.isClosed())
                        c.close();

                    c = App.getAppContext().getContentResolver().query(CurrenciesProvider.uriCurrency(CurrenciesHelper.getDefault().getMainCurrencyId()), null, null, null, null);
                }

                if (c != null && c.moveToFirst())
                {
                    final String groupSeparatorStr = c.getString(c.getColumnIndex(Tables.Currencies.GROUP_SEPARATOR));
                    //noinspection ConstantConditions
                    groupSeparator = TextUtils.isEmpty(groupSeparatorStr) ? '\0' : groupSeparatorStr.charAt(0);
                    //noinspection ConstantConditions
                    decimalSeparator = c.getString(c.getColumnIndex(Tables.Currencies.DECIMAL_SEPARATOR)).charAt(0);
                    decimals = c.getInt(c.getColumnIndex(Tables.Currencies.DECIMALS));
                    symbol = c.getString(c.getColumnIndex(Tables.Currencies.SYMBOL));
                    symbolFormat = c.getString(c.getColumnIndex(Tables.Currencies.SYMBOL_FORMAT));
                }
                else throw new NumberFormatException();
            }
            catch (Exception e)
            {
                // Ignore
            }
            finally
            {
                if (c != null && !c.isClosed())
                    c.close();
            }
        }

        return new CurrencyFormat(groupSeparator, decimalSeparator, decimals, symbol, symbolFormat);
    }

    public static String formatAmount(double amount)
    {
        if (decimalFormat == null)
        {
            decimalFormat = DecimalFormat.getInstance();
            decimalFormat.setMinimumFractionDigits(2);
            decimalFormat.setMaximumFractionDigits(2);
        }
        return decimalFormat.format(amount);
    }

    public static int getBalanceColor(Context context, double balance)
    {
        return getBalanceColor(context, balance, true);
    }

    public static int getBalanceColor(Context context, double balance, boolean isPrimary)
    {
        final int color;
        if (balance < 0)
        {
            if (negativeBalanceColor <= 0)
                negativeBalanceColor = context.getResources().getColor(R.color.text_red);
            color = negativeBalanceColor;
        }
        else
        {
            if (isPrimary)
            {
                if (primaryColor <= 0)
                    primaryColor = context.getResources().getColor(R.color.text_primary);
                color = primaryColor;
            }
            else
            {
                if (secondaryColor <= 0)
                    secondaryColor = context.getResources().getColor(R.color.text_secondary);
                color = secondaryColor;
            }
        }

        return color;
    }

    public static double getAmount(String amountStr)
    {
        if (TextUtils.isEmpty(amountStr))
            return 0;
        else
        {
            if (amountStr.startsWith("+"))
                amountStr = amountStr.substring(1);
            try
            {
                return NumberFormat.getInstance().parse(amountStr).doubleValue();
            }
            catch (ParseException e)
            {
                return 0;
            }
        }
    }
}