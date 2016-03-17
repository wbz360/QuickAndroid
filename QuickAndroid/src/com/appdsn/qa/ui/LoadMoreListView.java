package com.appdsn.qa.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appdsn.qa.R;


/**
 * 对于ListView，ScrollView，WebView这三种情况，他们是否滑动到最顶部或是最底部的实现是不一样
 * 这个类是对基类接口中特定的方法的实现，实现了ListView下拉刷新，上拉加载更多或者滑到底部自动加载。
 * 主要是针对ListView这个控件的一些独特配置，条件判定等，也是生产控件的类
 *
 * @author Li Hong
 * @since 2013-8-15
 */
public class LoadMoreListView extends ListView implements OnScrollListener
{

    /**
     * 下拉刷新和加载更多的监听器
     */
    private OnLoadListener mLoadListener;
    /**
     * 用于滑到底部自动加载的Footer
     */
    private View mAutoLoadingLayout;
    /**
     * 滚动的监听器
     */
    private OnScrollListener mScrollListener;
    private boolean hasMoreData = true;
    private ProgressBar mProgressBar;
    private TextView mHintView;
    /**
     * 当前的状态
     */
    private State mCurState = State.NORMAL;
    /**
     * 前一个状态
     */
    private State mPreState = State.NORMAL;

    /**
     * 构造方法
     *
     * @param context context
     */
    public LoadMoreListView(Context context)
    {
        this(context, null);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public LoadMoreListView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    /**
     * 构造方法
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public LoadMoreListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);// 调用基类构造方法，初始化一些配置方法
        setOnScrollListener(this);
        // 设置Footer
        mAutoLoadingLayout = LayoutInflater.from(context).inflate(R.layout.layout_load_more_footer, null);
        mProgressBar = (ProgressBar) mAutoLoadingLayout.findViewById(R.id.loadProgress);
        mHintView = (TextView) mAutoLoadingLayout.findViewById(R.id.loadHint);
        this.addFooterView(mAutoLoadingLayout, null, false);
        onStateChanged(State.UNAVAILABLE);// 首次进入一个界面是刷新，加载更多设置为不可用，需要隐藏footer
        
    }

    protected void startLoading()
    {

        if (null != mAutoLoadingLayout && mLoadListener != null) {
            onStateChanged(State.LOADING);// 设置底部状态，正在加载
            mLoadListener.onLoad(this);
        }
    }


    // 加载成功后，调用改变footer状态
    public void onLoadSucess(boolean hasMoreData)
    {
       
        mAutoLoadingLayout.setVisibility(View.VISIBLE);
        this.hasMoreData = hasMoreData;
        if (hasMoreData) {
            onStateChanged(State.NORMAL);// 设置底部状态，完成后隐藏
        }
        else {
            onStateChanged(State.NO_MORE_DATA);// 设置底部状态，完成后隐藏
        }
    }

    // 加载失败后，调用改变footer状态
    public void onLoadFailed()
    {

        this.hasMoreData = true;
        onStateChanged(State.FAILED);// 设置底部状态，完成后隐藏
    }

    //没有数据时，隐藏footer，并设置为不可用
    public void onLoadUnavailable()
    {

        this.hasMoreData = true;
        onStateChanged(State.UNAVAILABLE);// 设置底部状态，完成后隐藏
    }
    // 设置刷新的监听器
    public void setOnLoadListener(OnLoadListener loadListener)
    {
        mLoadListener = loadListener;
    }

    // 下面是监听滚动事件
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        //Logd("123", "onScrollStateChanged");
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        //Logd("123", "-onScroll");
        // setScrollLoadEnabled是在onScroll方法自动执行一次后再调用的（因为先创建的视图，设置事件监听器就会主动触发一次，只是数据为0）
        // 而且可见视图也不是一下就进来，也是先加载一些，再加载一些逐步的。。。
        if (isLastItemVisible()) {
            //Logd("123", hasMoreData + "-hasMoreData2");
            //Logd("123", mCurState + "-mCurState");
            if (hasMoreData && mCurState != State.LOADING&&mCurState != State.FAILED&&mCurState != State.UNAVAILABLE) {
                startLoading();// 开始加载
            }
        }

    }


    /**
     * 判断最后一个child是否完全显示出来 判断listview是否滑动到底部
     *
     * @return true完全显示出来，否则false
     */
    private boolean isLastItemVisible()
    {

        int lastItemPosition = this.getCount() - 1;
        int lastVisiblePosition = this.getLastVisiblePosition();
        //Logd("123", lastVisiblePosition + "lastVisiblePosition2");
        if (this.getCount() == 0) {// 這裡主要判断数据还没有加载进来时，不可以上拉，只能下拉刷新
            return false;
        }
        if (lastVisiblePosition == lastItemPosition) {// 当最后一项可见时
            int index = this.getChildCount() - 1;
            View lastVisibleChild = this.getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= this.getBottom();// 仅试验有可能小于一个像素值，就是下线条所占据的。每个item都有一个下线条，没有上线条
            }
        }

        return false;
    }

