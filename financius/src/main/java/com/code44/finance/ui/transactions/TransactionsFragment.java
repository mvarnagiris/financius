package com.code44.finance.ui.transactions;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.code44.finance.R;
import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.adapters.TransactionsAdapter;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.BaseModel;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.utils.CurrentInterval;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class TransactionsFragment extends ModelListFragment {
    @Inject @Main Currency mainCurrency;
    @Inject CurrentInterval currentInterval;

    private ExpandableStickyListHeadersListView headerList_V;

    public static TransactionsFragment newInstance() {
        final Bundle args = makeArgs(Mode.VIEW, null);

        final TransactionsFragment fragment = new TransactionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override public void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @Override protected BaseModelsAdapter createAdapter(Context context) {
        return new TransactionsAdapter(context, mainCurrency, currentInterval);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Transactions.getQuery().asCursorLoader(context, TransactionsProvider.uriTransactions());
    }

    @Override protected BaseModel modelFrom(Cursor cursor) {
        return Transaction.from(cursor);
    }

    @Override protected void onModelClick(Context context, View view, int position, String modelServerId, BaseModel model) {
        TransactionActivity.start(context, modelServerId);
    }

    @Override protected void startModelEdit(Context context, String modelServerId) {
        TransactionEditActivity.start(context, modelServerId);
    }

    @Override protected void prepareView(View view, BaseModelsAdapter adapter) {
        // Get views
        headerList_V = (ExpandableStickyListHeadersListView) view.findViewById(R.id.headerList_V);

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

    @Subscribe public void onIntervalChanged(CurrentInterval interval) {
        adapter.notifyDataSetChanged();
    }
}
