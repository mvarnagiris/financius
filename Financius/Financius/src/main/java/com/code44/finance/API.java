package com.code44.finance;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.providers.CurrenciesProvider;
import com.code44.finance.services.*;
import com.code44.finance.utils.DataHelper;
import com.code44.finance.utils.NotifyUtils;

public class API
{
    // Currencies
    // --------------------------------------------------------------------------------------------------------------------------------

    public static void createCurrency(ContentValues values)
    {
        DataHelper.create(Tables.Currencies.TABLE_NAME, CurrenciesProvider.uriCurrencies(FinanciusApp.getAppContext()), values, NotifyUtils.getCurrencyUpdatedURIs());
    }

    public static void updateCurrency(Context context, long itemId, String code, String symbol, int decimals, String groupSeparator, String decimalSeparator, boolean isDefault, String symbolFormat, double exchangeRate)
    {
        Intent intent = new Intent(context, CurrenciesService.class);
        intent.putExtra(CurrenciesService.EXTRA_REQUEST_TYPE, CurrenciesService.RT_UPDATE_ITEM);
        intent.putExtra(CurrenciesService.EXTRA_FORCE, true);
        intent.putExtra(CurrenciesService.EXTRA_ITEM_ID, itemId);
        intent.putExtra(CurrenciesService.EXTRA_CODE, code);
        intent.putExtra(CurrenciesService.EXTRA_SYMBOL, symbol);
        intent.putExtra(CurrenciesService.EXTRA_DECIMALS, decimals);
        intent.putExtra(CurrenciesService.EXTRA_GROUP_SEPARATOR, groupSeparator);
        intent.putExtra(CurrenciesService.EXTRA_DECIMAL_SEPARATOR, decimalSeparator);
        intent.putExtra(CurrenciesService.EXTRA_IS_DEFAULT, isDefault);
        intent.putExtra(CurrenciesService.EXTRA_SYMBOL_FORMAT, symbolFormat);
        intent.putExtra(CurrenciesService.EXTRA_EXCHANGE_RATE, exchangeRate);
        context.startService(intent);
    }

    public static void deleteCurrencies(Context context, long[] itemIDs)
    {
        Intent intent = new Intent(context, CurrenciesService.class);
        intent.putExtra(CurrenciesService.EXTRA_REQUEST_TYPE, CurrenciesService.RT_DELETE_ITEMS);
        intent.putExtra(CurrenciesService.EXTRA_FORCE, true);
        intent.putExtra(CurrenciesService.EXTRA_ITEM_IDS, itemIDs);
        context.startService(intent);
    }

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

    // Accounts
    // --------------------------------------------------------------------------------------------------------------------------------

    public static void createAccount(ContentValues values)
    {
        checkId(values, Tables.Accounts.CURRENCY_ID);
        checkString(values, Tables.Accounts.TITLE);

        DataHelper.create(Tables.Accounts.TABLE_NAME, AccountsProvider.uriAccounts(FinanciusApp.getAppContext()), values, NotifyUtils.getAccountUpdatedURIs());
    }

    public static void updateAccount(long itemId, ContentValues values)
    {
        DataHelper.update(Tables.Accounts.TABLE_NAME, AccountsProvider.uriAccounts(FinanciusApp.getAppContext()), itemId, values, NotifyUtils.getAccountUpdatedURIs());
    }

    public static void deleteAccounts(long[] itemIDs)
    {
        DataHelper.delete(Tables.Accounts.TABLE_NAME, AccountsProvider.uriAccounts(FinanciusApp.getAppContext()), itemIDs, NotifyUtils.getAccountUpdatedURIs());
    }

    // Categories
    // --------------------------------------------------------------------------------------------------------------------------------

    public static void createCategory(Context context, long parentId, String title, int level, int type, int color)
    {
        Intent intent = new Intent(context, CategoriesService.class);
        intent.putExtra(CategoriesService.EXTRA_REQUEST_TYPE, CategoriesService.RT_CREATE_ITEM);
        intent.putExtra(CategoriesService.EXTRA_FORCE, true);
        intent.putExtra(CategoriesService.EXTRA_PARENT_ID, parentId);
        intent.putExtra(CategoriesService.EXTRA_TITLE, title);
        intent.putExtra(CategoriesService.EXTRA_LEVEL, level);
        intent.putExtra(CategoriesService.EXTRA_TYPE, type);
        intent.putExtra(CategoriesService.EXTRA_COLOR, color);
        context.startService(intent);
    }

