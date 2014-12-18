package com.code44.finance.ui.transactions.controllers;

import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.ui.common.ViewController;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;

import java.util.List;

public class TransactionEditData {
    private Transaction storedTransaction;
    private AutoCompleteResult autoCompleteResult;

    private TransactionType transactionType;
    private Long amount;
    private Long date;
    private Account accountFrom;
    private Account accountTo;
    private Category category;
    private List<Tag> tags;
    private String note;
    private TransactionState transactionState;
    private Boolean includeInReports;
    private Double exchangeRate;

    private boolean isTransactionTypeSet = false;
    private boolean isAmountSet = false;
    private boolean isDateSet = false;
    private boolean isAccountFromSet = false;
    private boolean isAccountToSet = false;
    private boolean isCategorySet = false;
    private boolean isTagsSet = false;
    private boolean isNoteSet = false;
    private boolean isTransactionStateSet = false;
    private boolean isIncludeInReportsSet = false;
    private boolean isExchangeRateSet = false;

    public Transaction getStoredTransaction() {
        return storedTransaction;
    }

    public void setStoredTransaction(Transaction storedTransaction) {
        this.storedTransaction = storedTransaction;
    }

    public AutoCompleteResult getAutoCompleteResult() {
        return autoCompleteResult;
    }

    public void setAutoCompleteResult(AutoCompleteResult autoCompleteResult) {
        this.autoCompleteResult = autoCompleteResult;
    }

    public TransactionType getTransactionType() {
        if (isTransactionTypeSet || transactionType != null) {
            return transactionType;
        }

        if (storedTransaction != null && storedTransaction.getTransactionType() != null) {
            return storedTransaction.getTransactionType();
        }

        return TransactionType.Expense;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
        isTransactionTypeSet = true;
    }

    public long getAmount() {
        if (isAmountSet || amount != null) {
            return amount;
        }

        if (storedTransaction != null) {
            return storedTransaction.getAmount();
        }

        return 0;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
        isAmountSet = true;
    }

    public long getDate() {
        if (isDateSet || date != null) {
            return date;
        }

        if (storedTransaction != null) {
            return storedTransaction.getDate();
        }

        return System.currentTimeMillis();
    }

    public void setDate(Long date) {
        this.date = date;
        isDateSet = true;
    }

    public Account getAccountFrom() {
        if (getTransactionType() == TransactionType.Income) {
            return null;
        }

        if (isAccountFromSet || accountFrom != null) {
            return accountFrom;
        }

        if (storedTransaction != null && storedTransaction.getAccountFrom() != null) {
            return storedTransaction.getAccountFrom();
        }

        if (autoCompleteResult != null) {
            return autoCompleteResult.getAccountFrom();
        }

        return null;
    }

    public void setAccountFrom(Account accountFrom) {
        this.accountFrom = accountFrom;
        isAccountFromSet = true;
    }

    public Account getAccountTo() {
        if (getTransactionType() == TransactionType.Expense) {
            return null;
        }

        if (isAccountToSet || accountTo != null) {
            return accountTo;
        }

        if (storedTransaction != null && storedTransaction.getAccountTo() != null) {
            return storedTransaction.getAccountTo();
        }

        if (autoCompleteResult != null) {
            return autoCompleteResult.getAccountTo();
        }

        return null;
    }

    public void setAccountTo(Account accountTo) {
        this.accountTo = accountTo;
        isAccountToSet = true;
    }

    public Category getCategory() {
        if (getTransactionType() == TransactionType.Transfer) {
            return null;
        }

        if (isCategorySet || category != null) {
            return category;
        }

        if (storedTransaction != null && storedTransaction.getCategory() != null && storedTransaction.getCategory().getTransactionType() == getTransactionType()) {
            return storedTransaction.getCategory();
        }

        if (autoCompleteResult != null) {
            return autoCompleteResult.getCategory();
        }

        return null;
    }

    public void setCategory(Category category) {
        this.category = category;
        isCategorySet = true;
    }

    public List<Tag> getTags() {
        if (isTagsSet || tags != null) {
            return tags;
        }

        if (storedTransaction != null && storedTransaction.getTags() != null) {
            return storedTransaction.getTags();
        }

        if (autoCompleteResult != null) {
            return autoCompleteResult.getTags();
        }

        return null;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
        isTagsSet = true;
    }

    public String getNote() {
        if (isNoteSet || note != null) {
            return note;
        }

        if (storedTransaction != null && storedTransaction.getNote() != null) {
            return storedTransaction.getNote();
        }

        if (autoCompleteResult != null) {
            return autoCompleteResult.getNote();
        }

        return null;
    }

