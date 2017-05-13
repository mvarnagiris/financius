package com.code44.finance.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.api.endpoints.EndpointsApi;
import com.code44.finance.common.interval.IntervalType;
import com.code44.finance.common.security.SecurityType;
import com.code44.finance.ui.categories.list.CategoriesActivity;
import com.code44.finance.ui.common.activities.ActivityStarter;
import com.code44.finance.ui.common.activities.DrawerActivity;
import com.code44.finance.ui.common.navigation.NavigationScreen;
import com.code44.finance.ui.currencies.list.CurrenciesActivity;
import com.code44.finance.ui.dialogs.ListDialogFragment;
import com.code44.finance.ui.settings.about.AboutActivity;
import com.code44.finance.ui.settings.data.DataActivity;
import com.code44.finance.ui.settings.security.LockActivity;
import com.code44.finance.ui.settings.security.Security;
import com.code44.finance.ui.settings.security.UnlockActivity;
import com.code44.finance.ui.tags.list.TagsActivity;
import com.code44.finance.utils.analytics.Screens;
import com.code44.finance.utils.interval.ActiveInterval;
import com.code44.finance.utils.interval.CurrentInterval;
import com.code44.finance.utils.preferences.GeneralPrefs;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SettingsActivity extends DrawerActivity implements AdapterView.OnItemClickListener {
    private static final int REQUEST_INTERVAL = 98527;
    private static final int REQUEST_UNLOCK = 2351;
    private static final int REQUEST_LOCK = 49542;

    private static final String FRAGMENT_INTERVAL = "FRAGMENT_INTERVAL";
    private static final String FRAGMENT_SECURITY = "FRAGMENT_SECURITY";

    @Inject GeneralPrefs generalPrefs;
    @Inject CurrentInterval currentInterval;
    @Inject ActiveInterval activeInterval;
    @Inject EndpointsApi endpointsApi;

    private SettingsAdapter adapter;
    private boolean isResumed = false;
    private boolean requestLock = false;

    public static void start(Context context) {
        ActivityStarter.begin(context, SettingsActivity.class).start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get views
        final ListView list_V = (ListView) findViewById(R.id.listView);

        // Setup
        adapter = new SettingsAdapter(this);
        list_V.setAdapter(adapter);
        list_V.setOnItemClickListener(this);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_UNLOCK:
                if (resultCode == RESULT_OK) {
                    requestLock();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override protected void onResume() {
        super.onResume();
        isResumed = true;
        getEventBus().register(this);

        if (requestLock) {
            requestLock();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        return true;
    }

    @Override protected void onPause() {
        super.onPause();
        isResumed = false;
        getEventBus().unregister(this);
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.Settings;
    }

    @Override protected NavigationScreen getNavigationScreen() {
        return NavigationScreen.Settings;
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id == SettingsAdapter.ID_CURRENCIES) {
            CurrenciesActivity.startView(this);
        } else if (id == SettingsAdapter.ID_CATEGORIES) {
            CategoriesActivity.startView(this);
        } else if (id == SettingsAdapter.ID_TAGS) {
            TagsActivity.startView(this);
        } else if (id == SettingsAdapter.ID_PERIOD) {
            requestInterval();
        } else if (id == SettingsAdapter.ID_SECURITY) {
            UnlockActivity.startForResult(this, REQUEST_UNLOCK, false);
        } else if (id == SettingsAdapter.ID_DATA) {
            DataActivity.start(this);
        } else if (id == SettingsAdapter.ID_ABOUT) {
            AboutActivity.start(this);
        }
    }

    @Subscribe public void onIntervalChanged(CurrentInterval intervalHelper) {
        adapter.setInterval(intervalHelper);
    }

    @Subscribe public void onListDialogItemSelected(ListDialogFragment.ListDialogEvent event) {
        if (event.isActionButtonClicked()) {
            return;
        }

        switch (event.getRequestCode()) {
            case REQUEST_INTERVAL:
                onIntervalSelected(event.getPosition());
                break;
            case REQUEST_LOCK:
                onLockSelected(event.getPosition());
                break;
            default:
                return;
        }

        event.dismiss();
    }

    @Subscribe public void onNewLockSelected(Security security) {
        adapter.setSecurity(security);
    }

    private void onIntervalSelected(int selectedPosition) {
        final IntervalType intervalType;
        final int length;
        switch (selectedPosition) {
            case 0:
                intervalType = IntervalType.Day;
                length = 1;
                break;
            case 1:
                intervalType = IntervalType.Week;
                length = 1;
                break;
            case 2:
                intervalType = IntervalType.Month;
                length = 1;
                break;
            case 3:
                intervalType = IntervalType.Year;
                length = 1;
                break;
            default:
                throw new IllegalArgumentException("Selected invalid position for interval.");
        }

        generalPrefs.setIntervalTypeAndLength(intervalType, length);
        generalPrefs.notifyChanged();
        currentInterval.setTypeAndLength(intervalType, length);
        activeInterval.setTypeAndLength(intervalType, length);
        endpointsApi.syncConfig();
    }

    private void onLockSelected(int selectedPosition) {
        final SecurityType securityType;
        switch (selectedPosition) {
            case 1:
                securityType = SecurityType.Pin;
                break;
            default:
                securityType = SecurityType.None;
                break;
        }

        LockActivity.start(this, securityType);
    }

    private void requestInterval() {
        final List<ListDialogFragment.ListDialogItem> items = new ArrayList<>();
        items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.day), generalPrefs.getIntervalIntervalType() == IntervalType.Day));
        items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.week), generalPrefs.getIntervalIntervalType() == IntervalType.Week));
        items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.month), generalPrefs.getIntervalIntervalType() == IntervalType.Month));
        items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.year), generalPrefs.getIntervalIntervalType() == IntervalType.Year));
        new ListDialogFragment.Builder(REQUEST_INTERVAL).setTitle(getString(R.string.period))
                .setItems(items)
                .setPositiveButtonText(getString(R.string.cancel))
                .build()
                .show(getSupportFragmentManager(), FRAGMENT_INTERVAL);
    }

    private void requestLock() {
        if (!isResumed) {
            requestLock = true;
            return;
        }
        requestLock = false;

        final List<ListDialogFragment.ListDialogItem> items = new ArrayList<>();
        items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.none), getSecurity().getSecurityType() == SecurityType.None));
        items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.pin), getSecurity().getSecurityType() == SecurityType.Pin));
        new ListDialogFragment.Builder(REQUEST_LOCK).setTitle(getString(R.string.security))
                .setItems(items)
                .setPositiveButtonText(getString(R.string.cancel))
                .build()
                .show(getSupportFragmentManager(), FRAGMENT_SECURITY);
    }
}
