package com.code44.finance.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.widget.TextView;
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
    private static int expenseColor;
    private static int incomeColor;
    private static int transferColor;
    private static int negativeBalanceColor;
    private static NumberFormat decimalFormat;

    public static String formatAmount(Context context, long currencyId, double amount)
    {
        CurrencyFormat format = formatArray.get(currencyId);
        if (format == null)
        {
            format = initCurrency(context, currencyId);
            formatArray.put(currencyId, format);
        }

        return format.format(amount);
    }

    public static void onCurrencyUpdated(Context context, long currencyId)
    {
        formatArray.remove(currencyId);
        formatArray.put(currencyId, initCurrency(context, currencyId));
    }

    public static CurrencyFormat initCurrency(Context context, long currencyId)
    {
        CurrencyFormat format;
        Cursor c = null;
        try
        {
            c = context.getContentResolver().query(CurrenciesProvider.uriCurrency(context, currencyId), null, null, null, null);
            if (c == null || !c.moveToFirst())
            {
                if (c != null && !c.isClosed())
                    c.close();

                c = context.getContentResolver().query(CurrenciesProvider.uriCurrency(context, CurrenciesHelper.getDefault(context).getMainCurrencyId()), null, null, null, null);
            }

            if (c != null && c.moveToFirst())
            {
                final String groupSeparator = c.getString(c.getColumnIndex(Tables.Currencies.GROUP_SEPARATOR));
                format = new CurrencyFormat(TextUtils.isEmpty(groupSeparator) ? '\0' : groupSeparator.charAt(0),
                        c.getString(c.getColumnIndex(Tables.Currencies.DECIMAL_SEPARATOR)).charAt(0),
                        c.getInt(c.getColumnIndex(Tables.Currencies.DECIMALS)),
                        c.getString(c.getColumnIndex(Tables.Currencies.SYMBOL)),
                        c.getString(c.getColumnIndex(Tables.Currencies.SYMBOL_FORMAT)));
            }
            else throw new NumberFormatException();
        }
        catch (NullPointerException e)
        {
            format = new CurrencyFormat();
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        return format;
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

    public static void setAmount(TextView amount_TV, double amount, int categoryType)
    {
        // TODO Review this method
        switch (categoryType)
        {
            case Tables.Categories.Type.EXPENSE:
                final int color;
                if (amount < 0)
                {
                    if (negativeBalanceColor <= 0)
                        negativeBalanceColor = amount_TV.getContext().getResources().getColor(R.color.text_red);
                    color = negativeBalanceColor;
                }
                else
                {
                    if (expenseColor <= 0)
                        expenseColor = amount_TV.getContext().getResources().getColor(R.color.text_primary);
                    color = expenseColor;
                }
                amount_TV.setTextColor(color);
                amount_TV.setText(formatAmount(amount));
                break;

            case Tables.Categories.Type.INCOME:
                if (incomeColor <= 0)
                    incomeColor = amount_TV.getContext().getResources().getColor(R.color.text_green);
                amount_TV.setTextColor(incomeColor);
                amount_TV.setText(formatAmount(amount));
                break;

            case Tables.Categories.Type.TRANSFER:
                if (transferColor <= 0)
                    transferColor = amount_TV.getContext().getResources().getColor(R.color.text_yellow);
                amount_TV.setTextColor(transferColor);
                amount_TV.setText(formatAmount(amount));
                break;
        }
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