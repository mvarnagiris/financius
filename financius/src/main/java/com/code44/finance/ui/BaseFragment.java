package com.code44.finance.ui;


import android.app.Activity;
import android.support.v4.app.Fragment;

import com.code44.finance.App;
import com.code44.finance.utils.EventBus;

import javax.inject.Inject;

public class BaseFragment extends Fragment {
    @Inject EventBus eventBus;

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        App.with(activity).inject(this);
    }

    protected EventBus getEventBus() {
        return eventBus;
    }
}
