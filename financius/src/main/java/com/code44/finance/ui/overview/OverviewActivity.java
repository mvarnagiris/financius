package com.code44.finance.ui.overview;

import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.api.User;
import com.code44.finance.ui.BaseActivity;
import com.code44.finance.utils.PeriodHelper;

import de.greenrobot.event.EventBus;

public class OverviewActivity extends BaseActivity {
    private static final String FRAGMENT_CURRENT = "FRAGMENT_CURRENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        updateFragment();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbarHelper.setTitle(PeriodHelper.get().getTitle());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(User.UserChangedEvent event) {
        updateFragment();
    }

    private void updateFragment() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_V, WelcomeFragment.newInstance(), FRAGMENT_CURRENT)
                .commit();
    }
}
