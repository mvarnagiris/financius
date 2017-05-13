package com.code44.finance.ui.common.views;

import com.code44.finance.ui.reports.categories.CategoriesPieChartView;

import dagger.Module;

@Module(
        complete = false,
        injects = {CategoriesPieChartView.class, AccountsView.class, ActiveIntervalView.class})
public class ViewModule {
}
