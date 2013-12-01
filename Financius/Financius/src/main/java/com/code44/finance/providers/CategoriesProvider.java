package com.code44.finance.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import com.code44.finance.db.Tables;

import java.util.ArrayList;
import java.util.List;

public class CategoriesProvider extends AbstractItemsProvider
{

    public static Uri uriCategories(Context context)
    {
        return getContentUri(context);
    }

    public static Uri uriCategory(Context context, long categoryId)
    {
        return ContentUris.withAppendedId(uriCategories(context), categoryId);
    }

    protected static Uri getContentUri(Context context)
    {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(context, CategoriesProvider.class) + "/" + Tables.Categories.TABLE_NAME);
    }

    @Override
    protected String getItemTable()
    {
        return Tables.Categories.TABLE_NAME;
    }

    @Override
    protected Object onBeforeInsert(Uri uri, ContentValues values)
    {
        return null;
    }

    @Override
    protected void onAfterInsert(Uri uri, ContentValues values, long newId, Object objectFromBefore)
    {

    }

    @Override
    protected Object onBeforeUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        // If this is main category, update color to all sub-categories
        if (values.getAsInteger(Tables.Categories.LEVEL) == 1)
        {
            ContentValues tempValues = new ContentValues();
            tempValues.put(Tables.Categories.COLOR, values.getAsInteger(Tables.Categories.COLOR));
            db.update(Tables.Categories.TABLE_NAME, tempValues, Tables.Categories.PARENT_ID + "=?", new String[]{String.valueOf(values.getAsLong(BaseColumns._ID))});
        }

        return null;
    }

    @Override
    protected void onAfterUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {

    }

    @Override
    protected Object onBeforeDelete(Uri uri, String selection, String[] selectionArgs)
    {
        return getItemIDs(selection, selectionArgs);
    }

    @Override
    protected void onAfterDelete(Uri uri, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {
        // Find all subcategories for deleted categories
        final List<Long> itemIDs = (List<Long>) objectFromBefore;
        final List<Long> subcategoriesIDs = new ArrayList<Long>();
        InClause inClause = InClause.getInClause(itemIDs, Tables.Categories.PARENT_ID);
        Cursor c = null;
        try
        {
            c = getContext().getContentResolver().query(uriCategories(getContext()), new String[]{Tables.Categories.T_ID}, inClause.getSelection(), inClause.getSelectionArgs(), null);
            if (c != null && c.moveToFirst())
            {
                Long id;
                do
                {
                    id = c.getLong(0);
                    if (!itemIDs.contains(id))
                        itemIDs.add(id);
                    if (!subcategoriesIDs.contains(id))
                        subcategoriesIDs.add(id);
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        // Delete subcategories
        if (subcategoriesIDs.size() > 0)
        {
            inClause = InClause.getInClause(subcategoriesIDs, Tables.Categories.ID);
            getContext().getContentResolver().delete(uriCategories(getContext()), inClause.getSelection(), inClause.getSelectionArgs());
        }

        // Delete Transactions
        if (itemIDs != null && itemIDs.size() > 0)
        {
            inClause = InClause.getInClause(itemIDs, Tables.Transactions.CATEGORY_ID);
            getContext().getContentResolver().delete(TransactionsProvider.uriTransactions(getContext()), inClause.getSelection(), inClause.getSelectionArgs());
        }
    }
}