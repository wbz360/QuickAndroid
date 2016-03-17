package com.appdsn.qa.common.http.callback;

import okhttp3.Call;
import okhttp3.Response;

public abstract class HttpCallback<T>
{
    /**
     * UI Thread
     */
    public void onStart() {}
    public abstract void onFailure(Call call, Exception e);
    public abstract void onSuccess(T response);
    public void onProgress(long bytesWritten, long totalSize) {}
    public void onFinish() {}

    /**
     * Thread Pool Thread
     */
    public abstract T parseNetworkResponse(Response response) throws Exception;

}