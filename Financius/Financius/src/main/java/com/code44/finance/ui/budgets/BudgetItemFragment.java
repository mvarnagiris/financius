package com.code44.finance.ui.budgets;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.code44.finance.API;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.BudgetsProvider;
import com.code44.finance.ui.ItemFragment;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrenciesHelper;

public class BudgetItemFragment extends ItemFragment
{
    private TextView title_TV;
    private TextView amount_TV;
    private TextView categories_TV;
    private TextView note_TV;
    private TextView includeInTotalBudget_TV;
    private TextView showInOverview_TV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        title_TV = (TextView) view.findViewById(R.id.title_TV);
        amount_TV = (TextView) view.findViewById(R.id.amount_TV);
        categories_TV = (TextView) view.findViewById(R.id.categories_TV);
        note_TV = (TextView) view.findViewById(R.id.note_TV);
        includeInTotalBudget_TV = (TextView) view.findViewById(R.id.includeInTotalBudget_TV);
        showInOverview_TV = (TextView) view.findViewById(R.id.showInOverview_TV);
    }

    @Override
    protected void startItemEdit(Context context, long itemId, View expandFrom)
    {
        BudgetEditActivity.startItemEdit(context, itemId);
    }

    @Override
    protected boolean onDeleteItem(Context context, long[] itemIds)
    {
        API.deleteBudgets(context, itemIds);
        return true;
    }

    @Override
    protected Loader<Cursor> createItemLoader(Context context, long itemId)
    {
        final Uri uri = BudgetsProvider.uriBudget(context, itemId);
        final String[] projection = new String[]{Tables.Budgets.T_ID, Tables.Budgets.TITLE, Tables.Budgets.NOTE, Tables.Budgets.AMOUNT, Tables.Budgets.INCLUDE_IN_TOTAL_BUDGET, Tables.Budgets.SHOW_IN_OVERVIEW};
        final String selection = null;
        final String[] selectionArgs = null;
        final String sortOrder = null;

        return new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected void bindItem(Cursor c)
    {
        if (c != null && c.moveToFirst())
        {
            // Find indexes
            final int iTitle = c.getColumnIndex(Tables.Budgets.TITLE);
            final int iAmount = c.getColumnIndex(Tables.Budgets.AMOUNT);
            final int iNote = c.getColumnIndex(Tables.Budgets.NOTE);
            final int iIncludeInTotalBudget = c.getColumnIndex(Tables.Budgets.INCLUDE_IN_TOTAL_BUDGET);
            final int iShowInOverview = c.getColumnIndex(Tables.Budgets.SHOW_IN_OVERVIEW);

            // Set values
            final String checkMark = "\u2714";
            final String crossMark = "\u2717";
            final String note = c.getString(iNote);
            title_TV.setText(c.getString(iTitle));
            amount_TV.setText(AmountUtils.formatAmount(getActivity(), CurrenciesHelper.getDefault(getActivity()).getMainCurrencyId(), c.getDouble(iAmount)));
            if (TextUtils.isEmpty(note))
            {
                note_TV.setVisibility(View.GONE);
            }
            else
            {
                note_TV.setVisibility(View.VISIBLE);
                note_TV.setText(note);
            }
            includeInTotalBudget_TV.setText(c.getInt(iIncludeInTotalBudget) != 0 ? checkMark : crossMark);
            showInOverview_TV.setText(c.getInt(iShowInOverview) != 0 ? checkMark : crossMark);
        }
    }
}
