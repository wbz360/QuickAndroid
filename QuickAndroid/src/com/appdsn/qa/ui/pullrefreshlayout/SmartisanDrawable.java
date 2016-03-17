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
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;


public class SmartisanDrawable extends Drawable implements Animatable
{

    RectF mBounds;
    float mWidth;
    float mHeight;
    float mCenterX;
    float mCenterY;
    float mPercent;
    float mMaxAngle;
    float mRadius;
    float mLineLength;
    float mLineWidth;
    float mArrowLength;
    float mArrowAngle;
    float mArrowXSpace;
    float mArrowYSpace;
    Paint mPaint = new Paint();
    boolean mRunning;
    float mDegrees;
    Context context;

    public SmartisanDrawable(Context context, int size)
    {
        this.context = context;
        mMaxAngle = (float) (180f * .85);
        mRadius = dp2px(15);
        mLineLength = (float) (Math.PI / 180 * mMaxAngle * mRadius);
        mLineWidth = dp2px(3);
        mArrowLength = (int) (mLineLength * .15);
        mArrowAngle = (float) (Math.PI / 180 * 25);
        mArrowXSpace = (int) (mArrowLength * Math.sin(mArrowAngle));
        mArrowYSpace = (int) (mArrowLength * Math.cos(mArrowAngle));

        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GRAY);
        mHeight = size;
        mWidth = size;

    }

    @Override
    protected void onBoundsChange(Rect bounds)
    {
        super.onBoundsChange(bounds);
        mBounds = new RectF(bounds.width() / 2 - mWidth / 2, bounds.top - mHeight / 2, bounds.width() / 2 + mWidth / 2, bounds.top + mHeight / 2);
        mCenterX = mBounds.centerX();
        mCenterY = mBounds.centerY();
    }

    public void setPercent(float percent)
    {
        mPercent = percent;
        invalidateSelf();
    }

    public void setColor(int color)
    {
        
         mPaint.setColor(color);
        
    }

    @Override
    public void start()
    {
        mRunning = true;
        mDegrees = 0;
        invalidateSelf();
    }

    @Override
    public void stop()
    {
        mRunning = false;
    }

    @Override
    public boolean isRunning()
    {
        return mRunning;
    }

    @Override
    public void draw(Canvas canvas)
    {

        canvas.translate(0, mHeight / 2);
        canvas.clipRect(mBounds);

//        if (mOffset > mHeight && !isRunning()) {
//            canvas.rotate((mOffset - mHeight) / mHeight * 360, mCenterX, mCenterY);
//        }

        if (isRunning()) {
            canvas.rotate(mDegrees, mCenterX, mCenterY);
            mDegrees = mDegrees < 360 ? mDegrees + 10 : 0;
            invalidateSelf();//这里实现动画效果
        }

        if (mPercent <= .5f) {

            float percent = mPercent / .5f;

            // left
            float leftX = mCenterX - mRadius;
            float leftY = mCenterY + mLineLength - mLineLength * percent;

            canvas.drawLine(leftX, leftY, leftX, leftY + mLineLength, mPaint);

            // left arrow
            canvas.drawLine(leftX, leftY, leftX - mArrowXSpace, leftY + mArrowYSpace, mPaint);

            // right
            float rightX = mCenterX + mRadius;
            float rightY = mCenterY - mLineLength + mLineLength * percent;

            canvas.drawLine(rightX, rightY, rightX, rightY - mLineLength, mPaint);

            // right arrow
            canvas.drawLine(rightX, rightY, rightX + mArrowXSpace, rightY - mArrowYSpace, mPaint);

        }
        else {

            float percent = (mPercent - .5f) / .5f;
            // left
            float leftX = mCenterX - mRadius;
            float leftY = mCenterY;

            canvas.drawLine(leftX, leftY, leftX, leftY + mLineLength - mLineLength * percent, mPaint);

            RectF oval = new RectF(mCenterX - mRadius, mCenterY - mRadius, mCenterX + mRadius, mCenterY + mRadius);

            canvas.drawArc(oval, 180, mMaxAngle * percent, false, mPaint);

            // right
            float rightX = mCenterX + mRadius;
            float rightY = mCenterY;

            canvas.drawLine(rightX, rightY, rightX, rightY - mLineLength + mLineLength * percent, mPaint);

            canvas.drawArc(oval, 0, mMaxAngle * percent, false, mPaint);

            // arrow
            canvas.save();

//            canvas.translate(mCenterX, mCenterY);
            canvas.rotate(mMaxAngle * percent, mCenterX, mCenterY);
//            canvas.translate(-mCenterX, -mCenterY);

            // left arrow
            canvas.drawLine(leftX, leftY, leftX - mArrowXSpace, leftY + mArrowYSpace, mPaint);

            // right arrow
            canvas.drawLine(rightX, rightY, rightX + mArrowXSpace, rightY - mArrowYSpace, mPaint);

            canvas.restore();
        }

//        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha)
    {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter)
    {

    }

    @Override
    public int getOpacity()
    {
        return 0;
    }

    public int dp2px(float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
