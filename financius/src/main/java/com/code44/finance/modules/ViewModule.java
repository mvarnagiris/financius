package com.code44.finance.modules;

import com.code44.finance.ui.common.ActiveIntervalView;
import com.code44.finance.ui.overview.OverviewGraphView;
import com.code44.finance.ui.reports.categories.CategoriesReportView;
import com.code44.finance.views.AccountsView;

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
