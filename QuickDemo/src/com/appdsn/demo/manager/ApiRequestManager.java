package com.appdsn.demo.manager;

import java.lang.reflect.Type;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.appdsn.demo.application.AppConfig;
import com.appdsn.demo.application.AppUser;
import com.appdsn.demo.common.AsyncHttpHelper;
import com.appdsn.demo.entity.BaseEntity;
import com.appdsn.demo.entity.UserEntity;
import com.google.gson.reflect.TypeToken;


public class ApiRequestManager
{

	private Context mContext;
	private static ApiRequestManager mInstance = null;

	public static ApiRequestManager getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ApiRequestManager(context);
		}
		return mInstance;
	}
	private ApiRequestManager(Context context) {
		this.mContext = context;
	}

	/*原生用户登录*/
	public void startLogin(String mobile, String pwd,
			AsyncHttpHelper.ResultCallback<UserEntity> callback) {
		String url = AppConfig.BASE_URL+"/native/password/vcode";
		JSONObject jObject = new JSONObject();
		try {
			
			jObject.put("uname", mobile);
			jObject.put("password", pwd);
			
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		Type type = new TypeToken<BaseEntity>() {}.getType();
		AsyncHttpHelper.postRequest(mContext,url,jObject,type,callback);
	}

	/*获取我的基本信息*/
	public void getUserInfo(AsyncHttpHelper.ResultCallback<UserEntity> callback)
	{
		String url = AppConfig.BASE_URL+"/user/info?user_id="+ AppUser.getInstance(mContext).userID+"&user_token="+ AppUser.getInstance(mContext).token;
		Type type = new TypeToken<UserEntity>() {}.getType();
		AsyncHttpHelper.getRequest(mContext,url,type,callback);
	}

}
