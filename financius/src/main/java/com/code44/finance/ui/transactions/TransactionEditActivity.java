package com.code44.finance.ui.transactions;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.qualifiers.Local;
import com.code44.finance.ui.common.ModelEditActivity;
import com.code44.finance.ui.transactions.presenters.TransactionEditData;
import com.code44.finance.ui.transactions.presenters.TransactionPresenter;
import com.code44.finance.utils.analytics.Analytics;

import java.util.concurrent.Executor;

import javax.inject.Inject;

public class TransactionEditActivity extends ModelEditActivity<Transaction> implements TransactionPresenter.OnTransactionUpdatedListener {
    @Inject CurrenciesApi currenciesApi;
    @Inject CurrenciesManager currenciesManager;
    @Inject @Local Executor localExecutor;

    private Button saveButton;

    private TransactionPresenter transactionPresenter;

    public static void start(Context context, String transactionServerId) {
        startActivity(context, makeIntent(context, TransactionEditActivity.class, transactionServerId));
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_transaction_edit;
    }

    @Override protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        saveButton = (Button) findViewById(R.id.saveButton);
        transactionPresenter = new TransactionPresenter(this, modelId, savedInstanceState, getEventBus(), localExecutor, currenciesManager, currenciesApi, this);
    }

    @Override public void onResume() {
        super.onResume();
        transactionPresenter.onResume();
    }

    @Override public void onPause() {
        super.onPause();
        transactionPresenter.onPause();
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        transactionPresenter.onSaveInstanceState(outState);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        transactionPresenter.handleActivityResult(requestCode, resultCode, data);
    }

    @Override protected boolean onSave(Transaction model) {
        return transactionPresenter.save();
    }

    @Override protected void ensureModelUpdated(Transaction model) {
    }

    @Override protected CursorLoader getModelCursorLoader(String modelId) {
        return Tables.Transactions.getQuery().asCursorLoader(this, TransactionsProvider.uriTransaction(modelId));
    }

    @Override protected Transaction getModelFrom(Cursor cursor) {
        return Transaction.from(cursor);
    }

    @Override protected void onModelLoaded(Transaction transaction) {
        if (transaction.hasId()) {
            transactionPresenter.setStoredTransaction(transaction);
        }
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.TransactionEdit;
    }

    @Override public void onTransactionUpdated(TransactionEditData transactionEditData) {
        saveButton.setText(transactionEditData.getTransactionState() == TransactionState.Confirmed ? R.string.save : R.string.pending);
    }
}
