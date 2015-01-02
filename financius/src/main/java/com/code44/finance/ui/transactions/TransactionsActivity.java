package com.code44.finance.ui.transactions;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.view.View;
import android.widget.ImageView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Model;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.ui.common.BaseModelsAdapter;
import com.code44.finance.ui.common.ModelListActivity;
import com.code44.finance.ui.navigation.NavigationAdapter;
import com.code44.finance.utils.CurrentInterval;
import com.code44.finance.utils.analytics.Analytics;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class TransactionsActivity extends ModelListActivity {
    @Inject @Main Currency mainCurrency;
    @Inject CurrentInterval currentInterval;

    private ExpandableStickyListHeadersListView headerList_V;

    public static Intent makeViewIntent(Context context) {
        return makeViewIntent(context, TransactionsActivity.class);
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_transactions;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        setShowDrawer(true);
        setShowDrawerToggle(true);
        super.onCreate(savedInstanceState);
    }

    @Override protected void onSetupList(BaseModelsAdapter adapter) {
        // Get views
        headerList_V = (ExpandableStickyListHeadersListView) findViewById(R.id.headerList);

        // Setup
        headerList_V.setAdapter((TransactionsAdapter) adapter);
        headerList_V.setOnItemClickListener(this);
        headerList_V.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (headerList_V.isHeaderCollapsed(headerId)) {
                    headerList_V.expand(headerId);
                    ((ImageView) header.findViewById(R.id.arrow_IV)).setImageResource(R.drawable.ic_arrow_down_small);
                } else {
                    headerList_V.collapse(headerId);
                    ((ImageView) header.findViewById(R.id.arrow_IV)).setImageResource(R.drawable.ic_arrow_right_small);
                }
            }
        });
    }

    @Override public void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @Override protected BaseModelsAdapter createAdapter() {
        return new TransactionsAdapter(this, mainCurrency, currentInterval);
    }

    @Override protected CursorLoader getModelsCursorLoader() {
        return Tables.Transactions.getQuery().asCursorLoader(this, TransactionsProvider.uriTransactions());
    }

    @Override protected Model modelFrom(Cursor cursor) {
        return Transaction.from(cursor);
    }

    @Override protected void onModelClick(View view, int position, String modelId, Model model) {
        TransactionActivity.start(this, modelId);
    }

    @Override protected void startModelEdit(String modelId) {
        TransactionEditActivity.start(this, modelId);
    }

    @Override protected NavigationAdapter.NavigationScreen getNavigationScreen() {
        return NavigationAdapter.NavigationScreen.Transactions;
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.TransactionList;
    }

    @Subscribe public void onIntervalChanged(CurrentInterval interval) {
        adapter.notifyDataSetChanged();
    }
}
