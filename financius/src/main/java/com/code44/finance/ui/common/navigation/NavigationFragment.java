package com.code44.finance.ui.common.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.ui.BaseFragment;

public class NavigationFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private NavigationDrawerAdapter adapter;
    private NavigationListener navigationListener;
    private NavigationScreen pendingNavigationScreen;

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof NavigationListener) {
            navigationListener = (NavigationListener) activity;
        } else {
            throw new IllegalArgumentException("Activity " + activity.getClass().getName() + " must implement " + NavigationListener.class.getName());
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final ListView listView = (ListView) view.findViewById(R.id.listView);

        // Setup
        adapter = new NavigationDrawerAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if (pendingNavigationScreen != null) {
            adapter.setSelectedNavigationScreen(pendingNavigationScreen);
            pendingNavigationScreen = null;
        }
    }

    @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        final NavigationScreen navigationScreen = adapter.getItem(position).getNavigationScreen();
        adapter.setSelectedNavigationScreen(navigationScreen);
        navigationListener.onNavigationItemSelected(navigationScreen);
    }

    public void setSelected(NavigationScreen navigationScreen) {
        if (adapter == null) {
            pendingNavigationScreen = navigationScreen;
        } else {
            adapter.setSelectedNavigationScreen(navigationScreen);
        }
    }

    public static interface NavigationListener {
        public void onNavigationItemSelected(NavigationScreen navigationScreen);
    }
}
