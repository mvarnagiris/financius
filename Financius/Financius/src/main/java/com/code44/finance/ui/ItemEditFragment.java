package com.code44.finance.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

public abstract class ItemEditFragment extends ItemFragment
{
    private static final String STATE_CURRENT_STEP = "STATE_CURRENT_STEP";
    // -----------------------------------------------------------------------------------------------------------------
    protected Callbacks callbacks;
    protected boolean isDataLoaded = false;
    protected int currentStep;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        if (activity instanceof Callbacks)
            callbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        // Restore state
        currentStep = savedInstanceState != null ? savedInstanceState.getInt(STATE_CURRENT_STEP) : 0;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        restoreOrInit(itemId, savedInstanceState);
        isDataLoaded = savedInstanceState != null;
    }

    @Override
    protected void startItemEdit(Context context, long itemId)
    {
        // Ignore.
    }

    @Override
    protected boolean onDeleteItem(Context context, long[] itemIds)
    {
        // Ignore
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_STEP, currentStep);
    }

    /**
     * Called when loader gets item from database.
     *
     * @param c            Cursor.
     * @param isDataLoaded If {@code true} that means data is already loaded once from saved instance state (on rotation) or from cursor and there is no need to assign values again.
     * @return {@code true} if item data was updated; {@code false} otherwise.
     * <p>Usually you should keep this format:</p>
     * <pre>{@code
     * if (!isDataLoaded && c != null && c.moveToFirst())
     * {
     *     // Set item data.
     *     return true;
     * }
     * return isDataLoaded;
     * }</pre>
     */
    protected abstract boolean bindItem(Cursor c, boolean isDataLoaded);

    /**
     * Restore state here. Called from {@link #onActivityCreated(android.os.Bundle)}.
     * <p>Usually you should keep this format:</p>
     * <pre>{@code
     * if (savedInstanceState != null)
     * {
     *     // Restore state
     * }
     * else if (itemId == 0)
     * {
     *     // Init item creation
     * }
     * }</pre>
     *
     * @param itemId             Item Id.
     * @param savedInstanceState Saved instance state.
     */
    protected abstract void restoreOrInit(long itemId, Bundle savedInstanceState);

    /**
     * Save data here. If data cannot be saved, do whatever is necessary to notify the user about what's wrong.
     *
     * @param context Context. For convenience.
     * @param itemId  Item Id. For convenience.
     * @return {@code true} of data was saved successfully; {@code false} otherwise. Returning {@code false} will prevent the finishing of Activity.
     */
    protected abstract boolean onSave(Context context, long itemId);

    /**
     * Discard all changes here. Usually you don't need to do anything here, because most of the time the changes are just in memory, not database. In case data cannot be discarded, notify the user about what's wrong.
     *
     * @return {@code true} of data changes were discarded; {@code false} otherwise. Returning {@code false} will prevent the finishing of Activity.
     */
    protected abstract boolean onDiscard();

    public boolean saveOrNextStep()
    {
        final int stepsCount = getStepsCount();
        if (stepsCount < 1)
            throw new IllegalArgumentException("getStepsCount(long) = " + stepsCount + ". This value cannot return anything less that 1");

        if (stepsCount == 1 || getCurrentStep() == stepsCount - 1)
        {
            // If this is the only step or if it's the last step, then try to save
            return onSave(getActivity(), itemId);
        }
        else
        {
            // If this is not the last step move to next page
            int nextStep = onNextStep();

            // Check if moving to next step was successful.
            if (nextStep > 0)
            {
                currentStep = nextStep;
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public boolean discardOrPrevStep()
    {
        final int stepsCount = getStepsCount();
        if (stepsCount < 1)
            throw new IllegalArgumentException("getStepsCount(long) = " + stepsCount + ". This value cannot return anything less that 1");

        if (stepsCount == 1 || getCurrentStep() == 0)
        {
            // If this is the only step or if it's the first step, then try to discard
            return onDiscard();
        }
        else
        {
            // If this is not the first step move to previous page
            int prevStep = onPrevStep();

            // Check if moving to previous step was successful.
            if (prevStep >= 0)
            {
                currentStep = prevStep;
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Return the number of pages this create/update process will have. This affects what will be shown in the Activity's action bar - Discard | Done, Discard | Next, Back | Next, Back | Done.
     *
     * @return Number of steps for create/update process. Value must be >= 1.
     */
    public int getStepsCount()
    {
        return 1;
    }

    /**
     * @return Current step. If you override this method, make sure to change "Restore state" logic in {@link #onCreate(android.os.Bundle)} method.
     */
    public int getCurrentStep()
    {
        return currentStep;
    }

    @Override
    protected void bindItem(Cursor c)
    {
        isDataLoaded = bindItem(c, isDataLoaded);
    }

    /**
     * If your item edit is multistep, override this method and move to next step here.
     *
     * @return Zero based index of step number. If move to next step was unsuccessful, return a negative value.
     */
    protected int onNextStep()
    {
        return 0;
    }

    /**
     * If your item edit is multistep, override this method and move to previous step here.
     *
     * @return Zero based index of step number. If move to previous step was unsuccessful, return a negative value.
     */
    protected int onPrevStep()
    {
        return 0;
    }

    public static interface Callbacks
    {
        public void doBackOrDiscardClick();

        public void doNextOrSaveClick();
    }
}