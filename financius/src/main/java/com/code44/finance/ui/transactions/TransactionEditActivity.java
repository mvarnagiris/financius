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
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.qualifiers.Local;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.ui.common.ModelEditActivity;
import com.code44.finance.ui.transactions.controllers.TransactionController;
import com.code44.finance.ui.transactions.controllers.TransactionEditData;
import com.code44.finance.utils.analytics.Analytics;

import java.util.concurrent.Executor;

import javax.inject.Inject;

public class TransactionEditActivity extends ModelEditActivity<Transaction> implements TransactionController.OnTransactionUpdatedListener {
    @Inject CurrenciesApi currenciesApi;
    @Inject @Main Currency mainCurrency;
    @Inject @Local Executor localExecutor;

    private Button saveButton;

    private TransactionController transactionController;

    public static void start(Context context, String transactionServerId) {
        startActivity(context, makeIntent(context, TransactionEditActivity.class, transactionServerId));
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_transaction_edit;
    }

    @Override protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        saveButton = (Button) findViewById(R.id.saveButton);
        transactionController = new TransactionController(this, getEventBus(), localExecutor, mainCurrency, currenciesApi, this);
    }

    @Override public void onResume() {
        super.onResume();
        transactionController.onResume();
    }

    @Override public void onPause() {
        super.onPause();
        transactionController.onPause();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        transactionController.handleActivityResult(requestCode, resultCode, data);
    }

    @Override protected boolean onSave(Transaction model) {
        return transactionController.save();
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
            transactionController.setStoredTransaction(transaction);
        }
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.TransactionEdit;
    }

    @Override public void onTransactionUpdated(TransactionEditData transactionEditData) {
        saveButton.setText(transactionEditData.getTransactionState() == TransactionState.Confirmed ? R.string.save : R.string.pending);
    }
}
