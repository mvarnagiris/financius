package com.code44.finance.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.adapters.NavigationBottomAdapter;

public class NavigationFragment extends BaseFragment {
    private NavigationAdapter adapter;
    private NavigationListener navigationListener;
    private NavigationAdapter.NavigationScreen pendingNavigationScreen;
    private NavigationBottomAdapter bottomAdapter;

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
        adapter = new NavigationAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final NavigationAdapter.NavigationScreen navigationScreen = ((NavigationAdapter.NavigationItem) adapter.getItem(position)).getNavigationScreen();
                adapter.setSelectedNavigationScreen(navigationScreen);
                navigationListener.onNavigationItemSelected(navigationScreen);
            }
        });

        if (pendingNavigationScreen != null) {
            adapter.setSelectedNavigationScreen(pendingNavigationScreen);
            pendingNavigationScreen = null;
        }

        final ListView listViewBottom = (ListView) view.findViewById(R.id.listViewBottom);

        bottomAdapter = new NavigationBottomAdapter(getActivity());

        listViewBottom.setAdapter(bottomAdapter);
        listViewBottom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final NavigationAdapter.NavigationScreen navigationScreen = ((NavigationAdapter.NavigationItem) bottomAdapter.getItem(position)).getNavigationScreen();
                navigationListener.onNavigationItemSelected(navigationScreen);
            }
        });
    }

    public void setSelected(NavigationAdapter.NavigationScreen navigationScreen) {
        if (adapter == null) {
            pendingNavigationScreen = navigationScreen;
        } else {
            adapter.setSelectedNavigationScreen(navigationScreen);
        }
    }

    public static interface NavigationListener {
        public void onNavigationItemSelected(NavigationAdapter.NavigationScreen navigationScreen);
    }
}
