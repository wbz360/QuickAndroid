package com.appdsn.qa.ui.viewpagerindicator.scrollbar;

import android.graphics.Canvas;

public interface ScrollBar {
	public static enum Gravity {
		/** 顶部占位 */
		TOP,
		
		/** 底部占位 */
		BOTTOM,
		
		/** 中间覆盖在Indicator�? */
		CENTENT,
		
	}

	/**
	 * SlideView显示的高�?
	 * 
	 * @return
	 */
	public int getHeight(int tabHeight);

	/**
	 * SlideView 显示的宽�?
	 * 
	 * @param tabWidth
	 * @return
	 */
	public int getWidth(int tabWidth);


	public void draw(Canvas canvas, float left, float top, float right, float bottom);

	/**
	 * 位置
	 * 
	 * @return
	 */
	public Gravity getGravity();

}
