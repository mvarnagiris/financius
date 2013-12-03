package com.code44.finance.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import com.code44.finance.FinanciusApp;
import com.code44.finance.db.Tables;
import com.code44.finance.utils.BudgetsUtils;

public class BudgetsProvider extends AbstractItemsProvider
{
    public static Uri uriBudgets()
    {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(FinanciusApp.getAppContext(), BudgetsProvider.class) + "/" + Tables.Budgets.TABLE_NAME);
    }

    public static Uri uriBudget(long budgetId)
    {
        return ContentUris.withAppendedId(uriBudgets(), budgetId);
    }

    @Override
    protected String getItemTable()
    {
        return Tables.Budgets.TABLE_NAME;
    }

    @Override
    protected String getJoinedTables()
    {
        return " left join " + Tables.BudgetCategories.TABLE_NAME + " on " + Tables.BudgetCategories.BUDGET_ID + "=" + Tables.Budgets.T_ID
                + " left join " + Tables.Transactions.TABLE_NAME + " on " + Tables.Transactions.CATEGORY_ID + "=" + Tables.BudgetCategories.CATEGORY_ID
                + " left join " + Tables.Accounts.TABLE_NAME + " on " + Tables.Accounts.T_ID + "=" + Tables.Transactions.ACCOUNT_FROM_ID
                + " left join " + Tables.Currencies.TABLE_NAME + " on " + Tables.Currencies.T_ID + "=" + Tables.Accounts.CURRENCY_ID;
    }

    @Override
    protected Object onBeforeInsert(Uri uri, ContentValues values)
    {
        String delimitedIDs = values.getAsString(BudgetsUtils.TEMP_CATEGORY_IDS);
        values.remove(BudgetsUtils.TEMP_CATEGORY_IDS);

        return BudgetsUtils.parseLongIDs(delimitedIDs, BudgetsUtils.DELIMITER);
    }

    @Override
    protected void onAfterInsert(Uri uri, ContentValues values, long newId, Object objectFromBefore)
    {
        final long[] categoryIDs = (long[]) objectFromBefore;

        final ContentValues tempValues = new ContentValues();
        for (int i = 0; i < categoryIDs.length; i++)
        {
            tempValues.clear();
            tempValues.put(Tables.BudgetCategories.BUDGET_ID, newId);
            tempValues.put(Tables.BudgetCategories.CATEGORY_ID, categoryIDs[i]);
            db.insert(Tables.BudgetCategories.TABLE_NAME, null, tempValues);
        }
    }

    @Override
    protected Object onBeforeUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        String delimitedIDs = values.getAsString(BudgetsUtils.TEMP_CATEGORY_IDS);
        values.remove(BudgetsUtils.TEMP_CATEGORY_IDS);

        return BudgetsUtils.parseLongIDs(delimitedIDs, BudgetsUtils.DELIMITER);
    }

    @Override
    protected void onAfterUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {
        final long[] categoryIDs = (long[]) objectFromBefore;

        final long budgetId = values.getAsLong(Tables.Budgets.ID);
        db.delete(Tables.BudgetCategories.TABLE_NAME, Tables.BudgetCategories.BUDGET_ID + "=?", new String[]{String.valueOf(budgetId)});
        final ContentValues tempValues = new ContentValues();
        for (int i = 0; i < categoryIDs.length; i++)
        {
            tempValues.clear();
            tempValues.put(Tables.BudgetCategories.BUDGET_ID, budgetId);
            tempValues.put(Tables.BudgetCategories.CATEGORY_ID, categoryIDs[i]);
            db.insert(Tables.BudgetCategories.TABLE_NAME, null, tempValues);
        }
    }

    @Override
    protected Object onBeforeDelete(Uri uri, String selection, String[] selectionArgs)
    {
        return null;
    }

    @Override
    protected void onAfterDelete(Uri uri, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {

    }

    @Override
    protected Object onBeforeBulkInsert(Uri uri, ContentValues[] valuesArray)
    {
        return null;
    }

    @Override
    protected void onAfterBulkInsert(Uri uri, ContentValues[] valuesArray, Object objectFromBefore)
    {

    }

    @Override
    protected void checkValues(ContentValues values, int operation)
    {

    }
}
