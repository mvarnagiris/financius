package com.code44.finance.backend.endpoint;

import com.code44.finance.backend.endpoint.body.CurrenciesBody;
import com.code44.finance.backend.entity.Currency;
import com.code44.finance.backend.entity.UserAccount;
import com.code44.finance.backend.utils.EndpointUtils;
import com.code44.finance.common.Constants;
import com.google.api.server.spi.Constant;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import java.util.List;

import javax.inject.Named;

import static com.code44.finance.backend.OfyService.ofy;

@Api(
        name = "currencies",
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constant.API_EXPLORER_CLIENT_ID, Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID},
        audiences = {Constants.ANDROID_AUDIENCE},
        namespace = @ApiNamespace(
                ownerDomain = "endpoint.backend.finance.code44.com",
                ownerName = "endpoint.backend.finance.code44.com",
                packagePath = ""
        )
)
public class CurrenciesEndpoint {
    @ApiMethod(name = "list", httpMethod = "GET", path = "")
    public CollectionResponse<Currency> list(@Named("timestamp") long timestamp, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException {
        final UserAccount userAccount = EndpointUtils.getUserAccountAndVerifyPermissions(user);
        final List<Currency> currencies = ofy()
                .load()
                .type(Currency.class)
                .filter("userAccount", Key.create(UserAccount.class, userAccount.getId()))
                .filter("editTimestamp >=", timestamp)
                .list();

        return CollectionResponse.<Currency>builder().setItems(currencies).build();
    }

    @ApiMethod(name = "save", httpMethod = "POST", path = "")
    public void save(CurrenciesBody body, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException {
        final UserAccount userAccount = EndpointUtils.getUserAccountAndVerifyPermissions(user);
        final Key<UserAccount> key = Key.create(UserAccount.class, userAccount.getId());
        final List<Currency> currencies = body.getCurrencies();

        final Objectify ofy = ofy();
        for (Currency currency : currencies) {
            if (ofy.load().type(Currency.class).id(currency.getId()).now() == null) {
                currency.onCreate();
            } else {
                currency.onUpdate();
            }
            currency.setUserAccount(key);
        }
        ofy.save().entities(currencies).now();
    }
}