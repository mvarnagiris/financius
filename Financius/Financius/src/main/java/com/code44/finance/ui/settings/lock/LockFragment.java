package com.code44.finance.ui.settings.lock;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.code44.finance.ui.AbstractFragment;

public abstract class LockFragment extends AbstractFragment
{
    public static final int LT_NEW = 1;
    public static final int LT_COMPARE = 3;
    protected static final int LT_CONFIRM_NEW = 2;
    // --------------------------------------------------------------------------------------------------------------------------------
    protected static final int ERROR_NEW = 1;
    protected static final int ERROR_CONFIRM_NEW = 2;
    protected static final int ERROR_COMPARE = 3;
    // --------------------------------------------------------------------------------------------------------------------------------
    private static final String ARG_LOCK_TYPE = "ARG_LOCK_TYPE";
    private static final String ARG_CURRENT_CODE = "ARG_CURRENT_CODE";
    // --------------------------------------------------------------------------------------------------------------------------------
    private int lockType;
    private String currentCode;
    private LockFragmentListener listener;
    private LockFragmentUnlockListener unlockListener;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            unlockListener = (LockFragmentUnlockListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement LockFragmentUnlockListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // Get arguments
        if (savedInstanceState == null)
        {
            final Bundle args = getArguments();
            lockType = args.getInt(ARG_LOCK_TYPE);
            currentCode = args.getString(ARG_CURRENT_CODE);
        }
        else
        {
            lockType = savedInstanceState.getInt(ARG_LOCK_TYPE);
            currentCode = savedInstanceState.getString(ARG_CURRENT_CODE);
        }

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        updateViewsForLockType();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_LOCK_TYPE, lockType);
        outState.putString(ARG_CURRENT_CODE, currentCode);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    protected abstract void updateViewsNew();

    protected abstract void updateViewsConfirmNew();

    protected abstract void updateViewsCreated();

    protected abstract void updateViewsCompare();

    protected abstract void updateViewsError(int error);

    public void setLockFragmentListener(LockFragmentListener listener)
    {
        this.listener = listener;
    }

    protected void onCodeFromUser(String code)
    {
        switch (lockType)
        {
            case LT_NEW:
                currentCode = code;
                lockType = LT_CONFIRM_NEW;
                updateViewsForLockType();
                break;

            case LT_CONFIRM_NEW:
                if (currentCode.equals(code))
                    onLockCreated(code);
                else
                    updateViewsError(ERROR_CONFIRM_NEW);
                break;

            case LT_COMPARE:
                if (currentCode.equals(code))
                    onLockUnlocked();
                else
                    updateViewsError(ERROR_COMPARE);
                break;
        }
    }

    protected void updateViewsForLockType()
    {
        switch (lockType)
        {
            case LT_NEW:
                updateViewsNew();
                break;

            case LT_CONFIRM_NEW:
                updateViewsConfirmNew();
                break;

            case LT_COMPARE:
                updateViewsCompare();
        }
    }

    protected int getLockType()
    {
        return lockType;
    }

    protected static Bundle makeArgs(int lockType, String currentCode)
    {
        final Bundle args = new Bundle();
        args.putInt(ARG_LOCK_TYPE, lockType);
        args.putString(ARG_CURRENT_CODE, currentCode);
        return args;
    }

    private void onLockCreated(String code)
    {
        listener.onLockCreated(code);
        updateViewsCreated();
    }

    private void onLockUnlocked()
    {
        unlockListener.onLockUnlocked();
    }

    public static interface LockFragmentListener
    {
        public void onLockCreated(String code);
    }

    public static interface LockFragmentUnlockListener
    {
        public void onLockUnlocked();
    }
}