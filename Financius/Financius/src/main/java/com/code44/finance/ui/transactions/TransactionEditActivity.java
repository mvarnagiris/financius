package com.code44.finance.ui.transactions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.code44.finance.R;
import com.code44.finance.ui.AbstractActivity;

public class TransactionEditActivity extends AbstractActivity
{
    protected static final String EXTRA_ITEM_ID = TransactionEditActivity.class.getName() + ".EXTRA_ITEM_ID";
    // -----------------------------------------------------------------------------------------------------------------
    protected static final String FRAGMENT_ITEM = TransactionEditActivity.class.getName() + ".FRAGMENT_ITEM";

    public static void startItemEdit(Context context, long itemId)
    {
        final Intent intent = new Intent(context, TransactionEditActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_edit);

        // Hide ActionBar
        //noinspection ConstantConditions
        getActionBar().hide();

        // Setup fragment
        if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_ITEM) == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.container_V, TransactionEditFragment.newInstance(0), FRAGMENT_ITEM).commit();
    }
}