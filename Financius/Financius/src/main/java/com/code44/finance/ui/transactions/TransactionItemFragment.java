package com.code44.finance.ui.transactions;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.code44.finance.API;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.TransactionsProvider;
import com.code44.finance.ui.ItemFragment;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrenciesHelper;

public class TransactionItemFragment extends ItemFragment
{
    private TextView date_TV;
    private TextView account_TV;
    private View color_V;
    private TextView category_TV;
    private TextView amount_TV;
    private TextView exchangeRate_TV;
    private TextView mainCurrencyAmount_TV;
    private TextView note_TV;
    private TextView notIncluded_TV;

    public static TransactionItemFragment newInstance(long itemId)
    {
        final TransactionItemFragment f = new TransactionItemFragment();
        f.setArguments(makeArgs(itemId));
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        date_TV = (TextView) view.findViewById(R.id.date_TV);
        account_TV = (TextView) view.findViewById(R.id.account_TV);
        color_V = view.findViewById(R.id.color_V);
        category_TV = (TextView) view.findViewById(R.id.category_TV);
        amount_TV = (TextView) view.findViewById(R.id.amount_TV);
        exchangeRate_TV = (TextView) view.findViewById(R.id.exchangeRate_TV);
        mainCurrencyAmount_TV = (TextView) view.findViewById(R.id.mainCurrencyAmount_TV);
        note_TV = (TextView) view.findViewById(R.id.note_TV);
        notIncluded_TV = (TextView) view.findViewById(R.id.notIncluded_TV);
        final ViewGroup container_V = (ViewGroup) view.findViewById(R.id.container_V);
    }

    @Override
    protected void startItemEdit(Context context, long itemId)
    {
        TransactionEditActivity.startItemEdit(context, itemId);
    }

    @Override
    protected boolean onDeleteItem(Context context, long[] itemIds)
    {
        API.deleteTransactions(context, itemIds);
        return true;
    }

