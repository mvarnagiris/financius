package com.code44.finance.ui.overview;

import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.ui.BaseActivity;
import com.code44.finance.utils.PeriodHelper;

import de.greenrobot.event.EventBus;

public class OverviewActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        toolbarHelper.setElevation(0);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_V, OverviewFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTitle();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(PeriodHelper.PeriodChangedEvent event) {
        updateTitle();
    }

    private void updateTitle() {
        toolbarHelper.setTitle(PeriodHelper.get().getTitle());
    }
}
