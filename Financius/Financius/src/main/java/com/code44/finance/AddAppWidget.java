package com.code44.finance;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.RemoteViews;
import com.code44.finance.R;
import com.code44.finance.ui.MainActivity;
import com.code44.finance.ui.transactions.TransactionEditActivity;
import com.code44.finance.ui.transactions.TransactionEditFragment;

/**
 * Implementation of App Widget functionality.
 */
public class AddAppWidget extends AppWidgetProvider {

    private static final String AddExpenseTransaction = "AddExpenseTransaction";
    private static final String AddIncomeTransaction = "AddIncomeTransaction";
    private static final String AddTransferTransaction = "AddTransferTransaction";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
        Intent startIntent = new Intent(context, TransactionEditActivity.class);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        Boolean start = false;
        if(intent.getAction().compareTo(AddExpenseTransaction) == 0)
        {
            bundle.putString(TransactionEditFragment.TRANSACTION_TYPE, TransactionEditFragment.TRANSACTION_TYPE_EXPENSE);
            start = true;
        }
        else if(intent.getAction().compareTo(AddIncomeTransaction) == 0)
        {
            bundle.putString(TransactionEditFragment.TRANSACTION_TYPE, TransactionEditFragment.TRANSACTION_TYPE_INCOME);
            start = true;
        }
        else if(intent.getAction().compareTo(AddTransferTransaction) == 0)
        {
            bundle.putString(TransactionEditFragment.TRANSACTION_TYPE, TransactionEditFragment.TRANSACTION_TYPE_TRANSFER);
            start = true;
        }
        if(start)
        {
            startIntent.putExtras(bundle);
            context.startActivity(startIntent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        Intent actClick = new Intent();
        actClick.setAction(AddExpenseTransaction);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, actClick, 0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.add_app_widget);

        views.setOnClickPendingIntent(R.id.appwidget_expense, pending);
        actClick.setAction(AddIncomeTransaction);
        pending = PendingIntent.getBroadcast(context, 0, actClick, 0);
        views.setOnClickPendingIntent(R.id.appwidget_income, pending);
        actClick.setAction(AddTransferTransaction);
        pending = PendingIntent.getBroadcast(context, 0, actClick, 0);
        views.setOnClickPendingIntent(R.id.appwidget_transfer, pending);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }
}


