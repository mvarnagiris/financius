package com.code44.finance.api.endpoints.requests.accounts;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.api.endpoints.requests.GetEntitiesRequest;
import com.code44.finance.backend.financius.model.AccountEntity;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.utils.EventBus;

import java.util.List;

public class GetAccountsRequest extends GetEntitiesRequest<Account, AccountEntity> {
    public GetAccountsRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull User user) {
        super(eventBus, endpointFactory, context, user);
    }

    @Override protected long getLastUpdateTimestamp(User user) {
        return user.getLastAccountsUpdateTimestamp();
    }

    @Override protected List<AccountEntity> performRequest(long timestamp) throws Exception {
        return getEndpoint().listAccounts(timestamp).execute().getItems();
    }

    @Override protected Account getModelFrom(AccountEntity entity) {
        return Account.from(entity);
    }

    @Override protected Uri getSaveUri() {
        return AccountsProvider.uriAccounts();
    }

    @Override protected void saveLastUpdateTimestamp(User user, long lastUpdateTimestamp) {
        user.setLastAccountsUpdateTimestamp(lastUpdateTimestamp);
    }
}