    public static void updateCategory(Context context, long itemId, long parentId, String title, int level, int type, int color)
    {
        Intent intent = new Intent(context, CategoriesService.class);
        intent.putExtra(CategoriesService.EXTRA_REQUEST_TYPE, CategoriesService.RT_UPDATE_ITEM);
        intent.putExtra(CategoriesService.EXTRA_FORCE, true);
        intent.putExtra(CategoriesService.EXTRA_ITEM_ID, itemId);
        intent.putExtra(CategoriesService.EXTRA_PARENT_ID, parentId);
        intent.putExtra(CategoriesService.EXTRA_TITLE, title);
        intent.putExtra(CategoriesService.EXTRA_LEVEL, level);
        intent.putExtra(CategoriesService.EXTRA_TYPE, type);
        intent.putExtra(CategoriesService.EXTRA_COLOR, color);
        context.startService(intent);
    }

    public static void deleteCategories(Context context, long[] itemIDs)
    {
        Intent intent = new Intent(context, CategoriesService.class);
        intent.putExtra(CategoriesService.EXTRA_REQUEST_TYPE, CategoriesService.RT_DELETE_ITEMS);
        intent.putExtra(CategoriesService.EXTRA_FORCE, true);
        intent.putExtra(CategoriesService.EXTRA_ITEM_IDS, itemIDs);
        context.startService(intent);
    }

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

    // Transactions
    // --------------------------------------------------------------------------------------------------------------------------------

    public static void createTransaction(Context context, long accountFromId, long accountToId, long categoryId, long date, double amount, double exchangeRate, String note, int state, boolean showInTotals)
    {
        Intent intent = new Intent(context, TransactionsService.class);
        intent.putExtra(TransactionsService.EXTRA_REQUEST_TYPE, TransactionsService.RT_CREATE_ITEM);
        intent.putExtra(TransactionsService.EXTRA_FORCE, true);
        intent.putExtra(TransactionsService.EXTRA_ACCOUNT_FROM_ID, accountFromId);
        intent.putExtra(TransactionsService.EXTRA_ACCOUNT_TO_ID, accountToId);
        intent.putExtra(TransactionsService.EXTRA_CATEGORY_ID, categoryId);
        intent.putExtra(TransactionsService.EXTRA_DATE, date);
        intent.putExtra(TransactionsService.EXTRA_AMOUNT, amount);
        intent.putExtra(TransactionsService.EXTRA_EXCHANGE_RATE, exchangeRate);
        intent.putExtra(TransactionsService.EXTRA_NOTE, note);
        intent.putExtra(TransactionsService.EXTRA_STATE, state);
        intent.putExtra(TransactionsService.EXTRA_SHOW_IN_TOTALS, showInTotals);
        context.startService(intent);
    }

    public static void updateTransaction(Context context, long itemId, long accountFromId, long accountToId, long categoryId, long date, double amount, double exchangeRate, String note, int state, boolean showInTotals)
    {
        Intent intent = new Intent(context, TransactionsService.class);
        intent.putExtra(TransactionsService.EXTRA_REQUEST_TYPE, TransactionsService.RT_UPDATE_ITEM);
        intent.putExtra(TransactionsService.EXTRA_FORCE, true);
        intent.putExtra(TransactionsService.EXTRA_ITEM_ID, itemId);
        intent.putExtra(TransactionsService.EXTRA_ACCOUNT_FROM_ID, accountFromId);
        intent.putExtra(TransactionsService.EXTRA_ACCOUNT_TO_ID, accountToId);
        intent.putExtra(TransactionsService.EXTRA_CATEGORY_ID, categoryId);
        intent.putExtra(TransactionsService.EXTRA_DATE, date);
        intent.putExtra(TransactionsService.EXTRA_AMOUNT, amount);
        intent.putExtra(TransactionsService.EXTRA_EXCHANGE_RATE, exchangeRate);
        intent.putExtra(TransactionsService.EXTRA_NOTE, note);
        intent.putExtra(TransactionsService.EXTRA_STATE, state);
        intent.putExtra(TransactionsService.EXTRA_SHOW_IN_TOTALS, showInTotals);
        context.startService(intent);
    }

