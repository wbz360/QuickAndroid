package com.appdsn.qa.ui.pullrefreshlayout;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ArrowHeadView extends ImageView implements BaseHeadView
{
  
    private ArrowDrawable mProgressDrawable;
    private static final int DEFAULT_REFRESH_DISTANCE = 64;
    private static final int CIRCLE_DIAMETER = 40;
 
    public ArrowHeadView(Context context)
    {
    	 super(context, null);
         if (isInEditMode()) {
             return;
         }
         setWillNotDraw(false);
         mProgressDrawable = new ArrowDrawable(getContext());
         super.setImageDrawable(mProgressDrawable);
    }

    public void setArrowColor(int color) {
    	mProgressDrawable.setArrowColor(color);
	}

	public void setCircleColor(int color) {
		mProgressDrawable.setCircleColor(color);
	}
	public void setColor(int color) {
		mProgressDrawable.setArrowColor(color);
		mProgressDrawable.setCircleColor(color);
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
