package com.appdsn.qa.ui.pullrefreshlayout;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ArrowHeadView extends ImageView implements BaseHeadView
{
    private  Paint mTextPaint;
    private  boolean mIfDrawText;
    private int progressTextColor;

    private int progressStokeWidth;
    private boolean isShowArrow, isShowProgressBg;
    private int progressValue, progressValueMax;
    private int textType;
    private int progressBg;
    private int progressSize;
    private ArrowDrawable mProgressDrawable;
    private int mTextSize;
    private int mProgress=0;

    // Default background for the progress spinner
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    // Default offset in dips from the top of the view to where the progress spinner should stop
    private static final int DEFAULT_CIRCLE_TARGET = 64;
    private static final int CIRCLE_DIAMETER = 40;
    private int[] schemeColors;
    public ArrowHeadView(Context context)
    {
        this(context, null);
    }

    public ArrowHeadView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        setWillNotDraw(false);
        mProgressDrawable = new ArrowDrawable(getContext());
        super.setImageDrawable(mProgressDrawable);

    }


    @Override
    public  FrameLayout.LayoutParams getFrameLayoutParams(){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dip2px(CIRCLE_DIAMETER), dip2px( CIRCLE_DIAMETER));
        layoutParams.gravity = Gravity.CENTER ;
        return  layoutParams;
    }
    public int getStartRefreshDistance(){

        return  dip2px(DEFAULT_CIRCLE_TARGET);
    }


    public void onStart(PullRefreshLayout pullRefreshLayout)
    {
        if (mProgressDrawable != null) {
            mProgressDrawable.resetArrow();
        }

    }

    @Override
    public void onPull(PullRefreshLayout pullRefreshLayout, float fraction)
    {
        if (mProgressDrawable != null) {
            if (fraction>=1){
                mProgressDrawable.rotateArrow(true);
            }else{
                mProgressDrawable.rotateArrow(false);
            }
        }

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

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i("123","onDetachedFromWindow-arrow");
        if (mProgressDrawable != null) {
            mProgressDrawable.stop();
            mProgressDrawable.setVisible(false, false);
        }
    }

    public  int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
