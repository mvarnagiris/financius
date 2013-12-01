package com.code44.finance.services;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.BaseColumns;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.AbstractItemsProvider;

import java.util.UUID;

public abstract class AbstractItemService extends AbstractService
{
    public static final String EXTRA_ITEM_ID = AbstractItemService.class.getName() + ".EXTRA_ITEM_ID";
    public static final String EXTRA_ITEM_IDS = AbstractItemService.class.getName() + ".EXTRA_ITEM_IDS";
    public static final String EXTRA_CONTENT_VALUES = AbstractItemService.class.getName() + ".EXTRA_CONTENT_VALUES";
    public static final int RT_CREATE_ITEM = 1001;
    public static final int RT_UPDATE_ITEM = 1002;
    public static final int RT_DELETE_ITEMS = 1003;

    protected abstract void notifyOnItemUpdated();

    protected abstract void prepareValues(ContentValues outValues, Intent intent);

    protected abstract Uri getUriForItems();

    protected abstract String getItemTable();

    @Override
    protected void handleRequest(Intent intent, int requestType, long startTime, long lastSuccessfulWorkTime) throws Exception
    {
        switch (requestType)
        {
            case RT_CREATE_ITEM:
                rtCreateItem(intent);
                break;

            case RT_UPDATE_ITEM:
                rtUpdateItem(intent);
                break;

            case RT_DELETE_ITEMS:
                rtDeleteItems(intent);
                break;
        }
    }

    @Override
    protected boolean checkForNetwork(Intent intent, int requestType)
    {
        return false;
    }

    private void rtCreateItem(Intent intent) throws Exception
    {
        // Store values
        final ContentValues values = new ContentValues();
        prepareValues(values, intent);
        values.put(getItemTable() + "_" + Tables.SERVER_ID_SUFFIX, UUID.randomUUID().toString());
        getContentResolver().insert(getUriForItems(), values);

        // Notify
        notifyOnItemUpdated();
    }

    private void rtUpdateItem(Intent intent) throws Exception
    {
        // Get values
        final long itemId = intent.getLongExtra(EXTRA_ITEM_ID, 0);

        // Store values
        final ContentValues values = new ContentValues();
        prepareValues(values, intent);
        values.put(BaseColumns._ID, itemId);
        getContentResolver().update(getUriForItems(), values, getItemTable() + "." + BaseColumns._ID + "=?", new String[]{String.valueOf(itemId)});

        // Notify
        notifyOnItemUpdated();
    }

    private void rtDeleteItems(Intent intent) throws Exception
    {
        // Get values
        final long[] itemIDs = intent.getLongArrayExtra(EXTRA_ITEM_IDS);

        // Prepare IN clause
        final AbstractItemsProvider.InClause inClause = AbstractItemsProvider.InClause.getInClause(itemIDs, getItemTable() + "." + BaseColumns._ID);

        // Delete items
        getContentResolver().delete(getUriForItems(), inClause.getSelection(), inClause.getSelectionArgs());

        // Notify
        notifyOnItemUpdated();
    }
}