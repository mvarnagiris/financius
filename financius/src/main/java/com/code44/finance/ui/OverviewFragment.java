package com.code44.finance.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.ui.accounts.AccountsActivity;

public class OverviewFragment extends BaseFragment {
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

        view.findViewById(R.id.accounts_B).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountsActivity.start(getActivity(), v);
            }
        });
    }
}
