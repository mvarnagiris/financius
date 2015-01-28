package com.code44.finance.ui.transactions.list;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.ui.common.recycler.DividerDecoration;
import com.code44.finance.ui.common.recycler.SectionsDecoration;
import com.code44.finance.ui.transactions.detail.TransactionActivity;
import com.code44.finance.ui.transactions.edit.TransactionEditActivity;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.interval.CurrentInterval;
import com.squareup.otto.Subscribe;

class TransactionsActivityPresenter extends ModelsActivityPresenter<Transaction> {
    private final EventBus eventBus;
    private final AmountFormatter amountFormatter;
    private final CurrentInterval currentInterval;

    public TransactionsActivityPresenter(EventBus eventBus, AmountFormatter amountFormatter, CurrentInterval currentInterval) {
        this.eventBus = eventBus;
        this.amountFormatter = amountFormatter;
        this.currentInterval = currentInterval;
    }

    @Override public void onResume(BaseActivity activity) {
        super.onResume(activity);
        eventBus.register(this);
    }

    @Override public void onPause(BaseActivity activity) {
        super.onPause(activity);
        eventBus.unregister(this);
    }

    @Override protected ModelsAdapter<Transaction> createAdapter(ModelsAdapter.OnModelClickListener<Transaction> defaultOnModelClickListener) {
        return new TransactionsAdapter(defaultOnModelClickListener, amountFormatter, currentInterval);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Transactions.getQuery().asCursorLoader(context, TransactionsProvider.uriTransactions());
    }

    @Override protected void onModelClick(Context context, View view, Transaction model, Cursor cursor, int position) {
        TransactionActivity.start(context, model.getId());
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        TransactionEditActivity.start(context, modelId);
    }

    @Subscribe public void onIntervalChanged(CurrentInterval interval) {
        getAdapter().notifyDataSetChanged();
    }

    @Override protected RecyclerView.ItemDecoration[] getItemDecorations() {
        final Context context = getActivity();
        final RecyclerView.ItemDecoration dividerDecoration = new DividerDecoration(context).setPaddingLeft(context.getResources().getDimensionPixelSize(R.dimen.keyline_content));
        final RecyclerView.ItemDecoration sectionDecoration = new SectionsDecoration(true);
        return new RecyclerView.ItemDecoration[]{dividerDecoration, sectionDecoration};
    }
}
