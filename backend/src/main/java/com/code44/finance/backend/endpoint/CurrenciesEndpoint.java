package com.code44.finance.backend.endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

@Api(
        name = "currenciesEndpoint",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "endpoint.backend.finance.code44.com",
                ownerName = "endpoint.backend.finance.code44.com",
                packagePath = ""
        )
)
public class CurrenciesEndpoint {
//    private static final Logger LOG = Logger.getLogger(CurrenciesEndpoint.class.getName());

//    /**
//     * This method gets the <code>CurrencyEntity</code> object associated with the specified <code>id</code>.
//     * @param id The id of the object to be returned.
//     * @return The <code>CurrencyEntity</code> associated with <code>id</code>.
//     */
//    @ApiMethod(name = "getCurrencyEntity")
//    public CurrencyEntity getCurrencyEntity(@Named("id") Long id) {
//        // Implement this function
//
//        LOG.info("Calling getCurrencyEntity method");
//        return null;
//    }
//
//    /**
//     * This inserts a new <code>CurrencyEntity</code> object.
//     * @param currencyEntity The object to be added.
//     * @return The object to be added.
//     */
//    @ApiMethod(name = "insertCurrencyEntity")
//    public CurrencyEntity insertCurrencyEntity(CurrencyEntity currencyEntity) {
//        // Implement this function
//
//        LOG.info("Calling insertCurrencyEntity method");
//        return currencyEntity;
//    }
}