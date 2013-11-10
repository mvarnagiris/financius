package com.code44.finance.ui.budgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.code44.finance.API;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.AbstractItemsProvider;
import com.code44.finance.providers.BudgetsProvider;
import com.code44.finance.providers.CategoriesProvider;
import com.code44.finance.ui.ItemEditFragment;
import com.code44.finance.ui.categories.CategoryListActivity;
import com.code44.finance.ui.categories.CategoryListFragment;
import com.code44.finance.ui.transactions.CalculatorActivity;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.AnimUtils;
import com.code44.finance.utils.BudgetsUtils;
import com.code44.finance.utils.PeriodHelper;

@SuppressWarnings("ConstantConditions")
public class BudgetEditFragment extends ItemEditFragment implements View.OnClickListener
{
    private static final int REQUEST_AMOUNT = 1;
    private static final int REQUEST_CATEGORIES = 2;
    // -----------------------------------------------------------------------------------------------------------------
    private static final String STATE_TITLE = "STATE_TITLE";
    private static final String STATE_AMOUNT = "STATE_AMOUNT";
    private static final String STATE_CATEGORIES = "STATE_CATEGORIES";
    private static final String STATE_INCLUDE_IN_TOTAL_BUDGET = "STATE_INCLUDE_IN_TOTAL_BUDGET";
    private static final String STATE_SHOW_IN_OVERVIEW = "STATE_SHOW_IN_OVERVIEW";
    private static final String STATE_NOTE = "STATE_NOTE";
    // -----------------------------------------------------------------------------------------------------------------
    private static final int LOADER_CATEGORIES = 1;
    // -----------------------------------------------------------------------------------------------------------------
    private EditText title_ET;
    private Button amount_B;
    private Button categories_B;
    private CheckBox includeInTotalBudget_CB;
    private CheckBox showInOverview_CB;
    private EditText note_ET;
    // -----------------------------------------------------------------------------------------------------------------
    private long[] categoryIDs;

