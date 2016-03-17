package com.appdsn.qa.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.appdsn.qa.R;

/**
 * Created by baozhong 2016/02/01
 */
@SuppressLint("InflateParams") public abstract class BaseFragment extends Fragment {

    public Context mContext;
    public LayoutInflater mInflater;
    private View contentView;
    private FrameLayout layBody;
    private ViewStub stubLoadingFailed;
    private FrameLayout layLoadingFailed;
    private LinearLayout layClickReload;
    private ProgressBar loadingProgress;
    private boolean isFirstLoading=false;
    private boolean isViewCreated=false;
    private boolean isInit=false;
    @SuppressWarnings("deprecation")
	@Override
    public void onAttach(Activity activity)
    {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initVariable();
    }

    @SuppressWarnings("unchecked")
    protected final <T extends View> T $( @IdRes int id)
    {
        return (T) (contentView.findViewById(id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mInflater = inflater;
        View view = inflater.inflate(R.layout.fragment_base, null);
        // 内容区
        layBody = (FrameLayout)view.findViewById(R.id.layBody);
        stubLoadingFailed = (ViewStub) view.findViewById(R.id.stubLoadingFailed);
        addContentView();
        findViews(view);
        return view;
    }
    /***
     * 设置内容区域d
     */
    public void addContentView()
    {
        int resId = setContentView();
        View layContentView = mInflater.inflate(resId, null);
        if (layContentView == null) {
            return;
        }
        layContentView.setBackgroundColor(0x00000000);
        int contentId = getResources().getIdentifier("content", "id", mContext.getPackageName());
        contentView = layContentView.findViewById(contentId);
        if (contentView == null) {
            contentView = layContentView;
        }
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layContentView.setLayoutParams(layoutParams);
        layBody.addView(layContentView, 0);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()&&!isInit){
            loadData();
            setListener();
            isInit=true;
        }else {
            showLoadingView();//这里可以自定义显示其他view
        }
        isViewCreated=true;
    }

    /*该方法每次都在Fragment可见或不可见时调用一次，且在onCreateView方法前调用的*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        Log.i("123", "isVisibleToUser"+isVisibleToUser);
        if (isVisibleToUser&&isViewCreated&&!isInit){
            hideLoadingView();
            isInit=true;
            loadData();
            setListener();
        }
    }

    protected abstract void initVariable();
    protected abstract int setContentView();
    protected abstract void findViews(View view);
    protected abstract void loadData();
    protected abstract void setListener();

    public void showLoadingFailedView(){
        isFirstLoading=true;
        onFragmentLoadingFailed();
    }
    /*如果首次加载先失败，就显示失败界面，
   对于一个界面多个接口，之间不会有影响
   isFirstLoading=false;
   */
    public void onFragmentLoadingFailed()
    {
        if (layLoadingFailed == null || !isFirstLoading) {
            return;
        }
        layClickReload.setVisibility(View.VISIBLE);
        loadingProgress.setVisibility(View.INVISIBLE);
        isFirstLoading = false;
    }

    /*如果首次加载先成功，就显示成功界面，
       对于一个界面多个接口，之间不会有影响
       isFirstLoading=false;
       */
    public void onFragmentLoadingSuccess()
    {

        if (layLoadingFailed == null || !isFirstLoading) {
            return;
        }
        layLoadingFailed.setVisibility(View.GONE);
        if (contentView != null) {
            contentView.setVisibility(View.VISIBLE);
        }
        isFirstLoading = false;
    }
    private void hideLoadingView(){
        layLoadingFailed.setVisibility(View.GONE);
        if (contentView != null) {
            contentView.setVisibility(View.VISIBLE);
        }
    }

    private void showLoadingView(){
        if (layLoadingFailed == null) {
            layLoadingFailed = (FrameLayout) stubLoadingFailed.inflate();
            layClickReload = (LinearLayout) layLoadingFailed.findViewById(R.id.layClickReload);
            loadingProgress = (ProgressBar) layLoadingFailed.findViewById(R.id.loadingProgress);
            layClickReload.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    
                    startFragmentLoading();
                }
            });
        }
        layLoadingFailed.setVisibility(View.VISIBLE);
        layClickReload.setVisibility(View.INVISIBLE);
        loadingProgress.setVisibility(View.VISIBLE);

        if (contentView != null) {
            contentView.setVisibility(View.INVISIBLE);
        }

    }
    public void startFragmentLoading()
    {
        showLoadingView();
        isFirstLoading = true;
        onFragmentLoading();

    }
    protected void onFragmentLoading()
    {

    }
    @Override
    public void onResume() {
        super.onResume();
      
    }

    @Override
    public void onPause() {
        super.onPause();
       
    }

    @Override
    public void onStop() {
        super.onStop();
       
    }
}
