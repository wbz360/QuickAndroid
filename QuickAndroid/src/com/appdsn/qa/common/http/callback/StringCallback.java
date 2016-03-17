package com.appdsn.qa.common.http.callback;

import java.io.IOException;

import okhttp3.Response;


public abstract class StringCallback extends HttpCallback<String>
{
    @Override
    public String parseNetworkResponse(Response response) throws IOException
    {
        return response.body().string();
    }

}
