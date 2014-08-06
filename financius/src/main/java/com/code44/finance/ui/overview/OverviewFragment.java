package com.code44.finance.ui.overview;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.graphs.pie.PieChartData;
import com.code44.finance.graphs.pie.PieChartValue;
import com.code44.finance.ui.BaseFragment;
import com.code44.finance.views.OverviewGraphView;

public class OverviewFragment extends BaseFragment implements View.OnClickListener {
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

        // Setup
        overviewGraph_V.setOnClickListener(this);
        setOverviewGraphData(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.overviewGraph_V:
                break;
        }
    }

    private void setOverviewGraphData(Cursor cursor) {
        final PieChartData pieChartData = PieChartData.builder()
                .addValues(new PieChartValue(15215, 0xff8bc34a))
                .addValues(new PieChartValue(10458, 0xff03a9f4))
                .addValues(new PieChartValue(5790, 0xffffc107))
                .addValues(new PieChartValue(2000, 0xff673ab7))
                .build();
        overviewGraph_V.setPieChartData(pieChartData);
        overviewGraph_V.setTotalExpense(pieChartData.getTotalValue());
    }
}
