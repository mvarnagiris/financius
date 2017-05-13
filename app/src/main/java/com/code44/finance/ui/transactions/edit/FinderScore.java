package com.code44.finance.ui.transactions.edit;

import com.code44.finance.data.model.Transaction;

interface FinderScore {
    void add(Transaction transaction);

    float getScore();
}
