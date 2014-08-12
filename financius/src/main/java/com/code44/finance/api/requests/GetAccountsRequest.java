package com.code44.finance.api.requests;

import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.App;
import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.accounts.Accounts;
import com.code44.finance.backend.endpoint.accounts.model.AccountEntity;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.IOUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class GetAccountsRequest extends GetRequest<AccountEntity> {
    private final Map<String, Currency> currencies;
    @Inject Accounts accountsService;

    public GetAccountsRequest() {
        currencies = new HashMap<>();
    }

    @Override protected long getLastTimestamp(User user) {
        return user.getAccountsTimestamp();
    }

    @Override protected List<AccountEntity> performRequest(long timestamp) throws Exception {
        return accountsService.list(timestamp).execute().getItems();
    }

    @Override protected BaseModel getModelFrom(AccountEntity entity) {
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
                    .projectionId(Tables.Currencies.ID)
                    .projection(Tables.Currencies.PROJECTION)
                    .selection(Tables.Currencies.SERVER_ID + "=?", entity.getCurrencyId())
                    .from(App.getContext(), CurrenciesProvider.uriCurrencies())
                    .execute();
            currency = Currency.from(cursor);
            IOUtils.closeQuietly(cursor);
            currencies.put(entity.getCurrencyId(), currency);
        }

        return currency;
    }
}
