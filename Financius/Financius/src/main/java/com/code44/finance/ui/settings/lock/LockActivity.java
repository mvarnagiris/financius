package com.code44.finance.ui.settings.lock;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.code44.finance.R;
import com.code44.finance.ui.settings.lock.LockFragment.LockFragmentListener;
import com.code44.finance.ui.settings.lock.LockFragment.LockFragmentUnlockListener;
import com.code44.finance.utils.SecurityHelper;

public class LockActivity extends FragmentActivity implements LockFragmentListener, LockFragmentUnlockListener
{
    public static final String ACTION_KILL = LockActivity.class.getName() + ".ACTION_KILL";
    // --------------------------------------------------------------------------------------------------------------------------------
    private static final String EXTRA_MODE = LockActivity.class.getName() + ".EXTRA_MODE";
    private static final String EXTRA_LOCK = LockActivity.class.getName() + ".EXTRA_LOCK";
    // --------------------------------------------------------------------------------------------------------------------------------
    private static final String FRAGMENT_LOCK = "FRAGMENT_LOCK";
    // --------------------------------------------------------------------------------------------------------------------------------
    private int mode;
    private int lock;
    private String currentCode;

    public static void startLockNewPattern(Context context)
    {
        Intent intent = new Intent(context, LockActivity.class);
        intent.putExtra(EXTRA_MODE, SecurityHelper.MODE_NEW);
        intent.putExtra(EXTRA_LOCK, SecurityHelper.APP_LOCK_PATTERN);
        context.startActivity(intent);
    }

    public static void startLockCompare(Context context)
    {
        Intent intent = new Intent(context, LockActivity.class);
        intent.putExtra(EXTRA_MODE, SecurityHelper.MODE_COMPARE);
        context.startActivity(intent);
    }

    public static void startLockClear(Context context)
    {
        Intent intent = new Intent(context, LockActivity.class);
        intent.putExtra(EXTRA_MODE, SecurityHelper.MODE_CLEAR);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get extras
        final Intent extras = getIntent();
        mode = extras.getIntExtra(EXTRA_MODE, SecurityHelper.MODE_NEW);
        lock = extras.getIntExtra(EXTRA_LOCK, SecurityHelper.APP_LOCK_NONE);

        // Setup ActionBar
        setupActionBar(mode);

        // Add fragment for unlock if necessary
        if (savedInstanceState == null)
        {
            // Check if we need to unlock
            LockFragment f = SecurityHelper.getDefault(this).getLockFragmentForUnlock();
            if (f == null)
            {
                // No need to unlock
                onLockUnlocked();
            } else
            {
                // Need to unlock first
                f.setLockFragmentListener(this);
                getSupportFragmentManager().beginTransaction().add(android.R.id.content, f, FRAGMENT_LOCK).commit();
            }
        } else
        {
            ((LockFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_LOCK)).setLockFragmentListener(this);
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mode == SecurityHelper.MODE_COMPARE)
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_KILL));

        super.onBackPressed();
    }

    @Override
    public void onLockCreated(String code)
    {
        currentCode = code;
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
    }

    @Override
    public void onLockUnlocked()
    {
        // Called when previous lock is unlocked or when it was not set
        if (mode == SecurityHelper.MODE_NEW)
        {
            // Creating new lock
            LockFragment f = SecurityHelper.getDefault(this).getLockFragmentForNew(lock);
            f.setLockFragmentListener(this);
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, f, FRAGMENT_LOCK).commit();
        } else if (mode == SecurityHelper.MODE_CLEAR)
        {
            // Lock cleared
            SecurityHelper.getDefault(this).clearLock();
            Toast.makeText(this, R.string.lock_removed, Toast.LENGTH_SHORT).show();
            finish();
        }
        else if (mode == SecurityHelper.MODE_COMPARE)
        {
            finish();
        }
    }

    private void setupActionBar(int mode)
    {
        if (mode == SecurityHelper.MODE_NEW)
        {
            // Inflate a "Done/Discard" custom action bar view.
            final LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            final View customActionBarView = inflater.inflate(R.layout.v_actionbar_done_discard, null);
            customActionBarView.findViewById(R.id.action_done).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    saveNewCode();
                }
            });
            customActionBarView.findViewById(R.id.action_discard).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    finish();
                }
            });

            // Show the custom action bar view and hide the normal Home icon and title.
            final ActionBar actionBar = getActionBar();
            actionBar.setCustomView(customActionBarView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else
        {
            getActionBar().hide();
        }
    }

    private void saveNewCode()
    {
        SecurityHelper.getDefault(this).setNewCode(lock, currentCode);
        Toast.makeText(LockActivity.this, R.string.lock_created, Toast.LENGTH_SHORT).show();
        finish();
    }
}