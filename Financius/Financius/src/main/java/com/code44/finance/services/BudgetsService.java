package com.code44.finance.services;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.BudgetsProvider;
import com.code44.finance.utils.BudgetsUtils;
import com.code44.finance.utils.PeriodHelper;

public class BudgetsService extends AbstractItemService
{
    public static final String EXTRA_TITLE = BudgetsService.class.getName() + ".EXTRA_TITLE";
    public static final String EXTRA_NOTE = BudgetsService.class.getName() + ".EXTRA_NOTE";
    public static final String EXTRA_PERIOD = BudgetsService.class.getName() + ".EXTRA_PERIOD";
    public static final String EXTRA_AMOUNT = BudgetsService.class.getName() + ".EXTRA_AMOUNT";
    public static final String EXTRA_INCLUDE_IN_TOTAL_BUDGET = BudgetsService.class.getName() + ".EXTRA_INCLUDE_IN_TOTAL_BUDGET";
    public static final String EXTRA_SHOW_IN_OVERVIEW = BudgetsService.class.getName() + ".EXTRA_SHOW_IN_OVERVIEW";
    public static final String EXTRA_CATEGORY_IDS = BudgetsService.class.getName() + ".EXTRA_CATEGORY_IDS";

    @Override
    protected void notifyOnItemUpdated()
    {
        //NotifyUtils.onBudgetUpdated(this);
    }

    @Override
    protected void prepareValues(ContentValues outValues, Intent intent)
    {
        // Get values
        final String title = intent.getStringExtra(EXTRA_TITLE);
        final String note = intent.getStringExtra(EXTRA_NOTE);
        final int period = intent.getIntExtra(EXTRA_PERIOD, PeriodHelper.TYPE_MONTH);
        final double amount = intent.getDoubleExtra(EXTRA_AMOUNT, 0);
        final boolean includeInTotalBudget = intent.getBooleanExtra(EXTRA_INCLUDE_IN_TOTAL_BUDGET, true);
        final boolean showInOverview = intent.getBooleanExtra(EXTRA_SHOW_IN_OVERVIEW, false);
        final long[] categoryIDs = intent.getLongArrayExtra(EXTRA_CATEGORY_IDS);

        // Prepare
        BudgetsUtils.prepareValues(outValues, title, note, period, amount, includeInTotalBudget, showInOverview, categoryIDs);
    }

    @Override
    protected Uri getUriForItems()
    {
        return BudgetsProvider.uriBudgets();
    }

    @Override
    protected String getItemTable()
    {
        return Tables.Budgets.TABLE_NAME;
    }
}