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
        switch (transactionType) {
            case Expense:
                transactionTypeImageButton.setImageResource(R.drawable.ic_category_type_expense);
                break;
            case Income:
                transactionTypeImageButton.setImageResource(R.drawable.ic_category_type_income);
                break;
            case Transfer:
                transactionTypeImageButton.setImageResource(R.drawable.ic_category_type_transfer);
                break;
        }
    }
}
