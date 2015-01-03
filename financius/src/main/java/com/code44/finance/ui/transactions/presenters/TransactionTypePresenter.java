package com.code44.finance.ui.transactions.presenters;

import android.view.View;
import android.widget.ImageView;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.Presenter;

public class TransactionTypePresenter extends Presenter {
    private final ImageView transactionTypeImageView;

    public TransactionTypePresenter(BaseActivity activity, View.OnClickListener clickListener) {
        final View transactionTypeContainerView = findView(activity, R.id.transactionTypeContainerView);
        transactionTypeImageView = findView(activity, R.id.transactionTypeImageView);

        transactionTypeContainerView.setOnClickListener(clickListener);
    }

    @Override public void showError(Throwable error) {
    }

    protected void setTransactionType(TransactionType transactionType) {
        final int color;
        switch (transactionType) {
            case Expense:
                color = transactionTypeImageView.getContext().getResources().getColor(R.color.text_negative);
                break;
            case Income:
                color = transactionTypeImageView.getContext().getResources().getColor(R.color.text_positive);
                break;
            case Transfer:
                color = transactionTypeImageView.getContext().getResources().getColor(R.color.text_neutral);
                break;
            default:
                throw new IllegalArgumentException("Transaction type " + transactionType + " is not supported.");
        }
        transactionTypeImageView.setColorFilter(color);
    }
}
