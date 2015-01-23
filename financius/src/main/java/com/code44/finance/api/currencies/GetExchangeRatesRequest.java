package com.code44.finance.api.currencies;

import com.code44.finance.api.Request;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.utils.EventBus;

import java.util.Arrays;

public class GetExchangeRatesRequest extends Request<ExchangeRatesResponse> {
    private final CurrenciesRequestService requestService;
    private final String[] codes;

    public GetExchangeRatesRequest(EventBus eventBus, CurrenciesRequestService requestService, String... codes) {
        super(eventBus);
        Preconditions.notNull(eventBus, "EventBus cannot be empty.");
        this.requestService = Preconditions.notNull(requestService, "Request service cannot be null.");
        this.codes = Preconditions.notNull(codes, "Codes cannot be null.");
    }

    @Override protected ExchangeRatesResponse performRequest() throws Exception {
        final StringBuilder sb = new StringBuilder();
        sb.append("select * from yahoo.finance.xchange where pair in (");
        for (String code : codes) {
            sb.append("\"").append(code).append("\",");
        }
        sb.append(")");

        return requestService.getExchangeRates(sb.toString());
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GetExchangeRatesRequest)) return false;

        final GetExchangeRatesRequest that = (GetExchangeRatesRequest) o;

        //noinspection RedundantIfStatement
        if (!Arrays.equals(codes, that.codes)) return false;

        return true;
    }

    @Override public int hashCode() {
        return Arrays.hashCode(codes);
    }
}
