package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrencyHelper;

public class AccountsAdapter extends AbstractCursorAdapter
{
    private int iTitle;
    private int iBalance;
    private int iCurrencyId;
    private int iExchangeRate;
    private int iIncludeInTotals;

    public AccountsAdapter(Context context)
    {
        super(context, null);
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup root)
    {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_account, root, false);
        final ViewHolder holder = new ViewHolder();
        //noinspection ConstantConditions
        holder.title_TV = (TextView) view.findViewById(R.id.title_TV);
        holder.balance_TV = (TextView) view.findViewById(R.id.balance_TV);
        holder.balanceMain_TV = (TextView) view.findViewById(R.id.balanceMain_TV);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor c)
    {
        final ViewHolder holder = (ViewHolder) view.getTag();

        // Get values
        final String title = c.getString(iTitle);
        final double balance = c.getDouble(iBalance);
        final long currencyId = c.getLong(iCurrencyId);

        // Set values
        holder.title_TV.setText(title);
        holder.title_TV.setTextColor(context.getResources().getColor(c.getInt(iIncludeInTotals) != 0 ? R.color.text_primary : R.color.text_secondary));
        holder.balance_TV.setText(AmountUtils.formatAmount(currencyId, balance));
        holder.balance_TV.setTextColor(AmountUtils.getBalanceColor(context, balance));
        if (currencyId == CurrencyHelper.get().getMainCurrencyId())
        {
            holder.balanceMain_TV.setVisibility(View.GONE);
        }
        else
        {
            final double convertedBalance = balance * c.getDouble(iExchangeRate);
            holder.balanceMain_TV.setVisibility(View.VISIBLE);
            holder.balanceMain_TV.setText(AmountUtils.formatAmount(CurrencyHelper.get().getMainCurrencyId(), convertedBalance));
            holder.balanceMain_TV.setTextColor(AmountUtils.getBalanceColor(context, convertedBalance, false));
        }
    }

    @Override
    protected void findIndexes(Cursor c)
    {
        iTitle = c.getColumnIndex(Tables.Accounts.TITLE);
        iBalance = c.getColumnIndex(Tables.Accounts.BALANCE);
        iCurrencyId = c.getColumnIndex(Tables.Accounts.CURRENCY_ID);
        iExchangeRate = c.getColumnIndex(Tables.Currencies.EXCHANGE_RATE);
        iIncludeInTotals = c.getColumnIndex(Tables.Accounts.SHOW_IN_TOTALS);
    }

    private static class ViewHolder
    {
        public TextView title_TV;
        public TextView balance_TV;
        public TextView balanceMain_TV;
    }
}