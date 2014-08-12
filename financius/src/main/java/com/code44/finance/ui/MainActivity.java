package com.code44.finance.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.services.StartupService;
import com.code44.finance.ui.accounts.AccountsFragment;
import com.code44.finance.ui.overview.OverviewFragment;
import com.code44.finance.ui.transactions.TransactionsFragment;
import com.code44.finance.ui.user.UserFragment;

public class MainActivity extends BaseActivity implements NavigationFragment.NavigationListener {
    private static final String FRAGMENT_CONTENT = "FRAGMENT_CONTENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        if (savedInstanceState == null) {
            StartupService.start(this);
        }

        final BaseFragment fragment = (BaseFragment) getFragmentManager().findFragmentByTag(FRAGMENT_CONTENT);
        if (fragment == null) {
            ((NavigationFragment) getFragmentManager().findFragmentById(R.id.navigation_F)).select(NavigationAdapter.NAV_ID_OVERVIEW);
        } else {
            onFragmentLoaded(fragment);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (toolbarHelper != null && !(fragment instanceof NavigationFragment)) {
            onFragmentLoaded((BaseFragment) fragment);
        }
    }

    @Override
    public void onNavigationItemSelected(NavigationAdapter.NavigationItem item) {
        BaseFragment baseFragment = null;
        switch (item.getId()) {
            case NavigationAdapter.NAV_ID_USER:
                baseFragment = UserFragment.newInstance();
                break;

            case NavigationAdapter.NAV_ID_OVERVIEW:
                baseFragment = OverviewFragment.newInstance();
                break;

            case NavigationAdapter.NAV_ID_ACCOUNTS:
                baseFragment = AccountsFragment.newInstance(ModelListFragment.Mode.VIEW);
                break;

            case NavigationAdapter.NAV_ID_TRANSACTIONS:
                baseFragment = TransactionsFragment.newInstance();
                break;
        }

        toolbarHelper.closeDrawer();
        if (baseFragment != null) {
            loadFragment(baseFragment);
        }
    }

    private void loadFragment(BaseFragment fragment) {
        final FragmentManager fm = getFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content_V, fragment, FRAGMENT_CONTENT);
        if (fm.findFragmentByTag(FRAGMENT_CONTENT) != null) {
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }
        ft.commit();
    }

    private void onFragmentLoaded(BaseFragment fragment) {
        if (fragment instanceof OverviewFragment) {
            toolbarHelper.setElevation(0);
        } else {
            toolbarHelper.setElevation(getResources().getDimension(R.dimen.elevation_header));
        }
        toolbarHelper.setTitle(fragment.getTitle());
    }
}
