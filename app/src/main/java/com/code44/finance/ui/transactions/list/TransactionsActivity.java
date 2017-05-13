package com.code44.finance.ui.transactions.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.ui.common.activities.ActivityStarter;
import com.code44.finance.ui.common.activities.ModelsActivity;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.navigation.NavigationScreen;
import com.code44.finance.ui.common.recycler.DividerDecoration;
import com.code44.finance.ui.common.recycler.SectionsDecoration;
import com.code44.finance.ui.transactions.detail.TransactionActivity;
import com.code44.finance.ui.transactions.edit.TransactionEditActivity;
import com.code44.finance.utils.analytics.Screens;
import com.code44.finance.utils.interval.CurrentInterval;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class TransactionsActivity extends ModelsActivity<Transaction, TransactionsAdapter> {
    @Inject AmountFormatter amountFormatter;
    @Inject CurrentInterval currentInterval;

    public static void startView(Context context) {
        ActivityStarter.begin(context, TransactionsActivity.class).topLevel().modelsView().showDrawer().showDrawerToggle().start();
    }

    @Override protected void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override protected void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_transactions;
    }

    @Override protected TransactionsAdapter createAdapter(ModelsAdapter.OnModelClickListener<Transaction> defaultOnModelClickListener, Mode mode) {
        return new TransactionsAdapter(defaultOnModelClickListener, mode, this, amountFormatter, currentInterval);
    }

    @Override protected CursorLoader getModelsCursorLoader() {
        return Tables.Transactions.getQuery().asCursorLoader(this, TransactionsProvider.uriTransactions());
    }

    @Override protected void onModelClick(Transaction model) {
        TransactionActivity.start(this, model.getId());
    }

    @Override protected void startModelEdit(String modelId) {
        TransactionEditActivity.start(this, modelId);
    }

    @Override protected NavigationScreen getNavigationScreen() {
        return NavigationScreen.Transactions;
    }

    @Override protected void setupRecyclerViewDecorations(RecyclerView recyclerView) {
        final int keylineContent = getResources().getDimensionPixelSize(R.dimen.keyline_content);
        final DividerDecoration dividerDecoration = new DividerDecoration(this, keylineContent, 0, 0, 0, DividerDecoration.DRAW_DIVIDER_MIDDLE | DividerDecoration.DRAW_DIVIDER_END);
        final SectionsDecoration sectionDecoration = new SectionsDecoration(true);

        recyclerView.addItemDecoration(dividerDecoration);
        recyclerView.addItemDecoration(sectionDecoration);
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.TransactionList;
    }

    @Subscribe public void onIntervalChanged(CurrentInterval interval) {
        getAdapter().notifyDataSetChanged();
    }
}
