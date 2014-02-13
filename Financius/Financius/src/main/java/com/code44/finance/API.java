package com.code44.finance;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.code44.finance.services.BackupService;
import com.code44.finance.services.CategoriesService;
import com.code44.finance.services.CurrenciesRestService;
import com.code44.finance.utils.DataHelper;

public class API
{
    // Items
    // -----------------------------------------------------------------------------------------------------------------

    public static void createItem(Uri uri, ContentValues values, DataHelper.ValuesUpdater valuesUpdater)
    {
        DataHelper.create(uri, values, valuesUpdater);
    }

    public static void createItem(Uri uri, ContentValues values)
    {
        DataHelper.create(uri, values);
    }

    public static void updateItem(Uri uri, long itemId, ContentValues values, DataHelper.ValuesUpdater valuesUpdater)
    {
        DataHelper.update(uri, itemId, values, valuesUpdater);
    }

    public static void updateItem(Uri uri, long itemId, ContentValues values)
    {
        DataHelper.update(uri, itemId, values);
    }

    public static void deleteItems(Uri uri, long[] itemIDs)
    {
        DataHelper.delete(uri, itemIDs);
    }

    // Currencies
    // -----------------------------------------------------------------------------------------------------------------

    public static void getExchangeRate(Context context, String code)
    {
        Intent intent = new Intent(context, CurrenciesRestService.class);
        intent.putExtra(CurrenciesRestService.EXTRA_REQUEST_TYPE, CurrenciesRestService.RT_GET_EXCHANGE_RATE_TO_MAIN);
        intent.putExtra(CurrenciesRestService.EXTRA_FORCE, true);
        intent.putExtra(CurrenciesRestService.EXTRA_FROM_CODE, code);
        context.startService(intent);
    }

    public static void getExchangeRate(Context context, String fromCode, String toCode)
    {
        Intent intent = new Intent(context, CurrenciesRestService.class);
        intent.putExtra(CurrenciesRestService.EXTRA_REQUEST_TYPE, CurrenciesRestService.RT_GET_EXCHANGE_RATE);
        intent.putExtra(CurrenciesRestService.EXTRA_FORCE, true);
        intent.putExtra(CurrenciesRestService.EXTRA_FROM_CODE, fromCode);
        intent.putExtra(CurrenciesRestService.EXTRA_TO_CODE, toCode);
        context.startService(intent);
    }

    public static void updateExchangeRates(Context context, boolean onlyUsed)
    {
        Intent intent = new Intent(context, CurrenciesRestService.class);
        intent.putExtra(CurrenciesRestService.EXTRA_REQUEST_TYPE, CurrenciesRestService.RT_UPDATE_EXCHANGE_RATES);
        intent.putExtra(CurrenciesRestService.EXTRA_FORCE, true);
        intent.putExtra(CurrenciesRestService.EXTRA_ONLY_USED, onlyUsed);
        context.startService(intent);
    }

    // Categories
    // -----------------------------------------------------------------------------------------------------------------

    public static void swapCategories(Context context, int swapFrom, int swapTo, int categoryType)
    {
        Intent intent = new Intent(context, CategoriesService.class);
        intent.putExtra(CategoriesService.EXTRA_REQUEST_TYPE, CategoriesService.RT_SWAP_ORDER);
        intent.putExtra(CategoriesService.EXTRA_FORCE, true);
        intent.putExtra(CategoriesService.EXTRA_SWAP_FROM, swapFrom);
        intent.putExtra(CategoriesService.EXTRA_SWAP_TO, swapTo);
        intent.putExtra(CategoriesService.EXTRA_TYPE, categoryType);
        context.startService(intent);
    }

    // Backup
    // --------------------------------------------------------------------------------------------------------------------------------

    public static void exportCSV(Context context, long dateFrom, long dateTo)
    {
        Intent intent = new Intent(context, BackupService.class);
        intent.putExtra(BackupService.EXTRA_REQUEST_TYPE, BackupService.RT_EXPORT_CSV);
        intent.putExtra(BackupService.EXTRA_FORCE, true);
        intent.putExtra(BackupService.EXTRA_DATE_FROM, dateFrom);
        intent.putExtra(BackupService.EXTRA_DATE_TO, dateTo);
        context.startService(intent);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static void exportJSON(Context context)
    {
        Intent intent = new Intent(context, BackupService.class);
        intent.putExtra(BackupService.EXTRA_REQUEST_TYPE, BackupService.RT_EXPORT_JSON);
        intent.putExtra(BackupService.EXTRA_FORCE, true);
        context.startService(intent);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static void importJSON(Context context, String filePath)
    {
        Intent intent = new Intent(context, BackupService.class);
        intent.putExtra(BackupService.EXTRA_REQUEST_TYPE, BackupService.RT_IMPORT_JSON);
        intent.putExtra(BackupService.EXTRA_FORCE, true);
        intent.putExtra(BackupService.EXTRA_FILE_PATH, filePath);
        context.startService(intent);
    }
}