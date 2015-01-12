package com.code44.finance.ui.accounts.list;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.ui.accounts.AccountActivity;
import com.code44.finance.ui.accounts.AccountEditActivity;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.utils.MoneyFormatter;
import com.code44.finance.utils.ThemeUtils;

public class AccountsActivityPresenter extends ModelsActivityPresenter<Account> {
    private final Currency mainCurrency;

    private TextView balanceTextView;

    public AccountsActivityPresenter(Currency mainCurrency) {
        this.mainCurrency = mainCurrency;
    }

    @Override protected void setupRecyclerView(RecyclerView recyclerView) {
        super.setupRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new ThickDividerDecoration(recyclerView.getContext()));
    }

    @Override public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        balanceTextView = findView(activity, R.id.balanceTextView);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODELS) {
            updateBalance(data);
        }
        super.onLoadFinished(loader, data);
    }

    @Override protected ModelsAdapter<Account> createAdapter(ModelsAdapter.OnModelClickListener<Account> defaultOnModelClickListener) {
        return new AccountsAdapter(defaultOnModelClickListener, mainCurrency);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Accounts.getQuery().asCursorLoader(context, AccountsProvider.uriAccounts());
    }

    @Override protected void onModelClick(Context context, View view, Account model, Cursor cursor, int position) {
        AccountActivity.start(context, model.getId());
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        AccountEditActivity.start(context, modelId);
    }

    private void updateBalance(Cursor cursor) {
        long balance = 0;
        if (cursor.moveToFirst()) {
            do {
                final Account account = Account.from(cursor);
                if (account.includeInTotals()) {
                    balance += account.getBalance() * account.getCurrency().getExchangeRate();
                }
            } while (cursor.moveToNext());
        }
        balanceTextView.setText(MoneyFormatter.format(mainCurrency, balance));
    }

    private static class ThickDividerDecoration extends RecyclerView.ItemDecoration {
        private final Drawable dividerDrawable;

        private ThickDividerDecoration(Context context) {
            dividerDrawable = ThemeUtils.getDrawable(context, android.R.attr.dividerHorizontal);
        }

        @Override public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);

            final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            for (int i = 0, size = parent.getChildCount(); i < size; i++) {
                final View view = parent.getChildAt(i);
                if (!drawDivider(view, parent)) {
                    continue;
                }

                final int left = layoutManager.getDecoratedLeft(view);
                final int right = layoutManager.getDecoratedRight(view);
                final int top = layoutManager.getDecoratedTop(view);
                final int bottom = top + (dividerDrawable.getIntrinsicHeight() * 2);

                dividerDrawable.setBounds(left, top, right, bottom);
                dividerDrawable.draw(c);
            }
        }

        @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (!drawDivider(view, parent)) {
                return;
            }

            outRect.top += dividerDrawable.getIntrinsicHeight() * 2;
        }

        private boolean drawDivider(View view, RecyclerView parent) {
            if (((AccountsAdapter.ViewHolder) parent.getChildViewHolder(view)).getModel().includeInTotals()) {
                return false;
            }

            final int previousViewIndex = parent.indexOfChild(view) - 1;
            if (previousViewIndex < 0) {
                return false;
            }

            if (!((AccountsAdapter.ViewHolder) parent.getChildViewHolder(parent.getChildAt(previousViewIndex))).getModel().includeInTotals()) {
                return false;
            }

            return true;
        }
    }
}
