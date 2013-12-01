package com.code44.finance.utils;

import android.content.Context;
import android.net.Uri;
import com.code44.finance.FinanciusApp;
import com.code44.finance.providers.*;

public class NotifyUtils
{
    public static void notifyAll(Context context)
    {
        context.getContentResolver().notifyChange(CurrenciesProvider.uriCurrencies(context), null);
        context.getContentResolver().notifyChange(AccountsProvider.uriAccounts(context), null);
        context.getContentResolver().notifyChange(CategoriesProvider.uriCategories(context), null);
        context.getContentResolver().notifyChange(TransactionsProvider.uriTransactions(context), null);
        context.getContentResolver().notifyChange(BudgetsProvider.uriBudgets(context), null);
    }

    public static Uri[] getAccountUpdatedURIs()
    {
        final Context context = FinanciusApp.getAppContext();
        return new Uri[]{CurrenciesProvider.uriCurrencies(context), AccountsProvider.uriAccounts(context), TransactionsProvider.uriTransactions(context), BudgetsProvider.uriBudgets(context)};
    }

    public static void onCategoryUpdated(Context context)
    {
        context.getContentResolver().notifyChange(CategoriesProvider.uriCategories(context), null);
        context.getContentResolver().notifyChange(TransactionsProvider.uriTransactions(context), null);
        context.getContentResolver().notifyChange(BudgetsProvider.uriBudgets(context), null);
    }

    public static void onTransactionUpdated(Context context)
    {
        context.getContentResolver().notifyChange(AccountsProvider.uriAccounts(context), null);
        context.getContentResolver().notifyChange(TransactionsProvider.uriTransactions(context), null);
        context.getContentResolver().notifyChange(BudgetsProvider.uriBudgets(context), null);
    }

    public static void onBudgetUpdated(Context context)
    {
        context.getContentResolver().notifyChange(BudgetsProvider.uriBudgets(context), null);
    }
}