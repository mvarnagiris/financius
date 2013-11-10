package com.code44.finance.utils;

import android.content.ContentValues;
import com.code44.finance.db.Tables;
import com.code44.finance.db.Tables.DeleteState;
import com.code44.finance.db.Tables.SyncState;

public class BudgetsUtils
{
    public static final String TEMP_CATEGORY_IDS = "TEMP_CATEGORY_IDS";
    public static final String DELIMITER = ";";

    public static void prepareValues(ContentValues values, String title, String note, int period, double amount, boolean includeInTotalBudget, boolean showInOverview, long[] categoryIDs)
    {
        values.put(Tables.Budgets.TITLE, title);
        values.put(Tables.Budgets.NOTE, note);
        values.put(Tables.Budgets.PERIOD, period);
        values.put(Tables.Budgets.AMOUNT, amount);
        values.put(Tables.Budgets.INCLUDE_IN_TOTAL_BUDGET, includeInTotalBudget);
        values.put(Tables.Budgets.SHOW_IN_OVERVIEW, showInOverview);
        values.put(Tables.Budgets.TIMESTAMP, System.currentTimeMillis());
        values.put(Tables.Budgets.DELETE_STATE, DeleteState.NONE);
        values.put(Tables.Budgets.SYNC_STATE, SyncState.LOCAL_CHANGES);

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < categoryIDs.length; i++)
        {
            if (i > 0)
                sb.append(DELIMITER);
            sb.append(categoryIDs[i]);
        }
        values.put(TEMP_CATEGORY_IDS, sb.toString());
    }

    public static long[] parseLongIDs(String delimitedIDs, String delimiter)
    {
        final String[] split = delimitedIDs.split(delimiter);
        final long[] idArray = new long[split.length];
        for (int i = 0; i < split.length; i++)
            idArray[i] = Long.parseLong(split[i]);
        return idArray;
    }
}