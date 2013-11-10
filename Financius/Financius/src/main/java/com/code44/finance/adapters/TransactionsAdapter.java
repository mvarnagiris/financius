package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrenciesHelper;
import com.code44.finance.utils.PeriodHelper;

import java.util.Calendar;

public class TransactionsAdapter extends AbstractSectionedCursorAdapter
{
    private final int darkColor;
    private final int darkSecondaryColor;
    private final int inverseColor;
    private final int greenColor;
    private final int yellowColor;
    private final int monthFlags;
    private final Calendar calendar;
    private int iDate;
    private int iAccountFromId;
    private int iAccountFromTitle;
    private int iAccountFromCurrencyId;
    private int iAccountFromCurrencyExchangeRate;
    private int iAccountToId;
    private int iAccountToTitle;
    private int iAccountToCurrencyId;
    private int iAccountToCurrencyExchangeRate;
    private int iCategoryId;
    private int iCategoryTitle;
    private int iCategoryType;
    private int iCategoryColor;
    private int iAmount;
    private int iNote;
    private int iState;
    private int iExchangeRate;

    public TransactionsAdapter(Context context)
    {
        super(context, null);
        darkColor = context.getResources().getColor(R.color.text_primary);
        darkSecondaryColor = context.getResources().getColor(R.color.text_secondary);
        inverseColor = context.getResources().getColor(R.color.text_primary_inverted);
        greenColor = context.getResources().getColor(R.color.text_green);
        yellowColor = context.getResources().getColor(R.color.text_yellow);
        monthFlags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_NO_NOON | DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_ABBREV_MONTH;
        calendar = Calendar.getInstance();
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup root)
    {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_transaction, root, false);

