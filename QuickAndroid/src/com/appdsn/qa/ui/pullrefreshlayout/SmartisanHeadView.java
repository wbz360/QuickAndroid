package com.appdsn.qa.ui.pullrefreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class SmartisanHeadView extends ImageView implements BaseHeadView
{
   
    private SmartisanDrawable mProgressDrawable;
    private static final int DEFAULT_REFRESH_DISTANCE = 64;
    private static final int CIRCLE_DIAMETER = 40;
    public SmartisanHeadView(Context context)
    {
        this(context, null);
    }

    public SmartisanHeadView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        setWillNotDraw(false);
        mProgressDrawable = new SmartisanDrawable(getContext(), dip2px(CIRCLE_DIAMETER));
        super.setImageDrawable(mProgressDrawable);

    }

    public void setColor(int color)
    {
        
    	mProgressDrawable.setColor(color);
    }

    @Override
    public  FrameLayout.LayoutParams getFrameLayoutParams(){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dip2px(CIRCLE_DIAMETER), dip2px( CIRCLE_DIAMETER));
        layoutParams.gravity = Gravity.CENTER ;
        return  layoutParams;
    }
    public int getStartRefreshDistance(){

        return  dip2px(DEFAULT_REFRESH_DISTANCE);
    }


    public void onStart(PullRefreshLayout pullRefreshLayout)
    {
        if (mProgressDrawable != null) {

        }

    }

    @Override
    public void onPull(PullRefreshLayout pullRefreshLayout, float fraction)
    {
        if (mProgressDrawable != null) {
            mProgressDrawable.setPercent(fraction);
        }

    }


    @Override
    public void onRefresh(PullRefreshLayout pullRefreshLayout)
    {
        if (mProgressDrawable != null) {
            mProgressDrawable.setPercent(1);
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
