package com.code44.finance.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.adapters.NavigationAdapter;
import com.squareup.otto.Subscribe;

public class NavigationFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private static final String STATE_SELECTED_ID = "STATE_SELECTED_ID";
    private static final String STATE_REQUESTED_NAVIGATION_ID = "STATE_REQUESTED_NAVIGATION_ID";

    private NavigationAdapter adapter;
    private NavigationListener navigationListener;
    private int requestedNavigationId = 0;

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof NavigationListener) {
            navigationListener = (NavigationListener) activity;
        } else {
            throw new IllegalArgumentException("Activity " + activity.getClass().getName() + " must implement " + NavigationListener.class.getName());
        }
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getEventBus().register(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final ListView list_V = (ListView) view.findViewById(R.id.list_V);

        // Setup
        adapter = new NavigationAdapter(getActivity());
        list_V.setAdapter(adapter);
        list_V.setOnItemClickListener(this);
        if (savedInstanceState != null) {
            requestedNavigationId = savedInstanceState.getInt(STATE_REQUESTED_NAVIGATION_ID, 0);
            if (requestedNavigationId != 0) {
                select(requestedNavigationId);
                requestedNavigationId = 0;
            } else {
                adapter.setSelectedId(savedInstanceState.getInt(STATE_SELECTED_ID, NavigationAdapter.NAV_ID_OVERVIEW));
            }
        }
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_ID, adapter.getSelectedItem().getId());
        outState.putInt(STATE_REQUESTED_NAVIGATION_ID, requestedNavigationId);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        getEventBus().unregister(this);
    }

    @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        select(id);
    }

    @Subscribe public void onNavigationRequested(RequestNavigation requestNavigation) {
        if (isResumed()) {
            select(requestNavigation.getNavigationId());
        }
    }

    public void select(long navigationId) {
        adapter.setSelectedId(navigationId);
        navigationListener.onNavigationItemSelected(adapter.getSelectedItem());
    }

    public static interface NavigationListener {
        public void onNavigationItemSelected(NavigationAdapter.NavigationItem item);
    }

    public static class RequestNavigation {
        private final int navigationId;

        public RequestNavigation(int navigationId) {
            this.navigationId = navigationId;
        }

        public int getNavigationId() {
            return navigationId;
        }
    }
}
