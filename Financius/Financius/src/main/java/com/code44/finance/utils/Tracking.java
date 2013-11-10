package com.code44.finance.utils;

import android.app.Activity;
import android.content.Context;
import com.code44.finance.billing.Purchase;
import com.code44.finance.ui.settings.donate.DonateActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import java.util.UUID;

public class Tracking
{
    public static void startTracking(Activity activity)
    {
        EasyTracker.getInstance(activity).activityStart(activity);
    }

    public static void stopTracking(Activity activity)
    {
        EasyTracker.getInstance(activity).activityStop(activity);
    }

    public static void onPurchaseCompleted(Context context, Purchase purchase)
    {
        String productName = "Donate";
        double totalRevenue = 0;
        if (purchase.getSku().equals(DonateActivity.SKU_DONATE_1))
        {
            productName = "Donate 0.99";
            totalRevenue = 0.99;
        }
        else if (purchase.getSku().equals(DonateActivity.SKU_DONATE_2))
        {
            productName = "Donate 1.99";
            totalRevenue = 1.99;
        }
        else if (purchase.getSku().equals(DonateActivity.SKU_DONATE_3))
        {
            productName = "Donate 4.99";
            totalRevenue = 4.99;
        }
        else if (purchase.getSku().equals(DonateActivity.SKU_DONATE_4))
        {
            productName = "Donate 9.99";
            totalRevenue = 9.99;
        }
        else if (purchase.getSku().equals(DonateActivity.SKU_DONATE_5))
        {
            productName = "Donate 19.99";
            totalRevenue = 19.99;
        }


        final String transactionId = UUID.randomUUID().toString();

        final EasyTracker easyTracker = EasyTracker.getInstance(context);
        easyTracker.send(MapBuilder
                .createTransaction(transactionId,       // (String) Transaction ID
                        "In-app Store",                 // (String) Affiliation
                        totalRevenue,                   // (Double) Order revenue
                        0.3d,                           // (Double) Tax
                        0.0d,                           // (Double) Shipping
                        "GBP")                          // (String) Currency code
                .build()
        );

        easyTracker.send(MapBuilder
                .createItem(transactionId,              // (String) Transaction ID
                        productName,                    // (String) Product name
                        purchase.getSku(),              // (String) Product SKU
                        "Donate",                       // (String) Product category
                        totalRevenue,                   // (Double) Product price
                        1L,                             // (Long) Product quantity
                        "GBP")                          // (String) Currency code
                .build()
        );
    }
}
