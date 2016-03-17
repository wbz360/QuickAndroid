package com.appdsn.demo.common;

import java.lang.reflect.Type;

import okhttp3.Call;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.appdsn.demo.activity.LoginActivity;
import com.appdsn.demo.entity.BaseEntity;
import com.appdsn.qa.common.http.OkHttpUtils;
import com.appdsn.qa.common.http.RequestParams;
import com.appdsn.qa.common.http.callback.StringCallback;
import com.appdsn.qa.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AsyncHttpHelper
{

    public final static Gson GSON = new GsonBuilder().setDateFormat("yyyy.MM.dd HH:mm:ss").create();

    protected AsyncHttpHelper()
    {
    }

    private static void handleResult(Context mContext, String response, Type type, AsyncHttpHelper.ResultCallback callback)
    {
        LogUtils.i("123", response.toString());
        BaseEntity baseRespInfo = GSON.fromJson(response, type);
        if (baseRespInfo.code == 0) {
            if (callback != null) {
                callback.onSuccess(baseRespInfo);
            }
        }
        else if (baseRespInfo.code == 23) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            if (callback != null) {
                callback.onFailure(baseRespInfo.code + "", baseRespInfo.message);
            }
        }
        else {
            Toast.makeText(mContext, baseRespInfo.message, Toast.LENGTH_SHORT).show();
            if (callback != null) {
                callback.onFailure(baseRespInfo.code + "", baseRespInfo.message);
            }

        }
    }

    public static void getRequest(final Context mContext, String url, final Type type, final AsyncHttpHelper.ResultCallback callback)
    {

      
        OkHttpUtils.getInstance().get(mContext, url, new StringCallback()
        {
            @Override
            public void onFailure(Call call, Exception e)
            {
                Toast.makeText(mContext, "请检查网络", Toast.LENGTH_SHORT).show();
                callback.onFailure("-1", "请检查网络");
            }
            @Override
            public void onSuccess(String response)
            {
                handleResult(mContext, response, type, callback);
            }
        });
    }

    public static void postRequest(final Context mContext, String url, JSONObject jObject, final Type type, final AsyncHttpHelper.ResultCallback callback)
    {
      
        OkHttpUtils.getInstance().post(mContext, url, RequestParams.createStringBody(jObject.toString(),null), new StringCallback()
        {
            @Override
            public void onFailure(Call call, Exception e)
            {
                Toast.makeText(mContext, "请检查网络", Toast.LENGTH_SHORT).show();
                callback.onFailure("-1", "请检查网络");
            }

            @Override
            public void onSuccess(String response)
            {
                handleResult(mContext, response, type, callback);
            }
        });

    }
    public interface ResultCallback<T>
    {

        /**
         * 成功时调用
         *
         * @param data 返回的数据
         */
        public void onSuccess(T data);

        /**
         * 失败时调用
         *
         * @param errorEvent 错误码
         * @param message    错误信息
         */
        public void onFailure(String errorCode, String message);
    }


}
