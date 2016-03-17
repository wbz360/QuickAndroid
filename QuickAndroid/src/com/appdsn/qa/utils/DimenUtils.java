package com.appdsn.qa.utils;

import android.content.Context;

/**
 * 系统屏幕的一些操作
 * 
 * Created by baozhong 2016/02/01
 */
public final class DimenUtils {

	
	/**
    * 获取屏幕高度
    */
    public static final int getScreenHeight(Context context) {
        final int height = context.getResources().getDisplayMetrics().heightPixels;
        return height;
    }

    /**
     * 获取屏幕宽度
     */
    public static final int getScreenWidth(Context context) {
        final int width = context.getResources().getDisplayMetrics().widthPixels;
        return width;
    }

   
	 /**
     * 获取屏幕物理尺寸,英寸
     */
    public static int px2pt(Context context, float pxValue) {
    	final float scale = context.getResources().getDisplayMetrics().density;
    	float ptValue=pxValue/(160*scale);
    	return (int) ptValue;
     
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     */
    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px
     */
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}