    /**
     * 当状态改变时调用
     *
     *   当前状态
     *   老的状态
     */
    protected void onStateChanged(State newState)
    {
    	mAutoLoadingLayout.setVisibility(View.VISIBLE);
        mAutoLoadingLayout.setOnClickListener(null);
        mCurState = newState;
        switch (newState) {
            case NORMAL:
                mProgressBar.setVisibility(View.GONE);
                mHintView.setText("上拉可以加载更多");
                break;
            case LOADING:
                mProgressBar.setVisibility(View.VISIBLE);
                mHintView.setText("正在加载更多...");
                break;
            case NO_MORE_DATA:
                mProgressBar.setVisibility(View.GONE);
                mHintView.setText("没有更多了");
                break;
            case UNAVAILABLE:
            	mAutoLoadingLayout.setVisibility(View.INVISIBLE);
                break;
            case FAILED:
                mProgressBar.setVisibility(View.GONE);
                mHintView.setText("加载更多失败，点击重新加载");
                mAutoLoadingLayout.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        startLoading();
                    }
                });
                break;
            default:
                break;
        }
    }

    // private boolean isLastItemVisible2() {
    // int lastItemPosition = this.getCount() - 1;
    // int lastVisiblePosition = this.getLastVisiblePosition();
    // if (lastVisiblePosition == -1) {// 這裡主要判断数据还没有加载进来时，可以上拉
    // return true;
    // }
    // if (lastVisiblePosition == lastItemPosition) {// 当最后一项可见时
    // Rect rect = new Rect();
    // int index = this.getChildCount() - 1;
    // View lastVisibleChild = this.getChildAt(index);// 获取listview当前展示的最后一个item
    // boolean visibleRect = lastVisibleChild.getGlobalVisibleRect(rect);//
    // 获取lastVisibleChild可见部分的rect,rect就是可见部分相对于整个屏幕的坐标
    // // 当最后一项完全展开时
    // if (visibleRect
    // && lastVisibleChild.getHeight() == (rect.bottom - rect.top)) {//
    // 如果可见部分高度等于真实的高度，说明已经滚动到底部
    // return true;
    // }
    // }
    // return false;
    // }

    /**
     * 当前的状态
     */
    private enum State
    {

        /**
         * No more data
         */
        NO_MORE_DATA,

        /**
         * 正常状态，或者初始化的状态
         */
        NORMAL,

        /**
         * 加载中
         */
        LOADING,
        /**
         * 失败后的状态
         */
        FAILED,
        /**
         * 不可用的状态
         */
        UNAVAILABLE
    }

    public interface OnLoadListener
    {

        /**
         * 加载更多时会被调用或上拉时调用，子类实现具体的业务逻
         */
        void onLoad(LoadMoreListView listView);
    }

}
