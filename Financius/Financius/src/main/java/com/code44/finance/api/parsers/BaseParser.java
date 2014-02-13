package com.code44.finance.api.parsers;

import retrofit.client.Response;

public interface BaseParser<T>
{
    public void parse(T response, Response rawResponse) throws Exception;
}