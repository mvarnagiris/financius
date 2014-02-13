package com.code44.finance.ui;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.code44.finance.R;

@SuppressWarnings({"UnusedParameters", "ConstantConditions", "UnnecessaryLocalVariable", "UnusedDeclaration"})
public abstract class ItemListActivity extends BaseActivity
{
    // -----------------------------------------------------------------------------------------------------------------
    protected static final String EXTRA_SELECTION_TYPE = ItemListActivity.class.getName() + ".EXTRA_SELECTION_TYPE";
    protected static final String EXTRA_ITEM_IDS = ItemListActivity.class.getName() + ".EXTRA_ITEM_IDS";
    // -----------------------------------------------------------------------------------------------------------------
    protected static final String FRAGMENT_LIST = "FRAGMENT_LIST";
    // -----------------------------------------------------------------------------------------------------------------
    protected ItemListFragment list_F;
    // -----------------------------------------------------------------------------------------------------------------
    protected int selectionType;

    /**
     * Use this when creating intent for subclasses.
     *
     * @param context Context.
     * @param cls     Class of items activity.
     * @return Created intent with required extras.
     */
    protected static Intent makeIntent(Context context, Class cls)
    {
        Intent intent = new Intent(context, cls);
        return intent;
    }

    /**
     * Use this when creating an activity for item selection. {@link Intent} must be generated using {@link #makeIntent(android.content.Context, Class)}.
     *
     * @param fragment    Fragment that requests an item.
     * @param intent      Intent to send.
     * @param requestCode Request code.
     */
    protected static void startForSelect(Fragment fragment, Intent intent, int requestCode)
    {
        intent.putExtra(EXTRA_SELECTION_TYPE, ItemListFragment.SELECTION_TYPE_SINGLE);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * Use this when creating an activity for item multi-selection. {@link Intent} must be generated using {@link #makeIntent(android.content.Context, Class)}.
     *
     * @param fragment    Fragment that requests an item.
     * @param intent      Intent to send.
     * @param requestCode Request code.
     */
    protected static void startForMultiSelect(Fragment fragment, Intent intent, int requestCode, long[] itemIDs)
    {
        intent.putExtra(EXTRA_SELECTION_TYPE, ItemListFragment.SELECTION_TYPE_MULTI);
        intent.putExtra(EXTRA_ITEM_IDS, itemIDs);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get extras
        final Intent extras = getIntent();
        selectionType = extras.getIntExtra(EXTRA_SELECTION_TYPE, ItemListFragment.SELECTION_TYPE_NONE);
        final long[] itemIDs = extras.getLongArrayExtra(EXTRA_ITEM_IDS);

        // Inflate layout
        final int containerId = inflateView(selectionType);

        // Setup actionbar
        switch (selectionType)
        {
            case ItemListFragment.SELECTION_TYPE_NONE:
                setActionBarTitle(getActivityTitle());
                break;

            case ItemListFragment.SELECTION_TYPE_SINGLE:
                setActionBarTitle(R.string.selection);
                break;

            case ItemListFragment.SELECTION_TYPE_MULTI:
                final ActionBar actionBar = getActionBar();
                final LayoutInflater inflater = (LayoutInflater) actionBar.getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View buttons_V = inflater.inflate(R.layout.v_actionbar_done_discard, null);
                final TextView discardTitle_TV = (TextView) buttons_V.findViewById(R.id.discardTitle_TV);
                discardTitle_TV.setText(R.string.cancel);
                buttons_V.findViewById(R.id.action_done).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (list_F != null)
                        {
                            final long[] itemIDs = list_F.getSelectedItemIDs();
                            Intent data = new Intent();
                            data.putExtra(ItemListFragment.RESULT_EXTRA_ITEM_IDS, itemIDs);
                            setResult(RESULT_OK, data);
                            finish();
                        }
                    }
                });
                buttons_V.findViewById(R.id.action_discard).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                });
                actionBar.setCustomView(buttons_V, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                        ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
                break;
        }

        // Setup fragments
        final FragmentManager fm = getSupportFragmentManager();
        list_F = (ItemListFragment) fm.findFragmentByTag(FRAGMENT_LIST);
        if (list_F == null)
        {
            final FragmentTransaction ft = fm.beginTransaction();

            if (list_F == null)
            {
                list_F = createListFragment(selectionType, itemIDs);
                ft.replace(containerId, list_F, FRAGMENT_LIST);
            }

            ft.commit();
        }
    }

    /**
     * Create new instance of list fragment.
     *
     *
     * @param selectionType List fragments require this parameter.
     * @param itemIDs
     * @return New instance of list fragment.
     */
    protected abstract ItemListFragment createListFragment(int selectionType, long[] itemIDs);

    /**
     * Return the title of activity here.
     *
     * @return Title of activity
     */
    protected abstract String getActivityTitle();

    /**
     * Override this if you want custom layout for your list. By default, list fragment will be put in android.R.id.content container.
     *
     * @param selectionType For convenience. Maybe you require different layout for selection.
     * @return Id of the container where to put list fragment.
     */
    protected int inflateView(int selectionType)
    {
        return android.R.id.content;
    }
}