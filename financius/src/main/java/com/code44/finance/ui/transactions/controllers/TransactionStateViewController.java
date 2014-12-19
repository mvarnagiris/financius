package com.code44.finance.ui.transactions.controllers;

import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.ViewController;

public class TransactionStateViewController extends ViewController {
    private final CheckBox confirmedCheckBox;
    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    public TransactionStateViewController(BaseActivity activity, CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        confirmedCheckBox = findView(activity, R.id.confirmedCheckBox);

        onCheckedChangeListener = checkedChangeListener;
        confirmedCheckBox.setOnCheckedChangeListener(checkedChangeListener);
    }

    @Override public void showError(Throwable error) {
    }

    public void setTransactionState(TransactionState transactionState) {
        confirmedCheckBox.setOnCheckedChangeListener(null);
        confirmedCheckBox.setChecked(transactionState == TransactionState.Confirmed);
        confirmedCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
    }
}
