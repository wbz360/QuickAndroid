package com.appdsn.qa.ui.pullrefreshlayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MaterialHeadView extends ImageView implements BaseHeadView
{
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    private static final int DEFAULT_REFRESH_DISTANCE = 64;
    private static final int CIRCLE_DIAMETER = 40;
    private MaterialDrawable mProgressDrawable;
    public MaterialHeadView(Context context)
    {
        this(context, null);
    }

    public MaterialHeadView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        setWillNotDraw(false);
        mProgressDrawable = new MaterialDrawable(context, this);
        mProgressDrawable.setBackgroundColor(CIRCLE_BG_LIGHT);
        mProgressDrawable.showArrow(true);
        mProgressDrawable.setAlpha(255);
        super.setImageDrawable(mProgressDrawable);

        ShapeDrawable mBgCircle = new ShapeDrawable(new OvalShape());
        mBgCircle.getPaint().setColor(CIRCLE_BG_LIGHT);
        setBackgroundDrawable(mBgCircle);
    }

    @Override
    public FrameLayout.LayoutParams getFrameLayoutParams()
    {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dip2px(CIRCLE_DIAMETER), dip2px(CIRCLE_DIAMETER));
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        return layoutParams;
    }

    public int getStartRefreshDistance()
    {

        return dip2px(DEFAULT_REFRESH_DISTANCE);
    }

    public void setCircleSize(int circleSize)
    {
        getLayoutParams().width = dip2px(circleSize);
        getLayoutParams().height = dip2px(circleSize);
    }

    public void setColorSchemeColors(int[] colors)
    {
        mProgressDrawable.setColorSchemeColors(colors);
    }

    /*drawable和view背景一致*/
    @Override
    public void setBackgroundColor(int color)
    {
        mProgressDrawable.setBackgroundColor(color);
        if (getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable) getBackground()).getPaint().setColor(color);
        }
    }

    public void onStart(PullRefreshLayout pullRefreshLayout)
    {
        if (mProgressDrawable != null) {
            mProgressDrawable.showArrow(true);
            mProgressDrawable.setStartEndTrim(0, (float) 0.1);
        }
        ViewCompat.setScaleX(this, 1);
        ViewCompat.setScaleY(this, 1);
    }

    @Override
    public void onPull(PullRefreshLayout pullRefreshLayout, float fraction)
    {
    	float alpha=fraction*255;
        if (mProgressDrawable != null) {
            mProgressDrawable.setProgressRotation(fraction);
            if (fraction<0.7) {
            	 mProgressDrawable.setAlpha((int) alpha);
			}else if(fraction==1){
				 mProgressDrawable.setAlpha((int) alpha);
			}
//            mProgressDrawable.setStartEndTrim(0, (float) Math.min(0.8, fraction));
            mProgressDrawable.setStartEndTrim((float)0.8, (float) Math.min(1.6, fraction+0.8));
            mProgressDrawable.setArrowScale(fraction);
        }
//        ViewCompat.setAlpha(this, fraction);
       
    }

    @Override
    public void onRefresh(PullRefreshLayout pullRefreshLayout)
    {
        if (mProgressDrawable != null) {
            mProgressDrawable.start();
        }
    }


    @Override
    public void onComplete(PullRefreshLayout pullRefreshLayout)
    {
        if (mProgressDrawable != null) {
            mProgressDrawable.stop();
        }
        scaleView(this, 1, 0);
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        Log.i("123","onDetachedFromWindow-material");
        if (mProgressDrawable != null) {
            mProgressDrawable.stop();
            mProgressDrawable.setVisible(false, false);
        }
    }

    public void scaleView(View v, float a, float b)
    {
        ObjectAnimator ax = ObjectAnimator.ofFloat(v, "scaleX", a, b);
        ObjectAnimator ay = ObjectAnimator.ofFloat(v, "scaleY", a, b);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(ax).with(ay);
        animSet.setDuration(200);
        animSet.start();
    }

    public int dip2px(float dpValue)
    {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