        final ViewHolder holder = new ViewHolder();
        holder.dateContainer_V = view.findViewById(R.id.dateContainer_V);
        holder.month_TV = (TextView) view.findViewById(R.id.month_TV);
        holder.day_TV = (TextView) view.findViewById(R.id.day_TV);
        holder.account_TV = (TextView) view.findViewById(R.id.account_TV);
        holder.amount_TV = (TextView) view.findViewById(R.id.amount_TV);
        holder.categoryTitle_TV = (TextView) view.findViewById(R.id.categoryTitle_TV);
        holder.amount_TV = (TextView) view.findViewById(R.id.amount_TV);
        holder.amountCurrency_TV = (TextView) view.findViewById(R.id.amountCurrency_TV);
        holder.note_TV = (TextView) view.findViewById(R.id.note_TV);
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor c)
    {
        final ViewHolder holder = (ViewHolder) view.getTag();

        // Get values
        final long date = c.getLong(iDate);
        final long categoryId = c.getLong(iCategoryId);
        final int categoryType = c.getInt(iCategoryType);
        final String note = c.getString(iNote);
        final int state = c.getInt(iState);
        calendar.setTimeInMillis(date);

        // Date box.
        if (state == Tables.Transactions.State.CONFIRMED)
        {
            // Transaction is confirmed - show full color
            holder.dateContainer_V.setBackgroundColor(c.getInt(iCategoryColor));
            holder.month_TV.setTextColor(inverseColor);
            holder.day_TV.setTextColor(inverseColor);
            holder.categoryTitle_TV.setTextColor(darkColor);
            holder.note_TV.setTextColor(darkColor);
        }
        else
        {
            // Transaction is NOT confirmed - show outline
            holder.dateContainer_V.setBackgroundResource(R.drawable.bg_pending);
            holder.month_TV.setTextColor(darkSecondaryColor);
            holder.day_TV.setTextColor(darkSecondaryColor);
            holder.categoryTitle_TV.setTextColor(darkSecondaryColor);
            holder.note_TV.setTextColor(darkSecondaryColor);
        }

        // Set values to date box
        holder.day_TV.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        holder.month_TV.setText(PeriodHelper.getPeriodShortTitle(context, PeriodHelper.TYPE_MONTH, date, date).toUpperCase());

        // Set category and note
        holder.categoryTitle_TV.setText(c.getString(iCategoryTitle));
        if (TextUtils.isEmpty(note))
        {
            holder.note_TV.setVisibility(View.GONE);
        }
        else
        {
            holder.note_TV.setText(note);
            holder.note_TV.setVisibility(View.VISIBLE);
        }

        // Set accounts and amounts
        switch (categoryType)
        {
            case Tables.Categories.Type.EXPENSE:
                holder.account_TV.setText(c.getString(iAccountFromTitle));
                holder.amount_TV.setText(AmountUtils.formatAmount(context, c.getLong(iAccountFromCurrencyId), c.getDouble(iAmount)));
                holder.amount_TV.setTextColor(state == Tables.Transactions.State.CONFIRMED ? darkColor : darkSecondaryColor);
                if (c.getLong(iAccountFromCurrencyId) != CurrenciesHelper.getDefault(context).getMainCurrencyId())
                {
                    holder.amountCurrency_TV.setVisibility(View.VISIBLE);
                    holder.amountCurrency_TV.setText(AmountUtils.formatAmount(context, CurrenciesHelper.getDefault(context).getMainCurrencyId(), c.getDouble(iAmount) * c.getDouble(iAccountFromCurrencyExchangeRate)));
                }
                else
                {
                    holder.amountCurrency_TV.setVisibility(View.GONE);
                }
                break;

            case Tables.Categories.Type.INCOME:
                holder.account_TV.setText(c.getString(iAccountToTitle));
                holder.amount_TV.setText(AmountUtils.formatAmount(context, c.getLong(iAccountToCurrencyId), c.getDouble(iAmount)));
                holder.amount_TV.setTextColor(state == Tables.Transactions.State.CONFIRMED ? greenColor : darkSecondaryColor);
                if (c.getLong(iAccountToCurrencyId) != CurrenciesHelper.getDefault(context).getMainCurrencyId())
                {
                    holder.amountCurrency_TV.setVisibility(View.VISIBLE);
                    holder.amountCurrency_TV.setText(AmountUtils.formatAmount(context, CurrenciesHelper.getDefault(context).getMainCurrencyId(), c.getDouble(iAmount) * c.getDouble(iAccountToCurrencyExchangeRate)));
                }
                else
                {
                    holder.amountCurrency_TV.setVisibility(View.GONE);
                }
                break;

            case Tables.Categories.Type.TRANSFER:
                holder.account_TV.setText(c.getString(iAccountFromTitle) + " \u21E8 " + c.getString(iAccountToTitle));
                holder.amount_TV.setText(AmountUtils.formatAmount(context, c.getLong(iAccountFromCurrencyId), c.getDouble(iAmount)));
                holder.amount_TV.setTextColor(state == Tables.Transactions.State.CONFIRMED ? yellowColor : darkSecondaryColor);
                if (c.getLong(iAccountFromCurrencyId) != c.getLong(iAccountToCurrencyId))
                {
                    holder.amountCurrency_TV.setVisibility(View.VISIBLE);
                    holder.amountCurrency_TV.setText(AmountUtils.formatAmount(context, c.getLong(iAccountToCurrencyId), c.getDouble(iAmount) * c.getDouble(iExchangeRate)));
                }
                else
                {
                    holder.amountCurrency_TV.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    protected void findIndexes(Cursor c)
    {
        iDate = c.getColumnIndex(Tables.Transactions.DATE);
        iAccountFromId = c.getColumnIndex(Tables.Transactions.ACCOUNT_FROM_ID);
        iAccountFromTitle = c.getColumnIndex(Tables.Accounts.AccountFrom.TITLE);
        iAccountFromCurrencyId = c.getColumnIndex(Tables.Accounts.AccountFrom.CURRENCY_ID);
        iAccountFromCurrencyExchangeRate = c.getColumnIndex(Tables.Currencies.CurrencyFrom.EXCHANGE_RATE);
        iAccountToId = c.getColumnIndex(Tables.Transactions.ACCOUNT_TO_ID);
        iAccountToTitle = c.getColumnIndex(Tables.Accounts.AccountTo.TITLE);
        iAccountToCurrencyId = c.getColumnIndex(Tables.Accounts.AccountTo.CURRENCY_ID);
        iAccountToCurrencyExchangeRate = c.getColumnIndex(Tables.Currencies.CurrencyTo.EXCHANGE_RATE);
        iAmount = c.getColumnIndex(Tables.Transactions.AMOUNT);
        iCategoryId = c.getColumnIndex(Tables.Transactions.CATEGORY_ID);
        iCategoryTitle = c.getColumnIndex(Tables.Categories.CategoriesChild.TITLE);
        iCategoryType = c.getColumnIndex(Tables.Categories.CategoriesChild.TYPE);
        iCategoryColor = c.getColumnIndex(Tables.Categories.CategoriesChild.COLOR);
        iNote = c.getColumnIndex(Tables.Transactions.NOTE);
        iState = c.getColumnIndex(Tables.Transactions.STATE);
        iExchangeRate = c.getColumnIndex(Tables.Transactions.EXCHANGE_RATE);
    }

    @Override
    protected boolean isSectionExpanded(int section, Cursor c)
    {
        return true;
    }

    @Override
    protected boolean onToggleSection(int section, boolean isExpanded, Cursor c)
    {
        return true;
    }

    @Override
    protected String getIndexColumnValue(Cursor c)
    {
        return c.getString(iDate);
    }

    @Override
    protected String getRowSectionUniqueId(Cursor c)
    {
        return c.getInt(iState) == Tables.Transactions.State.PENDING ? "-1" : DateUtils.formatDateTime(mContext, c.getLong(iDate), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY);
    }

    @Override
    protected View newHeaderView(Context context, int section, Cursor c, ViewGroup root)
    {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_h_transaction, root, false);

        final HeaderViewHolder holder = new HeaderViewHolder();
        holder.date_TV = (TextView) view.findViewById(R.id.date_TV);
        view.setTag(holder);

        return view;
    }

    @Override
    protected void bindHeaderView(View view, Context context, int section, Cursor c)
    {
        final HeaderViewHolder holder = (HeaderViewHolder) view.getTag();

        // Get values
        final long date = c.getLong(iDate);

        // Set values
        holder.date_TV.setText(c.getInt(iState) == Tables.Transactions.State.PENDING ? context.getString(R.string.pending) : PeriodHelper.getPeriodTitle(context, PeriodHelper.TYPE_MONTH, date, date));
    }

    private static class ViewHolder
    {
        View dateContainer_V;
        TextView month_TV;
        TextView day_TV;
        TextView account_TV;
        TextView categoryTitle_TV;
        TextView amount_TV;
        TextView amountCurrency_TV;
        TextView note_TV;
    }

    private static class HeaderViewHolder
    {
        TextView date_TV;
    }
}