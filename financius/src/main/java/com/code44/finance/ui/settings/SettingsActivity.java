package com.code44.finance.ui.settings;

import android.content.Context;
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
import com.code44.finance.ui.tags.TagsActivity;
import com.code44.finance.utils.ActiveInterval;
import com.code44.finance.utils.BaseInterval;
import com.code44.finance.utils.CurrentInterval;
import com.code44.finance.utils.GeneralPrefs;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SettingsActivity extends DrawerActivity implements AdapterView.OnItemClickListener {
    private static final int REQUEST_INTERVAL = 98527;

    private static final String FRAGMENT_INTERVAL = "FRAGMENT_INTERVAL";

    @Inject GeneralPrefs generalPrefs;
    @Inject CurrentInterval currentInterval;
    @Inject ActiveInterval activeInterval;

    private SettingsAdapter adapter;

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
        getEventBus().register(this);
    }

    @Override protected NavigationAdapter.NavigationScreen getNavigationScreen() {
        return null;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        return true;
    }

    @Override protected void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id == SettingsAdapter.ID_CURRENCIES) {
            CurrenciesActivity.start(this);
        } else if (id == SettingsAdapter.ID_CATEGORIES) {
            CategoriesActivity.start(this);
        } else if (id == SettingsAdapter.ID_TAGS) {
            TagsActivity.start(this);
        } else if (id == SettingsAdapter.ID_PERIOD) {
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
        } else if (id == SettingsAdapter.ID_DATA) {
            DataActivity.start(this);
        } else if (id == SettingsAdapter.ID_ABOUT) {
            AboutActivity.start(this);
        }
    }

    @Subscribe public void onIntervalChanged(CurrentInterval intervalHelper) {
        adapter.onIntervalChanged(intervalHelper);
    }

    @Subscribe public void onNewIntervalSelected(ListDialogFragment.ListDialogEvent event) {
        if (event.getRequestCode() != REQUEST_INTERVAL || event.isActionButtonClicked()) {
            return;
        }

        final int selectedPosition = event.getPosition();
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
        event.dismiss();
    }
}
