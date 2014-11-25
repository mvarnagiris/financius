package com.code44.finance.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.adapters.SettingsAdapter;
import com.code44.finance.ui.DrawerActivity;
import com.code44.finance.ui.categories.CategoriesActivity;
import com.code44.finance.ui.currencies.CurrenciesActivity;
import com.code44.finance.ui.dialogs.ListDialogFragment;
import com.code44.finance.ui.settings.about.AboutActivity;
import com.code44.finance.ui.settings.data.DataActivity;
import com.code44.finance.ui.settings.security.LockActivity;
import com.code44.finance.ui.settings.security.Security;
import com.code44.finance.ui.settings.security.UnlockActivity;
import com.code44.finance.ui.tags.TagsActivity;
import com.code44.finance.utils.ActiveInterval;
import com.code44.finance.utils.BaseInterval;
import com.code44.finance.utils.CurrentInterval;
import com.code44.finance.utils.GeneralPrefs;
import com.code44.finance.utils.analytics.Analytics;
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

    private SettingsAdapter adapter;
    private boolean isResumed = false;
    private boolean requestLock = false;

    public static void start(Context context) {
        startActivity(context, makeIntentForActivity(context, SettingsActivity.class));
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

    @Override protected void onResume() {
        super.onResume();
        isResumed = true;
        getEventBus().register(this);

        if (requestLock) {
            requestLock();
        }
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

    @Override protected NavigationAdapter.NavigationScreen getNavigationScreen() {
        return null;
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id == SettingsAdapter.ID_CURRENCIES) {
            CurrenciesActivity.start(this);
        } else if (id == SettingsAdapter.ID_CATEGORIES) {
            CategoriesActivity.start(this);
        } else if (id == SettingsAdapter.ID_TAGS) {
            TagsActivity.start(this);
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

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.Settings;
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
        final BaseInterval.Type type;
        final int length;
        switch (selectedPosition) {
            case 0:
                type = BaseInterval.Type.DAY;
                length = 1;
                break;
            case 1:
                type = BaseInterval.Type.WEEK;
                length = 1;
                break;
            case 2:
                type = BaseInterval.Type.MONTH;
                length = 1;
                break;
            case 3:
                type = BaseInterval.Type.YEAR;
                length = 1;
                break;
            default:
                throw new IllegalArgumentException("Selected invalid position for interval.");
        }

        generalPrefs.setIntervalTypeAndLength(type, length);
        currentInterval.setTypeAndLength(type, length);
        activeInterval.setTypeAndLength(type, length);
    }

    private void onLockSelected(int selectedPosition) {
        final Security.Type type;
        switch (selectedPosition) {
            case 1:
                type = Security.Type.Pin;
                break;
            default:
                type = Security.Type.None;
                break;
        }

        LockActivity.start(this, type);
    }

    private void requestInterval() {
        final List<ListDialogFragment.ListDialogItem> items = new ArrayList<>();
        items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.day), generalPrefs.getIntervalType() == BaseInterval.Type.DAY));
        items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.week), generalPrefs.getIntervalType() == BaseInterval.Type.WEEK));
        items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.month), generalPrefs.getIntervalType() == BaseInterval.Type.MONTH));
        items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.year), generalPrefs.getIntervalType() == BaseInterval.Type.YEAR));
        new ListDialogFragment.Builder(REQUEST_INTERVAL)
                .setTitle(getString(R.string.period))
                .setItems(items)
                .setPositiveButtonText(getString(R.string.cancel))
                .build().show(getSupportFragmentManager(), FRAGMENT_INTERVAL);
    }

    private void requestLock() {
        if (!isResumed) {
            requestLock = true;
            return;
        }
        requestLock = false;

        final List<ListDialogFragment.ListDialogItem> items = new ArrayList<>();
        items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.none), getSecurity().getType() == Security.Type.None));
        items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.pin), getSecurity().getType() == Security.Type.Pin));
        new ListDialogFragment.Builder(REQUEST_LOCK)
                .setTitle(getString(R.string.security))
                .setItems(items)
                .setPositiveButtonText(getString(R.string.cancel))
                .build().show(getSupportFragmentManager(), FRAGMENT_SECURITY);
    }
}