    public void setNote(String note) {
        this.note = note;
        isNoteSet = true;
    }

    public TransactionState getTransactionState() {
        if (!canBeConfirmed()) {
            return TransactionState.Pending;
        }

        if (isTransactionStateSet && transactionState != null) {
            return transactionState;
        }

        if (storedTransaction != null && storedTransaction.getTransactionState() != null) {
            return storedTransaction.getTransactionState();
        }

        return TransactionState.Confirmed;
    }

    public void setTransactionState(TransactionState transactionState) {
        this.transactionState = transactionState;
        isTransactionStateSet = true;
    }

    public boolean getIncludeInReports() {
        if (isIncludeInReportsSet && includeInReports != null) {
            return includeInReports;
        }

        return storedTransaction == null || storedTransaction.includeInReports();
    }

    public void setIncludeInReports(Boolean includeInReports) {
        this.includeInReports = includeInReports;
        isIncludeInReportsSet = true;
    }

    public double getExchangeRate() {
        double exchangeRate;

        if (isExchangeRateSet || this.exchangeRate != null) {
            exchangeRate = this.exchangeRate;
        } else if (storedTransaction != null) {
            exchangeRate = storedTransaction.getExchangeRate();
        } else {
            exchangeRate = 1;
        }

        if (Double.compare(exchangeRate, 0) <= 0) {
            exchangeRate = 1;
        }

        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        if (Double.compare(exchangeRate, 0) <= 0) {
            exchangeRate = 1.0;
        }
        this.exchangeRate = exchangeRate;
        isExchangeRateSet = true;
    }

    public boolean isTransactionTypeSet() {
        return isTransactionTypeSet || storedTransaction != null;
    }

    public boolean isAmountSet() {
        return isAmountSet || storedTransaction != null;
    }

    public boolean isDateSet() {
        return isDateSet || storedTransaction != null;
    }

    public boolean isAccountFromSet() {
        return isAccountFromSet || storedTransaction != null;
    }

    public boolean isAccountToSet() {
        return isAccountToSet || storedTransaction != null;
    }

    public boolean isCategorySet() {
        return isCategorySet || storedTransaction != null;
    }

    public boolean isTagsSet() {
        return isTagsSet || storedTransaction != null;
    }

    public boolean isNoteSet() {
        return isNoteSet || storedTransaction != null;
    }

    public boolean isTransactionStateSet() {
        return isTransactionStateSet || storedTransaction != null;
    }

    public boolean isIncludeInReportsSet() {
        return isIncludeInReportsSet || storedTransaction != null;
    }

    public boolean isExchangeRateSet() {
        return isExchangeRateSet || storedTransaction != null;
    }

    public Transaction getModel() {
        final Transaction transaction = new Transaction();
        transaction.setAccountFrom(getAccountFrom());
        transaction.setAccountTo(getAccountTo());
        transaction.setCategory(getCategory());
        transaction.setTags(getTags());
        transaction.setDate(getDate());
        transaction.setAmount(getAmount());
        transaction.setExchangeRate(getExchangeRate());
        transaction.setNote(getNote());
        transaction.setTransactionState(getTransactionState());
        transaction.setTransactionType(getTransactionType());
        transaction.setIncludeInReports(getIncludeInReports());
        return transaction;
    }

    public boolean canBeConfirmed() {
        return validateAmount(null) && validateAccountFrom(null) && validateAccountTo(null);
    }

    public boolean validateAmount(ViewController controller) {
        if (getAmount() > 0) {
            return true;
        }

        if (controller != null) {
            controller.showError(new Throwable());
        }

        return false;
    }

    public boolean validateAccountFrom(ViewController controller) {
        if (getTransactionType() == TransactionType.Income) {
            return true;
        }

        if (getAccountFrom() == null) {
            if (controller != null) {
                controller.showError(new Throwable());
            }
            return false;
        }

        if (getTransactionType() == TransactionType.Transfer && getAccountFrom().equals(getAccountTo())) {
            if (controller != null) {
                controller.showError(new Throwable());
            }
            return false;
        }

        return true;
    }

    public boolean validateAccountTo(ViewController controller) {
        if (getTransactionType() == TransactionType.Expense) {
            return true;
        }

        if (getAccountTo() == null) {
            if (controller != null) {
                controller.showError(new Throwable());
            }
            return false;
        }

        if (getTransactionType() == TransactionType.Transfer && getAccountTo().equals(getAccountFrom())) {
            if (controller != null) {
                controller.showError(new Throwable());
            }
            return false;
        }

        return true;
    }
}