    public static void deleteTransactions(Context context, long[] itemIDs)
    {
        Intent intent = new Intent(context, TransactionsService.class);
        intent.putExtra(TransactionsService.EXTRA_REQUEST_TYPE, TransactionsService.RT_DELETE_ITEMS);
        intent.putExtra(TransactionsService.EXTRA_FORCE, true);
        intent.putExtra(TransactionsService.EXTRA_ITEM_IDS, itemIDs);
        context.startService(intent);
    }

    // Budgets
    // --------------------------------------------------------------------------------------------------------------------------------

    public static void createBudget(Context context, String title, String note, int period, double amount, long[] categoryIDs, boolean includeInTotalBudget, boolean showInOverview)
    {
        Intent intent = new Intent(context, BudgetsService.class);
        intent.putExtra(BudgetsService.EXTRA_REQUEST_TYPE, BudgetsService.RT_CREATE_ITEM);
        intent.putExtra(BudgetsService.EXTRA_FORCE, true);
        intent.putExtra(BudgetsService.EXTRA_TITLE, title);
        intent.putExtra(BudgetsService.EXTRA_NOTE, note);
        intent.putExtra(BudgetsService.EXTRA_PERIOD, period);
        intent.putExtra(BudgetsService.EXTRA_AMOUNT, amount);
        intent.putExtra(BudgetsService.EXTRA_CATEGORY_IDS, categoryIDs);
        intent.putExtra(BudgetsService.EXTRA_INCLUDE_IN_TOTAL_BUDGET, includeInTotalBudget);
        intent.putExtra(BudgetsService.EXTRA_SHOW_IN_OVERVIEW, showInOverview);
        context.startService(intent);
    }

    public static void updateBudget(Context context, long itemId, String title, String note, int period, double amount, long[] categoryIDs, boolean includeInTotalBudget, boolean showInOverview)
    {
        Intent intent = new Intent(context, BudgetsService.class);
        intent.putExtra(BudgetsService.EXTRA_REQUEST_TYPE, BudgetsService.RT_UPDATE_ITEM);
        intent.putExtra(BudgetsService.EXTRA_FORCE, true);
        intent.putExtra(BudgetsService.EXTRA_ITEM_ID, itemId);
        intent.putExtra(BudgetsService.EXTRA_TITLE, title);
        intent.putExtra(BudgetsService.EXTRA_NOTE, note);
        intent.putExtra(BudgetsService.EXTRA_PERIOD, period);
        intent.putExtra(BudgetsService.EXTRA_AMOUNT, amount);
        intent.putExtra(BudgetsService.EXTRA_CATEGORY_IDS, categoryIDs);
        intent.putExtra(BudgetsService.EXTRA_INCLUDE_IN_TOTAL_BUDGET, includeInTotalBudget);
        intent.putExtra(BudgetsService.EXTRA_SHOW_IN_OVERVIEW, showInOverview);
        context.startService(intent);
    }

    public static void deleteBudgets(Context context, long[] itemIDs)
    {
        Intent intent = new Intent(context, BudgetsService.class);
        intent.putExtra(BudgetsService.EXTRA_REQUEST_TYPE, BudgetsService.RT_DELETE_ITEMS);
        intent.putExtra(BudgetsService.EXTRA_FORCE, true);
        intent.putExtra(BudgetsService.EXTRA_ITEM_IDS, itemIDs);
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

    public static void exportJSON(Context context)
    {
        Intent intent = new Intent(context, BackupService.class);
        intent.putExtra(BackupService.EXTRA_REQUEST_TYPE, BackupService.RT_EXPORT_JSON);
        intent.putExtra(BackupService.EXTRA_FORCE, true);
        context.startService(intent);
    }

    public static void importJSON(Context context, String filePath)
    {
        Intent intent = new Intent(context, BackupService.class);
        intent.putExtra(BackupService.EXTRA_REQUEST_TYPE, BackupService.RT_IMPORT_JSON);
        intent.putExtra(BackupService.EXTRA_FORCE, true);
        intent.putExtra(BackupService.EXTRA_FILE_PATH, filePath);
        context.startService(intent);
    }
}