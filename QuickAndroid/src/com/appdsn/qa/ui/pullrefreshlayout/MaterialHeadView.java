package com.appdsn.qa.ui.pullrefreshlayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
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
    // Default background for the progress spinner
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    // Default offset in dips from the top of the view to where the progress spinner should stop
    private static final int DEFAULT_CIRCLE_TARGET = 64;
    private static final int CIRCLE_DIAMETER = 45;
    private Paint mTextPaint;
    private boolean mIfDrawText;
    private int progressTextColor;
    private int progressStokeWidth;
    private boolean isShowArrow, isShowProgressBg;
    private int progressValue, progressValueMax;
    private int textType;
    private int progressBg;
    private int progressSize;
    private MaterialDrawable mProgressDrawable;
    private int mTextSize;
    private int mProgress = 0;
    private int[] schemeColors;

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
        mProgressDrawable = new MaterialDrawable(getContext(), this);
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

        return dip2px(DEFAULT_CIRCLE_TARGET);
    }

    public void setProgressSize(int progressSize)
    {
        getLayoutParams().width = dip2px(progressSize);
        getLayoutParams().height = dip2px(progressSize);
    }

    public void setColorSchemeColors(int[] colors)
    {
        mProgressDrawable.setColorSchemeColors(colors);
    }

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
        if (mProgressDrawable != null) {
            mProgressDrawable.setProgressRotation(fraction);
            mProgressDrawable.setStartEndTrim(0, (float) Math.min(0.8, fraction));
            mProgressDrawable.setArrowScale(fraction);
        }
        ViewCompat.setAlpha(this, fraction);
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
