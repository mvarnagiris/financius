package com.code44.finance.api;

import android.content.Context;
import com.code44.finance.utils.StringUtils;
import retrofit.RestAdapter;
import retrofit.client.Response;

import java.io.IOException;

public class Financius
{
    private static Financius instance;
    private Context context;
    private FService service;

    public Financius(Context context)
    {
        this.context = context.getApplicationContext();

        final RestAdapter restAdapter = new RestAdapter.Builder().setServer(getBaseURL()).build();
        service = restAdapter.create(FService.class);
    }

    public static Financius getDefault(Context context)
    {
        if (instance == null)
            instance = new Financius(context);
        return instance;
    }

    public static String parseResponseBody(Response response) throws IOException
    {
        String body = null;
        try
        {
            body = StringUtils.readInputStream(response.getBody().in());
        }
        finally
        {
            try
            {
                response.getBody().in().close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return body;
    }

    public FService getService()
    {
        return service;
    }

    protected String getBaseURL()
    {
        return "";
    }
}