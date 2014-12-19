package com.code44.finance.ui.transactions.controllers;

import android.view.View;
import android.widget.ImageButton;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.ViewController;

public class TransactionTypeViewController extends ViewController {
    private ImageButton transactionTypeImageButton;

    public TransactionTypeViewController(BaseActivity activity, View.OnClickListener clickListener) {
        transactionTypeImageButton = findView(activity, R.id.transactionTypeImageButton);

        transactionTypeImageButton.setOnClickListener(clickListener);
    }

    @Override public void showError(Throwable error) {
    }

    protected void setTransactionType(TransactionType transactionType) {
        final int color;
        switch (transactionType) {
            case Expense:
                color = transactionTypeImageButton.getContext().getResources().getColor(R.color.text_negative);
                break;
            case Income:
                color = transactionTypeImageButton.getContext().getResources().getColor(R.color.text_positive);
                break;
            case Transfer:
                color = transactionTypeImageButton.getContext().getResources().getColor(R.color.text_neutral);
                break;
            default:
                throw new IllegalArgumentException("Transaction type " + transactionType + " is not supported.");
        }
        transactionTypeImageButton.setColorFilter(color);
    }
}
