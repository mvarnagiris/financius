package com.code44.finance.api.financius.requests;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.transactions.model.TransactionEntity;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.Category;
import com.code44.finance.data.db.model.Transaction;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.utils.IOUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetTransactionsRequest extends FinanciusBaseRequest<Void> {
    private final Map<String, Account> accounts;
    private final Map<String, Category> categories;

    public GetTransactionsRequest(Context context, User user) {
        super(null, context, user);
        accounts = new HashMap<>();
        categories = new HashMap<>();
    }

    @Override
    protected Void performRequest() throws Exception {
        long transactionsTimestamp = user.getTransactionsTimestamp();
        final List<TransactionEntity> transactionEntities = getTransactionsService().list(transactionsTimestamp).execute().getItems();
        final List<ContentValues> transactionValues = new ArrayList<>();

        for (TransactionEntity entity : transactionEntities) {
            transactionValues.add(Transaction.from(entity, getAccountFor(entity.getAccountFromId()), getAccountFor(entity.getAccountToId()), getCategoryFor(entity)).asContentValues());
            if (transactionsTimestamp < entity.getEditTs()) {
                transactionsTimestamp = entity.getEditTs();
            }
        }

        DataStore.bulkInsert().values(transactionValues).into(TransactionsProvider.uriTransactions());
        user.setTransactionsTimestamp(transactionsTimestamp);
        return null;
    }

    private Account getAccountFor(String serverId) {
        Account account = accounts.get(serverId);

        if (account == null) {
            final Cursor cursor = Query.create()
                    .projectionId(Tables.Accounts.ID)
                    .projection(Tables.Accounts.PROJECTION)
                    .projection(Tables.Currencies.PROJECTION)
                    .selection(Tables.Accounts.SERVER_ID + "=?", serverId)
                    .from(context, AccountsProvider.uriAccounts())
                    .execute();
            account = Account.from(cursor);
            IOUtils.closeQuietly(cursor);
            accounts.put(serverId, account);
        }

        return account;
    }

    private Category getCategoryFor(TransactionEntity entity) {
        Category category = categories.get(entity.getId());

        if (category == null) {
            final Cursor cursor = Query.create()
                    .projectionId(Tables.Categories.ID)
                    .projection(Tables.Categories.PROJECTION)
                    .selection(Tables.Accounts.SERVER_ID + "=?", entity.getId())
                    .from(context, CategoriesProvider.uriCategories())
                    .execute();
            category = Category.from(cursor);
            IOUtils.closeQuietly(cursor);
            categories.put(entity.getId(), category);
        }

        return category;
    }
}
