package com.code44.finance.utils.analytics;

import android.content.Context;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.utils.CategoryUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.common.base.Strings;

import java.util.Map;

public class Events {
    private final Tracker tracker;

    public Events(Tracker tracker) {
        this.tracker = tracker;
    }

    public void createTransaction(Context context, Transaction transaction) {
        final String currency;
        final Account account;
        if (transaction.getTransactionType() == TransactionType.Income) {
            account = transaction.getAccountTo();
        } else {
            account = transaction.getAccountFrom();
        }

        if (account != null) {
            currency = account.getCurrencyCode();
        } else {
            currency = "Unknown";
        }

        final Map<String, String> event = new HitBuilders.EventBuilder().setCategory(Category.Transaction.getName())
                .setAction(Action.Create.getName())
                .setLabel(transaction.getTransactionType().toString())
                .set("Transaction state", transaction.getTransactionState().toString())
                .set("Amount", String.valueOf(transaction.getAmount()))
                .set("Currency", currency)
                .set("Category", CategoryUtils.getTitle(context, transaction))
                .set("Tag count", transaction.getTags() == null ? "0" : String.valueOf(transaction.getTags().size()))
                .set("Note length", Strings.isNullOrEmpty(transaction.getNote()) ? "0" : String.valueOf(transaction.getNote().length()))
                .build();

        tracker.send(event);
    }

    private enum Category {
        Transaction("Transaction");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private enum Action {
        Create("Create");

        private final String name;

        Action(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
