package com.code44.finance.api.requests;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.accounts.Accounts;
import com.code44.finance.backend.endpoint.accounts.model.AccountEntity;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Model;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.IOUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAccountsRequest extends GetRequest<AccountEntity> {
    private final Accounts accountsService;
    private final Map<String, Currency> currencies;

    public GetAccountsRequest(Context context, User user, Accounts accountsService) {
        super(null, context, user);
        Preconditions.notNull(accountsService, "Accounts cannot be null.");

        this.accountsService = accountsService;
        currencies = new HashMap<>();
    }

    @Override protected long getLastTimestamp(User user) {
        return user.getAccountsTimestamp();
    }

    @Override protected List<AccountEntity> performRequest(long timestamp) throws Exception {
        return accountsService.list(timestamp).execute().getItems();
    }

    @Override protected Model getModelFrom(AccountEntity entity) {
        return Account.from(entity, getCurrencyFor(entity));
    }

    @Override protected void saveNewTimestamp(User user, long newTimestamp) {
        user.setAccountsTimestamp(newTimestamp);
    }

    @Override protected Uri getSaveUri() {
        return AccountsProvider.uriAccounts();
    }

    private Currency getCurrencyFor(AccountEntity entity) {
        Currency currency = currencies.get(entity.getCurrencyId());

        if (currency == null) {
            final Cursor cursor = Query.create()
                    .projectionLocalId(Tables.Currencies.LOCAL_ID)
                    .projection(Tables.Currencies.PROJECTION)
                    .selection(Tables.Currencies.ID + "=?", entity.getCurrencyId())
                    .from(getContext(), CurrenciesProvider.uriCurrencies())
                    .execute();
            currency = Currency.from(cursor);
            IOUtils.closeQuietly(cursor);
            currencies.put(entity.getCurrencyId(), currency);
        }

        return currency;
    }
}
