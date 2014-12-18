package com.code44.finance.ui.transactions.autocomplete;

import com.code44.finance.data.model.Transaction;

public interface FinderScore {
    public void add(Transaction transaction);

    public float getScore();
}
