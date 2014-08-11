package com.code44.finance.api.requests;

import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.transactions.Transactions;
import com.code44.finance.backend.endpoint.transactions.model.TransactionEntity;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Category;
import com.code44.finance.data.db.model.Transaction;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.utils.IOUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class GetTransactionsRequest extends GetRequest<TransactionEntity> {
    private final Map<String, Account> accounts;
    private final Map<String, Category> categories;
    @Inject Transactions transactionsService;

    public GetTransactionsRequest() {
        accounts = new HashMap<>();
        categories = new HashMap<>();
    }

    @Override protected long getLastTimestamp(User user) {
        return user.getTransactionsTimestamp();
    }

    @Override protected List<TransactionEntity> performRequest(long timestamp) throws Exception {
        return transactionsService.list(timestamp).execute().getItems();
    }

    @Override protected BaseModel getModelFrom(TransactionEntity entity) {
        return Transaction.from(entity, getAccountFor(entity.getAccountFromId()), getAccountFor(entity.getAccountToId()), getCategoryFor(entity));
    }

    @Override protected void saveNewTimestamp(User user, long newTimestamp) {
        user.setTransactionsTimestamp(newTimestamp);
    }

    @Override protected Uri getSaveUri() {
        return TransactionsProvider.uriTransactions();
    }

    private Account getAccountFor(String serverId) {
        Account account = accounts.get(serverId);

        if (account == null) {
            final Cursor cursor = Query.create()
                    .projectionId(Tables.Accounts.ID)
                    .projection(Tables.Accounts.PROJECTION)
                    .projection(Tables.Currencies.PROJECTION)
                    .selection(Tables.Accounts.SERVER_ID + "=?", serverId)
                    .from(AccountsProvider.uriAccounts())
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
                    .from(CategoriesProvider.uriCategories())
                    .execute();
            category = Category.from(cursor);
            IOUtils.closeQuietly(cursor);
            categories.put(entity.getId(), category);
        }

        return category;
    }
}