    public static BudgetEditFragment newInstance(long itemId)
    {
        final Bundle args = makeArgs(itemId);

        BudgetEditFragment f = new BudgetEditFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_budget_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        title_ET = (EditText) view.findViewById(R.id.title_ET);
        amount_B = (Button) view.findViewById(R.id.amount_B);
        categories_B = (Button) view.findViewById(R.id.categories_B);
        includeInTotalBudget_CB = (CheckBox) view.findViewById(R.id.includeInTotalBudget_CB);
        showInOverview_CB = (CheckBox) view.findViewById(R.id.showInOverview_CB);
        note_ET = (EditText) view.findViewById(R.id.note_ET);

        // Setup
        amount_B.setOnClickListener(this);
        categories_B.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_TITLE, getTitle());
        outState.putDouble(STATE_AMOUNT, getAmount());
        outState.putLongArray(STATE_CATEGORIES, getCategories());
        outState.putBoolean(STATE_INCLUDE_IN_TOTAL_BUDGET, isIncludeInTotalBudget());
        outState.putBoolean(STATE_SHOW_IN_OVERVIEW, isShowInOverview());
        outState.putString(STATE_NOTE, getNote());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {
                case REQUEST_AMOUNT:
                    setAmount(data.getDoubleExtra(CalculatorActivity.RESULT_EXTRA_AMOUNT, 0));
                    break;

                case REQUEST_CATEGORIES:
                    setCategories(data.getLongArrayExtra(CategoryListFragment.RESULT_EXTRA_ITEM_IDS));
                    break;
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
        switch (id)
        {
            case LOADER_CATEGORIES:
            {
                final long[] idArray = getCategories();
                final AbstractItemsProvider.InClause inClause = AbstractItemsProvider.InClause.getInClause(idArray != null && idArray.length > 0 ? idArray : new long[]{0}, Tables.Categories.T_ID);
                final Uri uri = CategoriesProvider.uriCategories(getActivity());
                final String[] projection = new String[]{Tables.Categories.TITLE, Tables.Categories.COLOR};
                final String selection = inClause.getSelection();
                final String[] selectionArgs = inClause.getSelectionArgs();
                final String sortOrder = null;

                return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
            }

        }
        return super.onCreateLoader(id, bundle);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        switch (cursorLoader.getId())
        {
            case LOADER_CATEGORIES:
            {
                bindCategories(cursor);
                break;
            }
        }
        super.onLoadFinished(cursorLoader, cursor);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.categories_B:
                CategoryListActivity.startListMultiSelection(getActivity(), this, REQUEST_CATEGORIES, Tables.Categories.Type.EXPENSE, getCategories());
                break;

            case R.id.amount_B:
                CalculatorActivity.startCalculator(this, REQUEST_AMOUNT, getAmount(), false, true);
                break;
        }
    }

    @Override
    protected boolean bindItem(Cursor c, boolean isDataLoaded)
    {
        if (!isDataLoaded && c != null && c.moveToFirst())
        {
            // Set item data.
            setTitle(c.getString(c.getColumnIndex(Tables.Budgets.TITLE)));
            setAmount(c.getDouble(c.getColumnIndex(Tables.Budgets.AMOUNT)));
            setCategories(BudgetsUtils.parseLongIDs(c.getString(c.getColumnIndex(Tables.Budgets.CATEGORIES)), ","));
            setIncludeInTotalBudget(c.getInt(c.getColumnIndex(Tables.Budgets.INCLUDE_IN_TOTAL_BUDGET)) != 0);
            setShowInOverview(c.getInt(c.getColumnIndex(Tables.Budgets.SHOW_IN_OVERVIEW)) != 0);
            setNote(c.getString(c.getColumnIndex(Tables.Budgets.NOTE)));
            return true;
        }

        return isDataLoaded;
    }

    @Override
    protected void restoreOrInit(long itemId, Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            // Restore state
            setTitle(savedInstanceState.getString(STATE_TITLE));
            setAmount(savedInstanceState.getDouble(STATE_AMOUNT));
            setCategories(savedInstanceState.getLongArray(STATE_CATEGORIES));
            setIncludeInTotalBudget(savedInstanceState.getBoolean(STATE_INCLUDE_IN_TOTAL_BUDGET));
            setShowInOverview(savedInstanceState.getBoolean(STATE_SHOW_IN_OVERVIEW));
            setNote(savedInstanceState.getString(STATE_NOTE));
        }
        else if (itemId == 0)
        {
            // Init item creation
            setTitle(null);
            setAmount(0);
            setCategories(null);
            setIncludeInTotalBudget(true);
            setShowInOverview(false);
            setNote(null);
        }
    }

    @Override
    protected boolean onSave(Context context, long itemId)
    {
        boolean isOK = true;

        if (TextUtils.isEmpty(getTitle()))
        {
            isOK = false;
            AnimUtils.shake(title_ET);
        }

        if (getAmount() <= 0)
        {
            isOK = false;
            AnimUtils.shake(amount_B);
        }

        if (getCategories() == null || getCategories().length == 0)
        {
            isOK = false;
            AnimUtils.shake(categories_B);
        }

        if (isOK)
        {
            if (itemId == 0)
                API.createBudget(context, getTitle(), getNote(), PeriodHelper.TYPE_MONTH, getAmount(), getCategories(), isIncludeInTotalBudget(), isShowInOverview());
            else
                API.updateBudget(context, itemId, getTitle(), getNote(), PeriodHelper.TYPE_MONTH, getAmount(), getCategories(), isIncludeInTotalBudget(), isShowInOverview());
        }

        return isOK;
    }

    @Override
    protected boolean onDiscard()
    {
        return true;
    }

    @Override
    protected Loader<Cursor> createItemLoader(Context context, long itemId)
    {
        final Uri uri = BudgetsProvider.uriBudget(getActivity(), itemId);
        final String[] projection = new String[]{Tables.Budgets.T_ID, Tables.Budgets.TITLE, Tables.Budgets.NOTE, Tables.Budgets.AMOUNT, Tables.Budgets.INCLUDE_IN_TOTAL_BUDGET, Tables.Budgets.SHOW_IN_OVERVIEW, Tables.Budgets.S_CATEGORIES};
        final String selection = "1)" +
                " GROUP BY (" + Tables.Budgets.T_ID;
        final String[] selectionArgs = null;
        final String sortOrder = null;

        return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
    }

    private void bindCategories(Cursor c)
    {
        final SpannableStringBuilder ssb = new SpannableStringBuilder();
        if (c != null && c.moveToFirst())
        {
            final int iTitle = c.getColumnIndex(Tables.Categories.TITLE);
            final int iColor = c.getColumnIndex(Tables.Categories.COLOR);

            String title;
            do
            {
                if (ssb.length() > 0)
                    ssb.append(", ");

                title = c.getString(iTitle);
                ssb.append(title);
                ssb.setSpan(new ForegroundColorSpan(c.getInt(iColor)), ssb.length() - title.length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            while (c.moveToNext());

            ssb.setSpan(new TypefaceSpan("sans-serif"), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        categories_B.setText(ssb);
    }

    private String getTitle()
    {
        return title_ET.getText().toString();
    }

    private void setTitle(String title)
    {
        title_ET.setText(title);
    }

    private double getAmount()
    {
        return AmountUtils.getAmount(amount_B.getText().toString());
    }

    private void setAmount(double amount)
    {
        if (amount == 0)
            amount_B.setText(null);
        else
            amount_B.setText(AmountUtils.formatAmount(amount));
    }

    private long[] getCategories()
    {
        return categoryIDs;
    }

    private void setCategories(long[] categoryIDs)
    {
        this.categoryIDs = categoryIDs;
        getLoaderManager().restartLoader(LOADER_CATEGORIES, null, this);
    }

    private boolean isIncludeInTotalBudget()
    {
        return includeInTotalBudget_CB.isChecked();
    }

    private void setIncludeInTotalBudget(boolean include)
    {
        includeInTotalBudget_CB.setChecked(include);
    }

    private boolean isShowInOverview()
    {
        return showInOverview_CB.isChecked();
    }

    private void setShowInOverview(boolean show)
    {
        showInOverview_CB.setChecked(show);
    }

    private String getNote()
    {
        return note_ET.getText().toString();
    }

    private void setNote(String note)
    {
        note_ET.setText(note);
    }
}