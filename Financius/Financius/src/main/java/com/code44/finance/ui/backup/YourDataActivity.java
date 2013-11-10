package com.code44.finance.ui.backup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.code44.finance.R;
import com.code44.finance.ui.AbstractActivity;

public class YourDataActivity extends AbstractActivity
{
    public static void start(Context context)
    {
        context.startActivity(new Intent(context, YourDataActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Setup ActionBar
        setActionBarTitle(R.string.your_data);

        // Add fragment
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, YourDataFragment.newInstance()).commit();
    }
}