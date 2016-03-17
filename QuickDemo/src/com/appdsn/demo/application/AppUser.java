package com.appdsn.demo.application;

import android.content.Context;
import android.content.SharedPreferences;

public class AppUser {

	public int userID = 0;
	public String mobile = "";
	public String token = "";
	public String name = "";
	public String avatar = "";// 头像url
	public String jid = "";
	public int deviceType = 2;
	public String createTime = "";
	public boolean isLogin = false;

	private static AppUser user;
	private Context context;

	private AppUser(Context context) {
		this.context = context;
		getUserInfo();
	}

	public static AppUser getInstance(Context context){
		if (user == null) {
			user = new AppUser(context);
		}

		return user;
	}

	public void saveUserInfo() {
		SharedPreferences preference = context.getSharedPreferences(
				AppConfig.PREFERENCE_NAME_USER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt("userID", userID);
		editor.putString("mobile", mobile);
		editor.putString("token", token);
		editor.putString("name", name);
		editor.putString("avatar", avatar);
		editor.putString("createTime", createTime);
		editor.putBoolean("isLogin", isLogin);
		editor.commit();
	}

	public void getUserInfo() {
		SharedPreferences preference = context.getSharedPreferences(
				AppConfig.PREFERENCE_NAME_USER, Context.MODE_PRIVATE);
		userID = preference.getInt("userID", 0);
		mobile = preference.getString("mobile", "");
		token = preference.getString("token", "");
		name = preference.getString("name", "");
		avatar = preference.getString("avatar", "");
		createTime = preference.getString("createTime", "");
		isLogin = preference.getBoolean("isLogin", false);

	}

	public void clearUserInfo() {
		SharedPreferences preference = context.getSharedPreferences(
				AppConfig.PREFERENCE_NAME_USER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt("userID", 0);
		editor.putString("mobile", "");
		editor.putString("token", "");
		editor.putString("name", "");
		editor.putString("avatar", "");
		editor.putString("createTime", "");
		editor.putBoolean("isLogin", false);
		editor.commit();
		userID = 0;
		mobile = "";
		token = "";
		name = "";
		avatar = "";
		createTime = "";
		isLogin = false;
	}

}
