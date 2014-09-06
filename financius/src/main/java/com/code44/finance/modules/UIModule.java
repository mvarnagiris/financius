package com.code44.finance.modules;

import com.code44.finance.ui.MainActivity;
import com.code44.finance.ui.NavigationFragment;
import com.code44.finance.ui.overview.OverviewFragment;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                MainActivity.class,

                NavigationFragment.class,
                OverviewFragment.class
        }
)
public class UIModule {
}
