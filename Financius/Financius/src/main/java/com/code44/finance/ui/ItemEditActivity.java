package com.code44.finance.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import com.code44.finance.R;

public abstract class ItemEditActivity extends ItemActivity implements ItemEditFragment.Callbacks
{
    private TextView discardTitle_TV;
    private TextView doneTitle_TV;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Hide ActionBar
        //noinspection ConstantConditions
        getActionBar().hide();

        // Get views
        discardTitle_TV = (TextView) findViewById(R.id.discardTitle_TV);
        doneTitle_TV = (TextView) findViewById(R.id.doneTitle_TV);
        final View discardAction_V = findViewById(R.id.action_discard);
        final View doneAction_V = findViewById(R.id.action_done);

        // Setup
        discardAction_V.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onDiscardOrPrevClick();
            }
        });
        doneAction_V.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSaveOrNextClick();
            }
        });
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
    protected int inflateView()
    {
        setContentView(R.layout.activity_item_edit);
        return R.id.container_V;
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