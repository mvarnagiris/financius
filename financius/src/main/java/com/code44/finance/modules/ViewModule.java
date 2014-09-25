package com.code44.finance.modules;

import com.code44.finance.views.AccountsView;
import com.code44.finance.views.IntervalView;
import com.code44.finance.views.OverviewGraphView;

import dagger.Module;

@Module(
        complete = false,
        injects = {
                OverviewGraphView.class,
                AccountsView.class,
                IntervalView.class
        }
)
public class ViewModule {
}