    @Override
    protected Loader<Cursor> createItemLoader(Context context, long itemId)
    {
        final Uri uri = TransactionsProvider.uriTransaction(getActivity(), itemId);
        final String[] projection = new String[]{
                Tables.Transactions.T_ID, Tables.Transactions.DATE, Tables.Transactions.AMOUNT, Tables.Transactions.NOTE, Tables.Transactions.STATE, Tables.Transactions.EXCHANGE_RATE, Tables.Transactions.SHOW_IN_TOTALS,
                Tables.Transactions.ACCOUNT_FROM_ID, Tables.Accounts.AccountFrom.S_TITLE, Tables.Accounts.AccountFrom.S_CURRENCY_ID, Tables.Currencies.CurrencyFrom.S_CODE, Tables.Currencies.CurrencyFrom.S_EXCHANGE_RATE,
                Tables.Transactions.ACCOUNT_TO_ID, Tables.Accounts.AccountTo.S_TITLE, Tables.Accounts.AccountTo.S_CURRENCY_ID, Tables.Currencies.CurrencyTo.S_CODE, Tables.Currencies.CurrencyTo.S_EXCHANGE_RATE,
                Tables.Transactions.CATEGORY_ID, Tables.Categories.CategoriesChild.S_TITLE, Tables.Categories.CategoriesChild.S_TYPE, Tables.Categories.CategoriesChild.S_COLOR};
        final String selection = null;
        final String[] selectionArgs = null;
        final String sortOrder = null;

        return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected void bindItem(Cursor c)
    {
        if (c != null && c.moveToFirst())
        {
            // Find indexes
            final int iDate = c.getColumnIndex(Tables.Transactions.DATE);
            final int iAccountFromTitle = c.getColumnIndex(Tables.Accounts.AccountFrom.TITLE);
            final int iAccountFromCurrencyId = c.getColumnIndex(Tables.Accounts.AccountFrom.CURRENCY_ID);
            final int iAccountFromCurrencyExchangeRate = c.getColumnIndex(Tables.Currencies.CurrencyFrom.EXCHANGE_RATE);
            final int iAccountToTitle = c.getColumnIndex(Tables.Accounts.AccountTo.TITLE);
            final int iAccountToCurrencyId = c.getColumnIndex(Tables.Accounts.AccountTo.CURRENCY_ID);
            final int iAccountToCurrencyExchangeRate = c.getColumnIndex(Tables.Currencies.CurrencyTo.EXCHANGE_RATE);
            final int iCategoryTitle = c.getColumnIndex(Tables.Categories.CategoriesChild.TITLE);
            final int iCategoryType = c.getColumnIndex(Tables.Categories.CategoriesChild.TYPE);
            final int iCategoryColor = c.getColumnIndex(Tables.Categories.CategoriesChild.COLOR);
            final int iAmount = c.getColumnIndex(Tables.Transactions.AMOUNT);
            final int iNote = c.getColumnIndex(Tables.Transactions.NOTE);
            final int iIncludeInReport = c.getColumnIndex(Tables.Transactions.SHOW_IN_TOTALS);
            final int iState = c.getColumnIndex(Tables.Transactions.STATE);
            final int iExchangeRate = c.getColumnIndex(Tables.Transactions.EXCHANGE_RATE);

            // Get values
            final long date = c.getLong(iDate);
            final String accountFromTitle = c.getString(iAccountFromTitle);
            final long accountFromCurrencyId = c.getLong(iAccountFromCurrencyId);
            final String accountToTitle = c.getString(iAccountToTitle);
            final long accountToCurrencyId = c.getLong(iAccountToCurrencyId);
            final String categoryTitle = c.getString(iCategoryTitle);
            final int categoryType = c.getInt(iCategoryType);
            final double amount = c.getDouble(iAmount);
            final String note = c.getString(iNote);
            final long mainCurrencyId = CurrenciesHelper.getDefault(getActivity()).getMainCurrencyId();
            final Drawable colorDrawable = makeColorDrawable(c.getInt(iState) == Tables.Transactions.State.CONFIRMED ? c.getInt(iCategoryColor) : getResources().getColor(R.color.f_light_darker3));

            // Set values
            date_TV.setText(DateUtils.formatDateTime(getActivity(), date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                color_V.setBackground(colorDrawable);
            else
                color_V.setBackgroundDrawable(colorDrawable);
            category_TV.setText(categoryTitle);
            note_TV.setText(note);
            note_TV.setVisibility(TextUtils.isEmpty(note) ? View.GONE : View.VISIBLE);
            notIncluded_TV.setVisibility(c.getInt(iIncludeInReport) != 0 ? View.GONE : View.VISIBLE);
            switch (categoryType)
            {
                case Tables.Categories.Type.EXPENSE:
                    account_TV.setText(accountFromTitle);
                    category_TV.setVisibility(View.VISIBLE);
                    amount_TV.setText(AmountUtils.formatAmount(getActivity(), accountFromCurrencyId, amount));
                    amount_TV.setTextColor(getResources().getColor(R.color.text_primary));
                    if (mainCurrencyId != accountFromCurrencyId)
                    {
                        mainCurrencyAmount_TV.setVisibility(View.VISIBLE);
                        mainCurrencyAmount_TV.setText(AmountUtils.formatAmount(getActivity(), mainCurrencyId, amount * c.getDouble(iAccountFromCurrencyExchangeRate)));
                        exchangeRate_TV.setText("\u21C4" + String.valueOf(c.getDouble(iAccountFromCurrencyExchangeRate)));
                    }
                    else
                    {
                        mainCurrencyAmount_TV.setVisibility(View.GONE);
                        exchangeRate_TV.setVisibility(View.GONE);
                    }
                    break;

                case Tables.Categories.Type.INCOME:
                    account_TV.setText(accountToTitle);
                    category_TV.setVisibility(View.VISIBLE);
                    amount_TV.setText(AmountUtils.formatAmount(getActivity(), accountToCurrencyId, amount));
                    amount_TV.setTextColor(getResources().getColor(R.color.text_green));
                    if (mainCurrencyId != accountToCurrencyId)
                    {
                        mainCurrencyAmount_TV.setVisibility(View.VISIBLE);
                        mainCurrencyAmount_TV.setText(AmountUtils.formatAmount(getActivity(), mainCurrencyId, amount * c.getDouble(iAccountToCurrencyExchangeRate)));
                        exchangeRate_TV.setText("\u21C4" + String.valueOf(c.getDouble(iAccountToCurrencyExchangeRate)));
                    }
                    else
                    {
                        mainCurrencyAmount_TV.setVisibility(View.GONE);
                        exchangeRate_TV.setVisibility(View.GONE);
                    }
                    break;

                case Tables.Categories.Type.TRANSFER:
                    account_TV.setText(accountFromTitle + " \u21E8 " + accountToTitle);
                    category_TV.setVisibility(View.GONE);
                    if (accountFromCurrencyId == accountToCurrencyId)
                    {
                        amount_TV.setText(AmountUtils.formatAmount(getActivity(), c.getLong(iAccountFromCurrencyId), amount));
                        mainCurrencyAmount_TV.setVisibility(View.GONE);
                        exchangeRate_TV.setVisibility(View.GONE);
                    }
                    else
                    {
                        amount_TV.setText(AmountUtils.formatAmount(getActivity(), c.getLong(iAccountFromCurrencyId), amount));
                        mainCurrencyAmount_TV.setVisibility(View.VISIBLE);
                        mainCurrencyAmount_TV.setText(AmountUtils.formatAmount(getActivity(), c.getLong(iAccountToCurrencyId), amount * c.getDouble(iExchangeRate)));
                        exchangeRate_TV.setText("\u21C4" + String.valueOf(c.getDouble(iExchangeRate)));
                    }
                    amount_TV.setTextColor(getResources().getColor(R.color.text_yellow));

                    break;
            }
        }
    }

    private Drawable makeColorDrawable(int color)
    {
        // Convert to HSV
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        // Decrease brightness
        hsv[2] *= 0.8;
        final int darkerColor = Color.HSVToColor(hsv);

        // Make drawable
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[]{color, darkerColor});
        drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        drawable.setGradientRadius(getActivity().getResources().getDisplayMetrics().widthPixels * 0.8f);
        return drawable;
    }
}