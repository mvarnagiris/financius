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

@SuppressWarnings("ConstantConditions")
public class CurrenciesAdapter extends AbstractCursorAdapter
{
    private int iId;
    private int iCode;
    private int iExchangeRate;
    private int iIsDefault;

    public CurrenciesAdapter(Context context)
    {
        super(context, null);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup)
    {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_currency, viewGroup, false);
        final ViewHolder holder = new ViewHolder();
        holder.code_TV = (TextView) view.findViewById(R.id.code_TV);
        holder.exchangeRate_TV = (TextView) view.findViewById(R.id.exchangeRate_TV);
        holder.format_TV = (TextView) view.findViewById(R.id.format_TV);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        final ViewHolder holder = (ViewHolder) view.getTag();

        // Get values
        final String code = cursor.getString(iCode);

        // Set values
        holder.code_TV.setText(code);
        if (cursor.getInt(iIsDefault) != 0)
        {
            final int color = context.getResources().getColor(R.color.text_green);
            holder.code_TV.setTextColor(color);
            holder.exchangeRate_TV.setText(R.string.main_currency);
            holder.exchangeRate_TV.setTextColor(color);
        }
        else
        {
            final int color = context.getResources().getColor(R.color.text_primary);
            holder.code_TV.setTextColor(color);
            holder.exchangeRate_TV.setText(String.valueOf(cursor.getDouble(iExchangeRate)));
            holder.exchangeRate_TV.setTextColor(color);
        }
        holder.format_TV.setText("(" + AmountUtils.formatAmount(context, cursor.getLong(iId), 1000.00) + ")");
    }

    @Override
    protected void findIndexes(Cursor c)
    {
        iId = c.getColumnIndex(Tables.Currencies.ID);
        iCode = c.getColumnIndex(Tables.Currencies.CODE);
        iExchangeRate = c.getColumnIndex(Tables.Currencies.EXCHANGE_RATE);
        iIsDefault = c.getColumnIndex(Tables.Currencies.IS_DEFAULT);
    }

    private static class ViewHolder
    {
        public TextView code_TV;
        public TextView exchangeRate_TV;
        public TextView format_TV;
    }
}
