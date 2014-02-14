package com.code44.finance.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.utils.StringUtils;

import java.io.IOException;

public class AboutActivity extends BaseActivity
{
    public static void startAbout(Context context)
    {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Get views
        final TextView version_TV = (TextView) findViewById(R.id.version_TV);
        final TextView description_TV = (TextView) findViewById(R.id.description_TV);

        // Setup
        try
        {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            final String versionName = pInfo.versionName;

            version_TV.setText("Version: " + versionName);
        }
        catch (NameNotFoundException e)
        {
        }

        try
        {
            description_TV.setText(Html.fromHtml(StringUtils.readInputStream(getAssets().open("changes.txt"))));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return false;
    }
}