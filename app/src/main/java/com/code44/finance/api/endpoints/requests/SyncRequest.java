package com.code44.finance.api.endpoints.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import com.code44.finance.api.Result;
import com.code44.finance.api.endpoints.Device;
import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.EndpointRequest;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.api.endpoints.requests.accounts.GetAccountsRequest;
import com.code44.finance.api.endpoints.requests.accounts.SendAccountsRequest;
import com.code44.finance.api.endpoints.requests.categories.GetCategoriesRequest;
import com.code44.finance.api.endpoints.requests.categories.SendCategoriesRequest;
import com.code44.finance.api.endpoints.requests.configs.GetConfigRequest;
import com.code44.finance.api.endpoints.requests.currencies.GetCurrenciesRequest;
import com.code44.finance.api.endpoints.requests.currencies.SendCurrenciesRequest;
import com.code44.finance.api.endpoints.requests.tags.GetTagsRequest;
import com.code44.finance.api.endpoints.requests.tags.SendTagsRequest;
import com.code44.finance.api.endpoints.requests.transactions.GetTransactionsRequest;
import com.code44.finance.api.endpoints.requests.transactions.SendTransactionsRequest;
import com.code44.finance.api.endpoints.requests.users.GetUserRequest;
import com.code44.finance.backend.financius.model.ConfigEntity;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.settings.security.Security;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.interval.ActiveInterval;
import com.code44.finance.utils.interval.CurrentInterval;
import com.code44.finance.utils.preferences.GeneralPrefs;

import static com.google.common.base.Preconditions.checkNotNull;

public class SyncRequest extends EndpointRequest<Void> {
    private final Context context;
    private final User user;
    private final DBHelper dbHelper;
    private final Device device;
    private final CurrenciesManager currenciesManager;
    private final GeneralPrefs generalPrefs;
    private final Security security;
    private final CurrentInterval currentInterval;
    private final ActiveInterval activeInterval;

    public SyncRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull User user, @NonNull DBHelper dbHelper, @NonNull Device device, @NonNull CurrenciesManager currenciesManager, @NonNull GeneralPrefs generalPrefs, @NonNull Security security, @NonNull CurrentInterval currentInterval, @NonNull ActiveInterval activeInterval) {
        super(checkNotNull(eventBus, "EventBus cannot be null."), endpointFactory);
        this.context = checkNotNull(context, "Context cannot be null.").getApplicationContext();
        this.user = checkNotNull(user, "User cannot be null.");
        this.dbHelper = checkNotNull(dbHelper, "DBHelper cannot be null.");
        this.device = checkNotNull(device, "Device cannot be null.");
        this.currenciesManager = checkNotNull(currenciesManager, "CurrenciesManager cannot be null.");
        this.generalPrefs = checkNotNull(generalPrefs, "GeneralPrefs cannot be null.");
        this.security = checkNotNull(security, "Security cannot be null.");
        this.currentInterval = checkNotNull(currentInterval, "CurrentInterval cannot be null.");
        this.activeInterval = checkNotNull(activeInterval, "ActiveInterval cannot be null.");
    }

    @SuppressWarnings("ConstantConditions") @Override protected Void performRequest() throws Exception {
        // Config
        final ConfigEntity configEntity = getConfigEntity();
        user.setLastConfigUpdateTimestamp(configEntity.getConfigUpdateTimestamp());

        // User
        if (user.getLastUserUpdateTimestamp() != configEntity.getUserUpdateTimestamp()) {
            new GetUserRequest(eventBus, endpointFactory, user).call();
            user.setLastUserUpdateTimestamp(configEntity.getUserUpdateTimestamp());
        }

        // Currencies
        if (user.getLastCurrenciesUpdateTimestamp() != configEntity.getCurrenciesUpdateTimestamp()) {
            new GetCurrenciesRequest(eventBus, endpointFactory, context, user).call();
        }
        new SendCurrenciesRequest(eventBus, endpointFactory, context, user, dbHelper, device).call();

        // Tags
        if (user.getLastTagsUpdateTimestamp() != configEntity.getTagsUpdateTimestamp()) {
            new GetTagsRequest(eventBus, endpointFactory, context, user).call();
        }
        new SendTagsRequest(eventBus, endpointFactory, context, user, dbHelper, device).call();

        // Categories
        if (user.getLastCategoriesUpdateTimestamp() != configEntity.getCategoriesUpdateTimestamp()) {
            new GetCategoriesRequest(eventBus, endpointFactory, context, user).call();
        }
        new SendCategoriesRequest(eventBus, endpointFactory, context, user, dbHelper, device).call();

        // Accounts
        if (user.getLastAccountsUpdateTimestamp() != configEntity.getAccountsUpdateTimestamp()) {
            new GetAccountsRequest(eventBus, endpointFactory, context, user).call();
        }
        new SendAccountsRequest(eventBus, endpointFactory, context, user, dbHelper, device).call();

        // Transactions
        if (user.getLastTransactionsUpdateTimestamp() != configEntity.getTransactionsUpdateTimestamp()) {
            new GetTransactionsRequest(eventBus, endpointFactory, context, user).call();
        }
        new SendTransactionsRequest(eventBus, endpointFactory, context, user, dbHelper, device).call();

        return null;
    }

    private ConfigEntity getConfigEntity() throws Exception {
        //noinspection ConstantConditions
        final GetConfigRequest getConfigRequest = new GetConfigRequest(eventBus, endpointFactory, currenciesManager, generalPrefs, security, currentInterval, activeInterval);
        final Result<ConfigEntity> result = getConfigRequest.call();
        if (!result.isSuccess()) {
            throw result.getError();
        }
        return result.getData();
    }
}
