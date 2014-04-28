package com.code44.finance.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.content.LocalBroadcastManager;
import android.text.SpannableStringBuilder;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.code44.finance.R;
import com.code44.finance.ui.settings.SettingsActivity;
import com.code44.finance.ui.settings.lock.LockActivity;
import com.code44.finance.utils.SecurityHelper;
import com.code44.finance.utils.Tracking;

/**
 * Created by Mantas on 25/05/13.
 */
public abstract class BaseActivity extends FragmentActivity
{
    protected static final String STATE_FORCE_SECURITY = BaseActivity.class.getName() + ".STATE_FORCE_SECURITY";
    protected final BroadcastReceiver killReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            finish();
        }
    };
    protected boolean forceSecurity = false;

    protected static void start(Context context, Intent intent, View expandFromView)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && expandFromView != null &&context instanceof Activity)
        {
            final Bundle options = ActivityOptionsCompat.makeScaleUpAnimation(expandFromView, 0, 0, expandFromView.getWidth(), expandFromView.getHeight()).toBundle();
            ActivityCompat.startActivity((Activity) context, intent, options);
        }
        else
        {
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        // Register broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(killReceiver, new IntentFilter(LockActivity.ACTION_KILL));

        // Setup ActionBar
        //noinspection ConstantConditions
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Restore state
        if (savedInstanceState != null)
            forceSecurity = savedInstanceState.getBoolean(STATE_FORCE_SECURITY, false);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Tracking.startTracking(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Ask for unlock if necessary
        SecurityHelper.getDefault(this).checkSecurity(this, forceSecurity);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Update unlock timestamp
        SecurityHelper.getDefault(this).updateUnlockTimestamp();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Tracking.stopTracking(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        // Unregister broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(killReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_FORCE_SECURITY, forceSecurity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                TaskStackBuilder tsb = TaskStackBuilder.create(this);
                setupParentActivities(tsb);
                final int intentCount = tsb.getIntentCount();
                if (intentCount > 0)
                {
                    Intent upIntent = tsb.getIntents()[intentCount - 1];
                    if (NavUtils.shouldUpRecreateTask(this, upIntent))
                    {
                        // This activity is not part of the application's task, so create a new task with a synthesized back stack.
                        tsb.startActivities();
                        finish();
                    }
                    else
                    {
                        // This activity is part of the application's task, so simply navigate up to the hierarchical parent activity.
                        NavUtils.navigateUpTo(this, upIntent);
                    }
                }
                else
                {
                    onBackPressed();
                }
                return true;
            }

            case R.id.action_settings:
                SettingsActivity.startSettings(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setForceSecurity(boolean forceSecurity)
    {
        this.forceSecurity = forceSecurity;
    }

    /**
     * Override this method and add activities to {@link TaskStackBuilder}. If you don't do that, "Up" action will behave as back button.
     *
     * @param tsb Task stack builder.
     */
    protected void setupParentActivities(TaskStackBuilder tsb)
    {
    }

    protected void setActionBarTitle(int resId)
    {
        setActionBarTitle(getString(resId));
    }

    protected void setActionBarTitle(String title)
    {
        SpannableStringBuilder ssb = new SpannableStringBuilder(title);
        ssb.setSpan(new TypefaceSpan("sans-serif-light"), 0, title.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        getActionBar().setTitle(ssb);
    }
}