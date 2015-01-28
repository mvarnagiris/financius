package com.code44.finance.ui.transactions.edit.autocomplete;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

public abstract class TransactionAutoComplete implements Runnable {
    private final Executor executor;
    private final TransactionAutoCompleteListener listener;
    private final AutoCompleteInput autoCompleteInput;

    protected TransactionAutoComplete(Executor executor, TransactionAutoCompleteListener listener, AutoCompleteInput autoCompleteInput) {
        this.executor = executor;
        this.listener = listener;
        this.autoCompleteInput = autoCompleteInput;
    }

    @Override public void run() {
        final AutoCompleteResult result = autoComplete(autoCompleteInput);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override public void run() {
                listener.onTransactionAutoComplete(result);
            }
        });
    }

    public void execute() {
        executor.execute(this);
    }

    protected abstract AutoCompleteResult autoComplete(AutoCompleteInput input);

    public static interface TransactionAutoCompleteListener {
        public void onTransactionAutoComplete(AutoCompleteResult result);
    }
}
