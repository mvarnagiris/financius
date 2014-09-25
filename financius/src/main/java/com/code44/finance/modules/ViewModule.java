package com.code44.finance.modules;

import com.code44.finance.views.AccountsView;
import com.code44.finance.views.ActiveIntervalView;
import com.code44.finance.views.CategoriesReportView;
import com.code44.finance.views.OverviewGraphView;

import dagger.Module;

@Module(
        complete = false,
        injects = {
                OverviewGraphView.class,
                AccountsView.class,
                ActiveIntervalView.class,
                CategoriesReportView.class
        }
)
public class ViewModule {
}
