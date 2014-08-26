package com.code44.finance.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.adapters.SettingsAdapter;
import com.code44.finance.ui.BaseActivity;
import com.code44.finance.ui.categories.CategoriesActivity;
import com.code44.finance.ui.currencies.CurrenciesActivity;
import com.code44.finance.ui.dialogs.ListDialogFragment;
import com.code44.finance.ui.tags.TagsActivity;
import com.code44.finance.utils.IntervalHelper;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private static final int REQUEST_INTERVAL = 98527;

    private static final String FRAGMENT_INTERVAL = "FRAGMENT_INTERVAL";

    private final IntervalHelper intervalHelper = IntervalHelper.get();

    private SettingsAdapter adapter;

    public static void start(Context context) {
        start(context, makeIntent(context, SettingsActivity.class));
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbarHelper.setTitle(R.string.settings);

        // Get views
        final ListView list_V = (ListView) findViewById(R.id.list_V);

        // Setup
        adapter = new SettingsAdapter(this);
        list_V.setAdapter(adapter);
        list_V.setOnItemClickListener(this);
    }

    @Override protected void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return false;
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
            items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.day), intervalHelper.getType() == IntervalHelper.Type.DAY));
            items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.week), intervalHelper.getType() == IntervalHelper.Type.WEEK));
            items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.month), intervalHelper.getType() == IntervalHelper.Type.MONTH));
            items.add(new ListDialogFragment.SingleChoiceListDialogItem(getString(R.string.year), intervalHelper.getType() == IntervalHelper.Type.YEAR));
            new ListDialogFragment.Builder(REQUEST_INTERVAL)
                    .setTitle(getString(R.string.period))
                    .setItems(items)
                    .setPositiveButtonText(getString(R.string.cancel))
                    .build().show(getFragmentManager(), FRAGMENT_INTERVAL);
        } else if (id == SettingsAdapter.ID_DATA) {

        }
    }

    @Subscribe public void onIntervalChanged(IntervalHelper intervalHelper) {
        adapter.onIntervalChanged(intervalHelper);
    }

    @Subscribe public void onNewIntervalSelected(ListDialogFragment.ListDialogEvent event) {
        if (event.getRequestCode() != REQUEST_INTERVAL || !event.isListItemClicked()) {
            return;
        }

        final int selectedPosition = event.getPosition();
        switch (selectedPosition) {
            case 0:
                intervalHelper.setTypeAndLength(IntervalHelper.Type.DAY, 1);
                break;

            case 1:
                intervalHelper.setTypeAndLength(IntervalHelper.Type.WEEK, 1);
                break;

            case 2:
                intervalHelper.setTypeAndLength(IntervalHelper.Type.MONTH, 1);
                break;

            case 3:
                intervalHelper.setTypeAndLength(IntervalHelper.Type.YEAR, 1);
                break;
        }
        event.dismiss();
    }
}
