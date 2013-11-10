package com.code44.finance.api;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

public interface CurrencyService
{
    @GET("/currency")
    public Response getExchangeRate(@Query("from") String from, @Query("to") String to);
}