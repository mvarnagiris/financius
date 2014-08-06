package com.code44.finance.ui.overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.ui.BaseFragment;
import com.code44.finance.views.OverviewGraphView;

public class OverviewFragment extends BaseFragment {
    private OverviewGraphView overviewGraph_V;

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        overviewGraph_V = (OverviewGraphView) view.findViewById(R.id.overviewGraph_V);
    }
}
