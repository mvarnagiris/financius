package com.code44.finance.ui.transactions.edit;

import android.support.annotation.NonNull;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.ui.common.activities.ModelEditActivity;
import com.code44.finance.utils.ThemeUtils;

import static com.google.common.base.Preconditions.checkNotNull;

class TransactionEditValidator implements ModelEditActivity.ModelEditValidator<AutocompleteTransactionEditData> {
    private final Button amountButton;
    private final Button accountFromButton;
    private final Button accountToButton;

    public TransactionEditValidator(@NonNull Button amountButton, @NonNull Button accountFromButton, @NonNull Button accountToButton) {
        this.amountButton = checkNotNull(amountButton, "Amount Button cannot be null.");
        this.accountFromButton = checkNotNull(accountFromButton, "Account from Button cannot be null.");
        this.accountToButton = checkNotNull(accountToButton, "Account to Button cannot be null.");
    }

    @Override public boolean validate(@NonNull AutocompleteTransactionEditData modelEditData, boolean showError) {
        boolean isValid = true;

        if (!validateAmount(modelEditData)) {
            isValid = false;
            if (showError) {
                amountButton.setTextColor(ThemeUtils.getColor(amountButton.getContext(), R.attr.colorError));
            }
        }

        if (!validateAccountFrom(modelEditData)) {
            isValid = false;
            if (showError) {
                accountFromButton.setHintTextColor(ThemeUtils.getColor(accountFromButton.getContext(), R.attr.colorError));
            }
        }

        if (!validateAccountTo(modelEditData)) {
            isValid = false;
            if (showError) {
                accountToButton.setHintTextColor(ThemeUtils.getColor(accountToButton.getContext(), R.attr.colorError));
            }
        }

        return isValid;
    }

    public boolean canBeConfirmed(@NonNull TransactionEditData transactionEditData) {
        return validateAmount(transactionEditData) && validateAccountFrom(transactionEditData) && validateAccountTo(transactionEditData);
    }

    public boolean validateAmount(@NonNull TransactionEditData transactionEditData) {
        return transactionEditData.getAmount() > 0;
    }

    public boolean validateAccountFrom(@NonNull TransactionEditData transactionEditData) {
        if (transactionEditData.getTransactionType() == TransactionType.Income) {
            return true;
        }

        if (transactionEditData.getAccountFrom() == null) {
            return false;
        }

        final boolean isBothAccountsEqual = transactionEditData.getAccountFrom().equals(transactionEditData.getAccountTo());
        return !(transactionEditData.getTransactionType() == TransactionType.Transfer && isBothAccountsEqual);
    }

    public boolean validateAccountTo(@NonNull TransactionEditData transactionEditData) {
        if (transactionEditData.getTransactionType() == TransactionType.Expense) {
            return true;
        }

        if (transactionEditData.getAccountTo() == null) {
            return false;
        }

        final boolean isBothAccountsEqual = transactionEditData.getAccountTo().equals(transactionEditData.getAccountFrom());
        return !(transactionEditData.getTransactionType() == TransactionType.Transfer && isBothAccountsEqual);
    }
}
