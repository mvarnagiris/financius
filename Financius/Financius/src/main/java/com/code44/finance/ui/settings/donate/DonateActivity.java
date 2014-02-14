package com.code44.finance.ui.settings.donate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.code44.finance.BuildConfig;
import com.code44.finance.R;
import com.code44.finance.billing.IabHelper;
import com.code44.finance.billing.IabResult;
import com.code44.finance.billing.Inventory;
import com.code44.finance.billing.Purchase;
import com.code44.finance.billing.SkuDetails;
import com.code44.finance.ui.BaseActivity;
import com.code44.finance.utils.PrefsHelper;
import com.code44.finance.utils.SecurityHelper;
import com.code44.finance.utils.Tracking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonateActivity extends BaseActivity implements IabHelper.QueryInventoryFinishedListener, IabHelper.OnConsumeFinishedListener, IabHelper.OnConsumeMultiFinishedListener, IabHelper.OnIabPurchaseFinishedListener, View.OnClickListener
{
    public static final String SKU_DONATE_1 = "donate_1";
    public static final String SKU_DONATE_2 = "donate_2";
    public static final String SKU_DONATE_3 = "donate_3";
    public static final String SKU_DONATE_4 = "donate_4";
    public static final String SKU_DONATE_5 = "donate_5";
    // ---------------------------------------------------------------------------------------------
    private static final String p1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQE";
    private static final String p4 = "JSeOiVL3c1DAOxC2/6A61DWsqQOHOYpKHoZglL3c/NHBsbzwSbJrEdbJCKipcniAHdTiGA2ozAQDk";
    private static final String p6 = "97EMtsLxgrkUf3/fprakq33vo7PKbm5l3hPBuGeO+nJhIMeSms0IIErkstJH593CeAZ9QIbaYJIytLfrNq3dZtooXBuR3QQIDAQAB";
    // -----------------------------------------------------------------------------------------------------------------
    private static final int REQUEST_DONATE = 1;
    // -----------------------------------------------------------------------------------------------------------------
    private final Map<String, Product> products = new HashMap<String, Product>();
    // -----------------------------------------------------------------------------------------------------------------
    private ImageView photo_IV;
    private LinearLayout container_V;
    // -----------------------------------------------------------------------------------------------------------------
    private IabHelper billingHelper;

    public static void startDonate(Context context)
    {
        Intent intent = new Intent(context, DonateActivity.class);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        // Get vies
        photo_IV = (ImageView) findViewById(R.id.photo_IV);
        container_V = (LinearLayout) findViewById(R.id.container_V);
        final View separator_V = findViewById(R.id.separator_V);
        final View donateSwitchContainer_V = findViewById(R.id.donateSwitchContainer_V);
        final Switch switch_S = (Switch) findViewById(R.id.switch_S);

        // Setup
        setActionBarTitle(R.string.donate);
        separator_V.setVisibility(PrefsHelper.getDefault(this).isEnoughTimeForDonateInNavigation() ? View.VISIBLE : View.GONE);
        donateSwitchContainer_V.setVisibility(PrefsHelper.getDefault(this).isEnoughTimeForDonateInNavigation() ? View.VISIBLE : View.GONE);
        //Picasso.with(this).load("https://plus.google.com/s2/photos/profile/112109180062919976918").fit().into(photo_IV);
        switch_S.setChecked(PrefsHelper.getDefault(this).showDonateInNavigation());
        switch_S.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                PrefsHelper.getDefault(DonateActivity.this).setShowDonateInNavigation(isChecked);
            }
        });

        // Setup billing
        billingHelper = new IabHelper(this, p1 + "Ay+WnMitUf3lHTifPBZMBJYfseutMrgna88TJZ" + SecurityHelper.p3 + p4 + "HNcbX4+/yG1doAm7eIBKryReTM1gTMxEzJj1nzJGsKlEqOMBMq09OsCQfoHiCfnLKtf+NSpDV" + p6);
        if (BuildConfig.DEBUG)
            billingHelper.enableDebugLogging(true);
        billingHelper.startSetup(new IabHelper.OnIabSetupFinishedListener()
        {
            public void onIabSetupFinished(IabResult result)
            {
                if (!result.isSuccess())
                {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                List<String> skuList = new ArrayList<String>();
                skuList.add(SKU_DONATE_1);
                skuList.add(SKU_DONATE_2);
                skuList.add(SKU_DONATE_3);
                skuList.add(SKU_DONATE_4);
                skuList.add(SKU_DONATE_5);

                // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
                billingHelper.queryInventoryAsync(true, skuList, DonateActivity.this);
            }
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (billingHelper != null)
            billingHelper.dispose();
        billingHelper = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Pass on the activity result to the helper for handling
        if (!billingHelper.handleActivityResult(requestCode, resultCode, data))
        {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return false;
    }

    @Override
    public void onClick(View v)
    {
        billingHelper.launchPurchaseFlow(this, (String) v.getTag(), REQUEST_DONATE, this);
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inventory)
    {
        if (result.isFailure())
        {
            complain("Failed to query inventory: " + result);
            return;
        }

        final String[] donateSKUs = {SKU_DONATE_1, SKU_DONATE_2, SKU_DONATE_3, SKU_DONATE_4, SKU_DONATE_5};
        final List<Purchase> purchaseToConsume = new ArrayList<Purchase>();
        Purchase purchase;
        products.clear();
        for (String sku : donateSKUs)
        {
            if (inventory.hasDetails(sku))
            {
                SkuDetails details = inventory.getSkuDetails(sku);
                Product product = new Product(sku, details.getTitle().replace("(Financius - Expense Manager)", ""), details.getDescription(), details.getPrice());

                purchase = inventory.getPurchase(sku);
                if (purchase != null && verifyDeveloperPayload(purchase))
                {
                    purchaseToConsume.add(purchase);
                    product.setEnabled(false);
                }
                else
                    product.setEnabled(true);

                products.put(sku, product);
            }
        }

        if (purchaseToConsume.size() > 0)
        {
            billingHelper.consumeAsync(purchaseToConsume, this);
            return;
        }

        updateUI();
    }

    @Override
    public void onConsumeFinished(Purchase purchase, IabResult result)
    {
        if (result.isSuccess())
            products.get(purchase.getSku()).setEnabled(true);
        else
            complain("Error while consuming: " + result);
        updateUI();
    }

    @Override
    public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results)
    {
        for (int i = 0; i < purchases.size(); i++)
        {
            final IabResult result = results.get(i);
            if (result.isSuccess())
                products.get(purchases.get(i).getSku()).setEnabled(true);
            else
                complain("Error while consuming: " + result);
        }

        updateUI();
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase purchase)
    {
        if (result.isFailure())
        {
            if (result.getResponse() != -1005)
                complain("Error purchasing: " + result);
            return;
        }

        if (!verifyDeveloperPayload(purchase))
        {
            complain("Error purchasing. Authenticity verification failed.");
            return;
        }

        products.get(purchase.getSku()).setEnabled(false);
        updateUI();

        billingHelper.consumeAsync(purchase, this);
        Tracking.onPurchaseCompleted(this, purchase);

        new AlertDialog.Builder(this).setMessage(R.string.l_donate_thank_donate).setNeutralButton(R.string.ok, null).create().show();
    }

    private void complain(String message)
    {
        alert("Error: " + message);
    }

    private void alert(String message)
    {
        if (!isFinishing())
        {
            AlertDialog.Builder bld = new AlertDialog.Builder(this);
            bld.setMessage(message);
            bld.setNeutralButton("OK", null);
            bld.create().show();
        }
    }

    /**
     * Verifies the developer payload of a purchase.
     */
    private boolean verifyDeveloperPayload(Purchase p)
    {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    private void updateUI()
    {
        container_V.removeAllViews();

        String[] skuArray = {SKU_DONATE_1, SKU_DONATE_2, SKU_DONATE_3, SKU_DONATE_4, SKU_DONATE_5};

        Product product;
        for (String sku : skuArray)
        {
            product = products.get(sku);
            if (product != null)
            {
                View view = LayoutInflater.from(this).inflate(R.layout.v_donate, container_V, false);
                ((TextView) view.findViewById(R.id.title_TV)).setText(product.getTitle());
                ((TextView) view.findViewById(R.id.price_TV)).setText(product.getPrice());
                view.setBackgroundResource(R.drawable.btn_borderless);
                view.setEnabled(product.isEnabled());
                view.setTag(product.getSku());
                view.setOnClickListener(this);
                container_V.addView(view);
            }
        }
    }

    private static class Product
    {
        private String sku;
        private String title;
        private String description;
        private String price;
        private boolean isEnabled;

        private Product(String sku, String title, String description, String price)
        {
            this.sku = sku;
            this.title = title;
            this.description = description;
            this.price = price;
            this.isEnabled = false;
        }

        public String getSku()
        {
            return sku;
        }

        public String getTitle()
        {
            return title;
        }

        public String getDescription()
        {
            return description;
        }

        public String getPrice()
        {
            return price;
        }

        public boolean isEnabled()
        {
            return isEnabled;
        }

        public void setEnabled(boolean enabled)
        {
            isEnabled = enabled;
        }
    }
}