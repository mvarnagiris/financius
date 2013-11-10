package com.code44.finance.ui;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.code44.finance.R;

public abstract class ItemEditActivity extends ItemActivity implements ItemEditFragment.Callbacks
{
    private View buttons_V;

    /**
     * Use this when creating intent for subclasses.
     *
     * @param context Context.
     * @param cls     Class of items activity.
     * @param itemId  Id of the item.
     * @return Created intent with required extras.
     */
    protected static Intent makeIntent(Context context, Class cls, long itemId)
    {
        Intent intent = new Intent(context, cls);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Inflate a "Done/Discard" custom action bar view.
        final ActionBar actionBar = getActionBar();
        //noinspection ConstantConditions
        final LayoutInflater inflater = (LayoutInflater) actionBar.getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        buttons_V = inflater.inflate(R.layout.v_actionbar_done_discard, null);
        //noinspection ConstantConditions
        buttons_V.findViewById(R.id.action_done).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSaveOrNextClick();
            }
        });
        buttons_V.findViewById(R.id.action_discard).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((ItemEditFragment) item_F).onDiscard())
                {
                    onDiscardOrPrevClick();
                }
            }
        });

        actionBar.setCustomView(buttons_V, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //noinspection ConstantConditions
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Options menu will be hidden for item create/edit.
        return false;
    }

    /**
     * Initialize the item edit fragment here. This fragment will be added to activity.
     *
     * @param itemId Id of the item. 0 if this is "create item" process
     * @return Initialized item edit fragment.
     */
    protected abstract ItemEditFragment createItemEditFragment(long itemId);

    public void onBackPressed()
    {
        ItemEditFragment itemEdit_F = (ItemEditFragment) item_F;
        final int oldStep = itemEdit_F.getCurrentStep();
        if (itemEdit_F.discardOrPrevStep())
        {
            // Discard or move to previous step was successful. Check if current page is the first one, then proceed to super.onBackPressed()
            if (oldStep != itemEdit_F.getCurrentStep())
            {
                updateButtons();
                return;
            }
        }
        else
        {
            // If discard was unsuccessful, consume the back button event. But please make sure it never happens.
            return;
        }

        super.onBackPressed();
        overridePendingTransition(0, R.anim.cancel_item);
    }

    @Override
    public void doBackOrDiscardClick()
    {
        onDiscardOrPrevClick();
    }

    @Override
    public void doNextOrSaveClick()
    {
        onSaveOrNextClick();
    }

    @Override
    protected void onResumeFragments()
    {
        super.onResumeFragments();
        updateButtons();
    }

    @Override
    protected ItemFragment createItemFragment(long itemId)
    {
        // We reuse the same logic as in ItemActivity. Created new method for this just to make it clearer.
        return createItemEditFragment(itemId);
    }

    @Override
    protected String getActivityTitle()
    {
        // This will not be visible, but maybe accessibility framework will make a use of it.
        return getString(R.string.edit);
    }

    /**
     * This is called when user clicks Discard/Back button in the actionbar
     */
    protected void onDiscardOrPrevClick()
    {
        ItemEditFragment itemEdit_F = (ItemEditFragment) item_F;
        final int oldStep = itemEdit_F.getCurrentStep();
        if (itemEdit_F.discardOrPrevStep())
        {
            // Discard or move to previous step was successful. Check if current page is the first one, then proceed to finish the activity.
            if (oldStep != itemEdit_F.getCurrentStep())
            {
                updateButtons();
                return;
            }
        }
        else
        {
            // If discard was unsuccessful, consume the actionbar button button event.
            return;
        }

        finish();
        overridePendingTransition(0, R.anim.cancel_item);
    }

    /**
     * This is called when user clicks Done/Next button in the actionbar.
     */
    protected void onSaveOrNextClick()
    {
        ItemEditFragment itemEdit_F = (ItemEditFragment) item_F;
        final int oldStep = itemEdit_F.getCurrentStep();
        if (itemEdit_F.saveOrNextStep())
        {
            // Save or move to previous step was successful. Check if current page is the last one, then proceed to finish the activity.
            if (oldStep != itemEdit_F.getCurrentStep())
            {
                updateButtons();
                return;
            }
        }
        else
        {
            // If save was unsuccessful, consume the actionbar button button event.
            return;
        }

        finish();
    }

    /**
     * Updated actionbar buttons with appropriate texts and icons.
     */
    protected void updateButtons()
    {
        // Get steps count and current step
        ItemEditFragment itemEdit_F = (ItemEditFragment) item_F;
        final int currentStep = itemEdit_F.getCurrentStep();
        final int stepsCount = itemEdit_F.getStepsCount();

        // Get views
        final TextView discardTitle_TV = (TextView) buttons_V.findViewById(R.id.discardTitle_TV);
        final TextView doneTitle_TV = (TextView) buttons_V.findViewById(R.id.doneTitle_TV);

        // Update discard button
        if (currentStep == 0)
        {
            discardTitle_TV.setText(R.string.discard);
            discardTitle_TV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_cancel, 0, 0, 0);
        }
        else
        {
            discardTitle_TV.setText(R.string.back);
            discardTitle_TV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_previous, 0, 0, 0);
        }

        // Update done button
        if (currentStep == stepsCount - 1)
        {
            doneTitle_TV.setText(R.string.done);
            doneTitle_TV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_done, 0, 0, 0);
        }
        else
        {
            doneTitle_TV.setText(R.string.next);
            doneTitle_TV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_next, 0, 0, 0);
        }
    }
}