package com.code44.finance.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.ui.settings.donate.DonateActivity;
import com.code44.finance.utils.PrefsHelper;
import de.greenrobot.event.EventBus;

public class NavigationFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener
{
    private static final String STATE_SELECTED_POSITION = "STATE_SELECTED_POSITION";
    // -----------------------------------------------------------------------------------------------------------------
    private ListView list_V;
    private View donate_V;
    // -----------------------------------------------------------------------------------------------------------------
    private Callbacks callbacks;
    private NavigationAdapter adapter;

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

        // Register
        EventBus.getDefault().register(this, NavigationEvent.class);
        EventBus.getDefault().register(this, PrefsHelper.ShowDonateInNavigationChangedEvent.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        list_V = (ListView) view.findViewById(R.id.list_V);
        donate_V = view.findViewById(R.id.donate_V);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Setup
        adapter = new NavigationAdapter(getActivity());
        list_V.setAdapter(adapter);
        list_V.setOnItemClickListener(this);
        ((TextView) donate_V.findViewById(R.id.title_TV)).setText(R.string.donate);
        donate_V.setOnClickListener(this);
        donate_V.setBackgroundResource(R.drawable.btn_borderless);
        donate_V.setVisibility(PrefsHelper.getDefault(getActivity()).isEnoughTimeForDonateInNavigation() && PrefsHelper.getDefault(getActivity()).showDonateInNavigation() ? View.VISIBLE : View.GONE);

        // Select initial menu item
        final int defaultPosition = 0; // Overview
        final int initialPosition = savedInstanceState != null ? savedInstanceState.getInt(STATE_SELECTED_POSITION, defaultPosition) : defaultPosition;
        onItemClick(list_V, null, initialPosition, initialPosition);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // Unregister
        EventBus.getDefault().unregister(this, NavigationEvent.class);
        EventBus.getDefault().unregister(this, PrefsHelper.ShowDonateInNavigationChangedEvent.class);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, adapter.getSelectedPosition());
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.donate_V:
                DonateActivity.startDonate(getActivity());
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
    {
        if (callbacks != null)
        {
            NavigationAdapter.NavItemInfo item = (NavigationAdapter.NavItemInfo) adapter.getItem(position);
            callbacks.onNavItemSelected(item.getFragmentClassName());
            adapter.setSelectedPosition(position);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(NavigationEvent event)
    {
        switch (event.getType())
        {
            case NavigationEvent.TYPE_OVERVIEW:
                onItemClick(list_V, null, 0, 0);
                break;

            case NavigationEvent.TYPE_ACCOUNTS:
                onItemClick(list_V, null, 1, 1);
                break;

            case NavigationEvent.TYPE_TRANSACTIONS:
                onItemClick(list_V, null, 2, 2);
                break;

//            case NavigationEvent.TYPE_BUDGETS:
//                onItemClick(list_V, null, 3, 3);
//                break;

            case NavigationEvent.TYPE_REPORTS:
                onItemClick(list_V, null, 3, 3);
                break;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(PrefsHelper.ShowDonateInNavigationChangedEvent event)
    {
        donate_V.setVisibility(PrefsHelper.getDefault(getActivity()).isEnoughTimeForDonateInNavigation() && PrefsHelper.getDefault(getActivity()).showDonateInNavigation() ? View.VISIBLE : View.GONE);
    }

    public static interface Callbacks
    {
        public void onNavItemSelected(String fragmentName);
    }

    public static class NavigationEvent
    {
        public static final int TYPE_OVERVIEW = 0;
        public static final int TYPE_ACCOUNTS = 1;
        public static final int TYPE_TRANSACTIONS = 2;
        //public static final int TYPE_BUDGETS = 3;
        public static final int TYPE_REPORTS = 3;
        // -------------------------------------------------------------------------------------------------------------
        private final int type;

        public NavigationEvent(int type)
        {
            this.type = type;
        }

        public int getType()
        {
            return type;
        }
    }
}