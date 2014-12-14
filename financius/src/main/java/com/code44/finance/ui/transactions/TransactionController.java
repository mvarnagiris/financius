package com.code44.finance.ui.transactions;

import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;

import java.util.List;

public class TransactionController {
    private Transaction storedTransaction;
    private AutoCompleteResult autoCompleteResult;

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

    public void setStoredTransaction(Transaction storedTransaction) {
        this.storedTransaction = storedTransaction;
    }
}
