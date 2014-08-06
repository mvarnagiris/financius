package com.code44.finance.api.financius.requests;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.accounts.model.AccountEntity;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.IOUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAccountsRequest extends FinanciusBaseRequest<Void> {
    private final Map<String, Currency> currencies;

    public GetAccountsRequest(Context context, User user) {
        super(null, context, user);
        currencies = new HashMap<>();
    }

    @Override
    protected Void performRequest() throws Exception {
        long accountsTimestamp = user.getAccountsTimestamp();
        final List<AccountEntity> accountEntities = getAccountsService().list(accountsTimestamp).execute().getItems();
        final List<ContentValues> accountValues = new ArrayList<>();

        for (AccountEntity entity : accountEntities) {
            accountValues.add(Account.from(entity, getCurrencyFor(entity)).asContentValues());
            if (accountsTimestamp < entity.getEditTs()) {
                accountsTimestamp = entity.getEditTs();
            }
        }

        DataStore.bulkInsert().values(accountValues).into(AccountsProvider.uriAccounts());
        user.setAccountsTimestamp(accountsTimestamp);
        return null;
    }

    private Currency getCurrencyFor(AccountEntity entity) {
        Currency currency = currencies.get(entity.getCurrencyId());

        if (currency == null) {
            final Cursor cursor = Query.create()
                    .projectionId(Tables.Currencies.ID)
                    .projection(Tables.Currencies.PROJECTION)
                    .selection(Tables.Currencies.SERVER_ID + "=?", entity.getCurrencyId())
                    .from(context, CurrenciesProvider.uriCurrencies())
                    .execute();
            currency = Currency.from(cursor);
            IOUtils.closeQuietly(cursor);
            currencies.put(entity.getCurrencyId(), currency);
        }

        return currency;
    }
}
