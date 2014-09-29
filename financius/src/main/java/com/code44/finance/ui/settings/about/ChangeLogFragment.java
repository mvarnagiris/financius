package com.code44.finance.ui.settings.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.ui.BaseFragment;

public class ChangeLogFragment extends BaseFragment {
    public static ChangeLogFragment newInstance() {
        return new ChangeLogFragment();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_log, container, false);
    }
}
