package com.code44.finance.ui.transactions;

import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;

import java.util.List;

public class TransactionController {
    private Transaction storedTransaction;

    private Account accountFrom;
    private Account accountTo;
    private Category category;
    private List<Tag> tags;
    private Long date;
    private Long amount;
    private Double exchangeRate;
    private String note;
    private TransactionState transactionState;
    private TransactionType transactionType;
    private Boolean includeInReports;

    private Account getAccountFrom() {
        if (accountFrom != null) {
            return accountFrom;
        }

        if (storedTransaction != null && storedTransaction.getAccountFrom() != null) {
            return storedTransaction.getAccountFrom();
        }

        return autoCompleteTransaction.getAccountFrom();
    }

    private Account getAccountTo() {
        if (accountTo != null) {
            return accountTo;
        }

        if (storedTransaction != null && storedTransaction.getAccountTo() != null) {
            return storedTransaction.getAccountTo();
        }

        return autoCompleteTransaction.getAccountTo();
    }

    private Category getCategory() {
        if (category != null) {
            return category;
        }

        if (storedTransaction != null && storedTransaction.getCategory() != null) {
            return storedTransaction.getCategory();
        }

        return autoCompleteTransaction.getCategory();
    }

    private List<Tag> getTags() {
        if (tags != null) {
            return tags;
        }

        if (storedTransaction != null && storedTransaction.getTags() != null) {
            return storedTransaction.getTags();
        }

        return autoCompleteTransaction.getTags();
    }

    private long getDate() {
        if (date != null) {
            return date;
        }

        if (storedTransaction != null) {
            return storedTransaction.getDate();
        }

        return autoCompleteTransaction.getDate();
    }

    private long getAmount() {

    }
}
