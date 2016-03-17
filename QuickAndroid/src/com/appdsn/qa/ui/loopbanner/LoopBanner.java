package com.appdsn.qa.ui.loopbanner;

import java.lang.reflect.Field;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.appdsn.qa.ui.viewpagerindicator.PageIndicator;

/**
 * 页面翻转控件，极方便的广告栏
 * 支持无限循环，自动翻页，翻页特效
 * @author Sai 支持自动翻页
 */
public class LoopBanner extends RelativeLayout {
  
    public LoopPageAdapter pageAdapter;
    private LoopViewPager viewPager;
  
    private long autoTurningTime=5000;
    private boolean turning=false;//是否正在翻页
    private boolean canTurning = false;//是否可自动翻页
    public enum PageIndicatorAlign{
        ALIGN_PARENT_LEFT,ALIGN_PARENT_RIGHT,CENTER_HORIZONTAL,ALIGN_PARENT_BOTTOM,ALIGN_PARENT_TOP
    }

    private Runnable adSwitchTask = new Runnable() {
        @Override
        public void run() {
            if (viewPager != null && turning) {
                int page = viewPager.getCurrentItem() + 1;
                boolean canLoop=pageAdapter.isCanLoop();
                if (!canLoop&&page==pageAdapter.getRealCount()) {
					
				}else {
					 viewPager.setCurrentItem(page);
				}
               
                postDelayed(adSwitchTask, autoTurningTime);
            }
        }
    };
	private BannerPageIndicator pageIndicator;
	private ViewPagerScroller scroller;
	private int realCount;

    public LoopBanner(Context context) {
        this(context, null);
    }
    public LoopBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
       
        viewPager= new LoopViewPager(context);
        pageIndicator=new BannerPageIndicator(context);
        this.addView(viewPager);
        this.addView(pageIndicator);
        setPageIndicatorAlign(PageIndicatorAlign.CENTER_HORIZONTAL);
        initViewPagerScroll();
    }

    public LoopBanner setPageAdapter(LoopPageAdapter pageAdapter){
       
        this.pageAdapter = pageAdapter;
        viewPager.setAdapter(pageAdapter);
        pageAdapter.setViewPager(viewPager);
        pageIndicator.setViewPager(viewPager);
        realCount=pageAdapter.getRealCount();
        
        return this;
    }
    public LoopViewPager getViewPager() {
        return viewPager;
    }
    public PageIndicator getIndicator(){
    	
    	return pageIndicator;
    }
    /**
     * 通知数据变化
     * 如果只是增加数据建议使用 notifyDataSetAdd()
     */
    public void notifyDataSetChanged(){
    	
    	realCount=pageAdapter.getRealCount();
    	pageIndicator.notifyDataSetChanged();
    	pageAdapter.notifyDataSetChanged();
    	if (canTurning) {
    		startTurning(autoTurningTime);
		}
    	if (realCount<=1) {
    		viewPager.setCanScroll(false);
		}else {
			viewPager.setCanScroll(true);
		}
    	
    }

    /**
     * 设置底部指示器是否可见
     *
     * @param visible
     */
    public LoopBanner setIndicatorVisible(boolean visible) {
    	pageIndicator.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }


    /**
     * 指示器的方向
     * @param align  三个方向：居左 （RelativeLayout.ALIGN_PARENT_LEFT），居中 （RelativeLayout.CENTER_HORIZONTAL），居右 （RelativeLayout.ALIGN_PARENT_RIGHT）
     * @return
     */
    public LoopBanner setPageIndicatorAlign(PageIndicatorAlign align) {
        LayoutParams layoutParams = (LayoutParams) pageIndicator.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, align == PageIndicatorAlign.ALIGN_PARENT_TOP ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
       
        pageIndicator.setLayoutParams(layoutParams);
        return this;
    }
    
    /**
     * 设置ViewPager的滚动速度
     * @param scrollDuration
     */
    public void setScrollDuration(int scrollDuration){
        scroller.setScrollDuration(scrollDuration);
    }
    /**
     * 设置ViewPager的滑动速度
     * */
    private void initViewPagerScroll() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new ViewPagerScroller(
                    viewPager.getContext());
            mScroller.set(viewPager, scroller);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    /***
     * 是否开启了翻页
     * @return
     */
    public boolean isTurning() {
        return turning;
    }

    /***
     * 开始翻页
     * @param autoTurningTime 自动翻页时间
     * @return
     */
    public void startTurning(long autoTurningTime) {
    	//设置可以翻页
        this.autoTurningTime = autoTurningTime;
    	canTurning = true;
    	//如果是正在翻页的话先停掉
        if(turning){
            stopTurning();
        }
        if (realCount<=1) {
			return;
		}
        //开启翻页
        turning = true;
        postDelayed(adSwitchTask, autoTurningTime);
     
    }

    public void stopTurning() {
        turning = false;
        removeCallbacks(adSwitchTask);
    }

    public boolean isCanTurning() {
        return canTurning;
    }
    public void setCanTurning(boolean canTurning) {
        this.canTurning = canTurning;
    }
   
    /**
     * 自定义翻页动画效果
     *
     * @param transformer
     * @return
     */
    public LoopBanner setPageTransformer(PageTransformer transformer) {
        viewPager.setPageTransformer(true, transformer);
        return this;
    }


    public boolean isCanScroll() {
        return viewPager.isCanScroll();
    }

    public void setCanScroll(boolean canScroll) {
        viewPager.setCanScroll(canScroll);
    }

    //触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP||action == MotionEvent.ACTION_CANCEL||action == MotionEvent.ACTION_OUTSIDE) {
            // 开始翻页
            if (canTurning){
            	startTurning(autoTurningTime);
            }
        } else if (action == MotionEvent.ACTION_DOWN) {
            // 停止翻页
            if (canTurning){
            	stopTurning();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    //获取当前的页面index
    public int getCurrentItem(){
        if (viewPager!=null) {
            return viewPager.getRealItem();
        }
        return -1;
    }
    //设置当前的页面index
    public void setCurrentItem(int index){
        if (viewPager!=null) {
            viewPager.setCurrentItem(index,false);
        }
    }

   
    /**
     * 设置翻页监听器
     * @param onPageChangeListener
     * @return
     */
    public LoopBanner setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
      
    	pageIndicator.setOnPageChangeListener(onPageChangeListener);
        return this;
    }

  

   
}
