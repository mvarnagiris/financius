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

public class NavigationFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private NavigationAdapter adapter;
    private NavigationListener navigationListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof NavigationListener) {
            navigationListener = (NavigationListener) activity;
        } else {
            throw new IllegalArgumentException("Activity " + activity.getClass().getName() + " must implement " + NavigationListener.class.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final ListView list_V = (ListView) view.findViewById(R.id.list_V);

        // Setup
        adapter = new NavigationAdapter(getActivity());
        list_V.setAdapter(adapter);
        list_V.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        select(id);
    }

    private void select(long navigationId) {
        adapter.setSelectedId(navigationId);
        navigationListener.onNavigationItemSelected(adapter.getSelectedItem());
    }

    public static interface NavigationListener {
        public void onNavigationItemSelected(NavigationAdapter.NavigationItem item);
    }
}
