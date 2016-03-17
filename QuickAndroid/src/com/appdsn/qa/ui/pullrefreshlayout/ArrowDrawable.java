/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 baoyongzhang <baoyz94@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.appdsn.qa.ui.pullrefreshlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

/**
 * android-PullRefreshLayout Created by baoyz on 15/9/20.
 */
public class ArrowDrawable extends Drawable implements Animatable {

	private Paint mPaint;
	private int arrowHeadHeight;
	private int arrowAllHeight;
	private final Paint painDegree;
	Context context;
	private Rect mBounds;
	private float mCenterX;
	private float mCenterY;
	private int mArrowLength;
	private int mArrowDegrees;
	private boolean isArrowTop = false;// 是否箭头向上
	private boolean isRotateArrow = false;// 是否旋转箭头
	private int mCircleDegrees;
	private int radius;

	private int drawType = 0;// 0，箭头，1圆圈，2是成功

	public ArrowDrawable(Context context) {
		this.context = context;
		mPaint = new Paint();
		CornerPathEffect cornerPathEffect = new CornerPathEffect(dp2px(3));
		mPaint.setPathEffect(cornerPathEffect);
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.DKGRAY);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setStrokeWidth(dp2px(2));

		painDegree = new Paint();
		painDegree.setAntiAlias(true);
		painDegree.setColor(Color.DKGRAY);
		painDegree.setStyle(Paint.Style.STROKE);
	}

	public void setArrowColor(int color) {
		mPaint.setColor(color);
	}

	public void setCircleColor(int color) {
		painDegree.setColor(color);
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		mBounds = getBounds();// 实际绘制区域大小，和一般和view高宽一致，比如设置为背景
		mCenterX = bounds.exactCenterX();
		mCenterY = bounds.exactCenterY();

		// 获取宽高参数
		int mWidth = mBounds.width();
		int mHeight = mBounds.height();
		radius = Math.min(mWidth, mHeight) / 4;
		float strokeWidth = (float) (2 * Math.PI * radius / 48);
		painDegree.setStrokeWidth(strokeWidth);

		mArrowLength = (int) (radius * 1.8);// 箭头边长,高度的一半
		arrowHeadHeight = (int) (mArrowLength * Math.sin(Math.PI * 60 / 180));
		arrowAllHeight = mArrowLength * 2;
	}

	@Override
	public void draw(Canvas canvas) {

		if (drawType == 0) {
			drawArrow(canvas);
		} else if (drawType == 1) {
			drawCircle(canvas);
		} else {
			drawSucess(canvas);
		}

	}

	private void drawArrow(Canvas canvas) {
		canvas.rotate(mArrowDegrees, mCenterX, mCenterY);
		if (isRotateArrow) {
			if (isArrowTop) {
				mArrowDegrees -= 15;
				if (mArrowDegrees <= -180) {
					mArrowDegrees = -180;
					isRotateArrow = false;
				}
				invalidateSelf();// 这里实现动画效果
			} else {
				mArrowDegrees += 15;
				if (mArrowDegrees >= 0) {
					mArrowDegrees = 0;
					isRotateArrow = false;
				}
				invalidateSelf();// 这里实现动画效果
			}
		}
		Path path = new Path();
		path.moveTo(mCenterX, mCenterY + arrowAllHeight / 2);
		path.lineTo(mCenterX - mArrowLength * 2 / 3, mCenterY + arrowAllHeight
				/ 2 - arrowHeadHeight);
		path.lineTo(mCenterX + mArrowLength * 2 / 3, mCenterY + arrowAllHeight
				/ 2 - arrowHeadHeight);
		path.close();
		canvas.drawPath(path, mPaint);
		path.reset();

		path.moveTo(mCenterX - 2, mCenterY - arrowAllHeight / 2);
		path.lineTo(mCenterX - mArrowLength / 4, mCenterY + arrowAllHeight / 2
				- arrowHeadHeight + dp2px(3));
		path.lineTo(mCenterX + mArrowLength / 4, mCenterY + arrowAllHeight / 2
				- arrowHeadHeight + dp2px(3));
		path.lineTo(mCenterX + 2, mCenterY - arrowAllHeight / 2);
		path.close();
		canvas.drawPath(path, mPaint);

	}

	private void drawSucess(Canvas canvas) {
		// 画外圆
		painDegree.setAlpha(255);
		canvas.drawCircle(mCenterX, mCenterY, radius, painDegree);
		// 画对号
		Path path = new Path();
		path.moveTo(mCenterX - radius + dp2px(4), mCenterY);
		path.lineTo(mCenterX - dp2px(2), mCenterY + radius - dp2px(4)
				- dp2px(2));
		path.lineTo(mCenterX + radius - dp2px(4), mCenterY - dp2px(4));
		canvas.drawPath(path, painDegree);
	}

	private void drawCircle(Canvas canvas) {
		canvas.rotate(mCircleDegrees, mCenterX, mCenterY);
		mCircleDegrees = mCircleDegrees < 360 ? mCircleDegrees + 5 : 0;
		invalidateSelf();// 这里实现动画效果

		// 画刻度
		for (int i = 0; i < 24; i++) {
			int alpha = (i + 1) * 255 / 23;
			painDegree.setAlpha(alpha);
			canvas.drawLine(mCenterX, mCenterY - radius, mCenterX, mCenterY
					- radius / 3, painDegree);
			// 通过旋转画布简化坐标运算
			canvas.rotate(15, mCenterX, mCenterY);
		}

	}

	@Override
	public int getIntrinsicWidth() {
		return -1;// 这里当view设置为wrap_content时才会使用到该属性
	}

	@Override
	public int getIntrinsicHeight() {
		return -1;// 这里当view设置为wrap_content时才会使用到该属性
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter colorFilter) {
		mPaint.setColorFilter(colorFilter);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	public void resetArrow() {
		mArrowDegrees = 0;
		isRotateArrow = false;
		this.isArrowTop = false;
		drawType = 0;
		invalidateSelf();
	}

	public void rotateArrow(boolean isArrowTop) {
		if (this.isArrowTop != isArrowTop) {
			this.isArrowTop = isArrowTop;
			isRotateArrow = true;
			drawType = 0;
			invalidateSelf();
		}

	}

	@Override
	public void start() {
		drawType = 1;
		invalidateSelf();
	}

	@Override
	public void stop() {
		drawType = 2;
		invalidateSelf();
	}

	@Override
	public boolean isRunning() {
		return false;
	}

	public int dp2px(float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
