package com.appdsn.qa.common.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.appdsn.qa.common.http.callback.HttpCallback;
import com.appdsn.qa.common.http.cookie.SimpleCookieJar;

/**
 * Created by baozhong 2016/02/01
 */
public class OkHttpUtils
{
    public static final String TAG = "OkHttpUtils";
    public static final long DEFAULT_MILLISECONDS = 5000;
    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;
    private final Map<String, String> clientHeaderMap;
    private OkHttpUtils()
    {
        mHandler = new Handler(Looper.getMainLooper());
        clientHeaderMap = new HashMap<String, String>();
        /*下面是全局的默认配置*/
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.cookieJar(new SimpleCookieJar()); //cookie enabled
        okHttpClientBuilder.connectTimeout(DEFAULT_MILLISECONDS,TimeUnit.MILLISECONDS);
        okHttpClientBuilder.readTimeout(DEFAULT_MILLISECONDS,TimeUnit.MILLISECONDS);
        okHttpClientBuilder.writeTimeout(DEFAULT_MILLISECONDS,TimeUnit.MILLISECONDS);

        if (true)
        {
            okHttpClientBuilder.hostnameVerifier(new HostnameVerifier()
            {
                @Override
                public boolean verify(String hostname, SSLSession session)
                {
                    return true;
                }
            });
        }
        mOkHttpClient = okHttpClientBuilder.build();
    }

    private boolean debug;
    private String tag;

    public OkHttpUtils debug(String tag)
    {
        debug = true;
        this.tag = tag;
        return this;
    }

    public static OkHttpUtils getInstance()
    {
        if (mInstance == null)
        {
            synchronized (OkHttpUtils.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }
    public void removeAllHeaders() {
        clientHeaderMap.clear();
    }
    /**
     * Sets headers that will be added to all requests this client makes (before sending).
     */
    public void addHeader(String name, String value) {
        clientHeaderMap.put(name, value);
    }
    /**
     * Remove header from all requests this client makes (before sending).
     */
    public void removeHeader(String name) {
        clientHeaderMap.remove(name);
    }

    protected Headers appendHeaders()
    {
        Headers.Builder headerBuilder = new Headers.Builder();
        for (String key : clientHeaderMap.keySet())
        {
            headerBuilder.add(key, clientHeaderMap.get(key));
        }
        return  headerBuilder.build();
    }
    public Handler getHandler()
    {
        return mHandler;
    }

    public OkHttpClient getOkHttpClient()
    {
        return mOkHttpClient;
    }


    public Call get(Context context, String url,HttpCallback callback) {
        return get(context, url, null, callback);
    }

    public Call get(Context context, String url, RequestParams params, HttpCallback callback) {
        if (params!=null){
            url= params.getUrlWithParams(url);
        }
        Request request = new Request.Builder()
                .url(url)
                .tag(context)
                .headers(appendHeaders())//会覆盖之前添加的header
                .build();

        return sendRequest(request,callback);
    }

    public Call post(Context context, String url, RequestParams params, HttpCallback callback) {
        RequestBody requestBody=null;
        if (params!=null){
            try {
                requestBody=params.getRequestBody();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
      return  post(context,url,requestBody,callback);

    }
    public Call post(Context context, String url,  RequestBody requestBody ,HttpCallback callback) {
        requestBody= wrapRequestBody(requestBody,callback);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .tag(context)
                .headers(appendHeaders())
                .build();

        return sendRequest(request,callback);
    }

    private Call sendRequest(final Request request, final HttpCallback callback)
    {
        if (debug)
        {
            if(TextUtils.isEmpty(tag))
            {
                tag = TAG;
            }
            Log.d(tag, "{method:" +request.method() + ", detail:" + request.toString() + "}");
        }

        Call  call = mOkHttpClient.newCall(request);
        callback.onStart();
        call.enqueue(new okhttp3.Callback()
        {
            @Override
            public void onFailure(Call call, final IOException e)
            {
                sendFailResultCallback(call, e, callback);
            }

            @Override
            public void onResponse(final Call call, final Response response)
            {
                if (response.code() >= 400 && response.code() <= 599)
                {
                    try
                    {
                        sendFailResultCallback(call, new RuntimeException(response.body().string()), callback);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    return;
                }

                try
                {
                    Object o = callback.parseNetworkResponse(response);
                    sendSuccessResultCallback(o, callback);
                } catch (Exception e)
                {
                    sendFailResultCallback(call, e, callback);
                }

            }
        });

        return call;
    }


    public void sendFailResultCallback(final Call call, final Exception e, final HttpCallback callback)
    {
        if (callback == null) return;

        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                callback.onFailure(call, e);
                callback.onFinish();
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final HttpCallback callback)
    {
        if (callback == null) return;
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                callback.onSuccess(object);
                callback.onFinish();
            }
        });
    }

    /*文件上传进度监测*/
    protected RequestBody wrapRequestBody(RequestBody requestBody, final HttpCallback callback)
    {
        if (callback == null) return requestBody;
        RequestBodyWrapper requestBodyWrapper = new RequestBodyWrapper(requestBody, new RequestBodyWrapper.Listener()
        {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength)
            {

                OkHttpUtils.getInstance().getHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        callback.onProgress(bytesWritten, contentLength);
                    }
                });

            }
        });
        return requestBodyWrapper;
    }
    public void cancelTag(Object tag)
    {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls())
        {
            if (tag.equals(call.request().tag()))
            {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls())
        {
            if (tag.equals(call.request().tag()))
            {
                call.cancel();
            }
        }
    }


    public void setCertificates(InputStream... certificates)
    {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .sslSocketFactory(MySSLSocketFactory.getSslSocketFactory(certificates, null, null))
                .build();
    }

    public void setConnectTimeout(int timeout, TimeUnit units)
    {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .connectTimeout(timeout, units)
                .build();
    }

}

