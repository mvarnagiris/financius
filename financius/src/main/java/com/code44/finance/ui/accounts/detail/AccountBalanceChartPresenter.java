package com.code44.finance.ui.accounts.detail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.code44.finance.common.model.TransactionState;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.ui.reports.AmountGroups;
import com.code44.finance.ui.reports.balance.BalanceChartPresenter;
import com.code44.finance.ui.reports.balance.BalanceChartView;
import com.code44.finance.utils.interval.BaseInterval;

import lecho.lib.hellocharts.model.Line;

public class AccountBalanceChartPresenter extends BalanceChartPresenter implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ACCOUNT_BALANCE = 7231;

    private final LoaderManager loaderManager;
    private BaseInterval baseInterval;
    private Account account;

    public AccountBalanceChartPresenter(BalanceChartView balanceChartView, CurrencyFormat mainCurrencyFormat, LoaderManager loaderManager) {
        super(balanceChartView, mainCurrencyFormat);
        this.loaderManager = loaderManager;
    }

    @Override protected AmountGroups.AmountCalculator getTransactionValidator() {
        return new AmountGroups.AmountCalculator() {
            @Override public boolean isTransactionValid(Transaction transaction) {
                return true;
            }
        };
    }

    @Override protected void onLineCreated(AmountGroups.AmountCalculator amountCalculator, Line line) {
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ACCOUNT_BALANCE) {
            return Tables.Transactions
                    .getQuery()
                    .selection(" and " + Tables.Transactions.DATE + " between ? and ?", String.valueOf(baseInterval.getInterval().getStartMillis()), String.valueOf(baseInterval.getInterval().getEndMillis() - 1))
                    .selection(" and (" + Tables.Transactions.ACCOUNT_FROM_ID + "=? or " + Tables.Transactions.ACCOUNT_TO_ID + "=?)", account.getId(), account.getId())
                    .selection(" and " + Tables.Transactions.STATE + "=?", TransactionState.Confirmed.asString())
                    .clearSort()
                    .sortOrder(Tables.Transactions.DATE.getName())
                    .asCursorLoader(getContext(), TransactionsProvider.uriTransactions());
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ACCOUNT_BALANCE) {
            setData(account, data, baseInterval);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void setAccountAndInterval(Account account, BaseInterval baseInterval) {
        this.account = account;
        this.baseInterval = baseInterval;
        loaderManager.restartLoader(LOADER_ACCOUNT_BALANCE, null, this);
    }
}
