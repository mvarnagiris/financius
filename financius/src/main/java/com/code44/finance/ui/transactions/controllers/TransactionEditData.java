package com.code44.finance.ui.transactions.controllers;

import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
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
        if (transactionType != null) {
            return transactionType;
        }

        if (storedTransaction != null && storedTransaction.getTransactionType() != null) {
            return storedTransaction.getTransactionType();
        }

        return TransactionType.Expense;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public long getAmount() {
        if (amount != null) {
            return amount;
        }

        if (storedTransaction != null) {
            return storedTransaction.getAmount();
        }

        return 0;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public long getDate() {
        if (date != null) {
            return date;
        }

        if (storedTransaction != null) {
            return storedTransaction.getDate();
        }

        return System.currentTimeMillis();
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Account getAccountFrom() {
        if (getTransactionType() == TransactionType.Income) {
            return null;
        }

        if (accountFrom != null) {
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
    }

    public Account getAccountTo() {
        if (getTransactionType() == TransactionType.Expense) {
            return null;
        }

        if (accountTo != null) {
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
    }

    public Category getCategory() {
        if (getTransactionType() == TransactionType.Transfer) {
            return null;
        }

        if (category != null) {
            return category;
        }

        if (storedTransaction != null && storedTransaction.getCategory() != null) {
            return storedTransaction.getCategory();
        }

        if (autoCompleteResult != null) {
            return autoCompleteResult.getCategory();
        }

        return null;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Tag> getTags() {
        if (tags != null) {
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
    }

    public String getNote() {
        if (note != null) {
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
    }

    public TransactionState getTransactionState() {
        if (!canBeConfirmed()) {
            return TransactionState.Pending;
        }

        if (transactionState != null) {
            return transactionState;
        }

        if (storedTransaction != null && storedTransaction.getTransactionState() != null) {
            return storedTransaction.getTransactionState();
        }

        return TransactionState.Confirmed;
    }

    public void setTransactionState(TransactionState transactionState) {
        this.transactionState = transactionState;
    }

    public Boolean getIncludeInReports() {
        if (includeInReports != null) {
            return includeInReports;
        }

        return storedTransaction == null || storedTransaction.includeInReports();
    }

    public void setIncludeInReports(Boolean includeInReports) {
        this.includeInReports = includeInReports;
    }

    public Double getExchangeRate() {
        double exchangeRate;

        if (this.exchangeRate != null) {
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
        this.exchangeRate = exchangeRate;
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

    private boolean canBeConfirmed() {
        return validateAmount() && validateAccountFrom() && validateAccountTo() && validateAccounts();
    }

    private boolean validateAmount() {
        return getAmount() > 0;
    }

    private boolean validateAccountFrom() {
        return getTransactionType() == TransactionType.Income || (getAccountFrom() != null && getAccountFrom().hasId());
    }

    private boolean validateAccountTo() {
        return getTransactionType() == TransactionType.Expense || (getAccountTo() != null && getAccountTo().hasId());
    }

    private boolean validateAccounts() {
        return getTransactionType() != TransactionType.Transfer || !getAccountFrom().getId().equals(getAccountTo().getId());
    }
}
