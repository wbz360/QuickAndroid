package com.appdsn.demo.activity;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.appdsn.demo.R;
import com.appdsn.demo.fragment.CinemaFragment;
import com.appdsn.demo.fragment.FilmFragment;
import com.appdsn.demo.fragment.FindFragment;
import com.appdsn.demo.fragment.MineFragment;
import com.appdsn.qa.activity.BaseActivity;
import com.appdsn.qa.adapter.CommonSupportFragmentAdapter;
import com.appdsn.qa.ui.HackyViewPager;
import com.appdsn.qa.ui.viewpagerindicator.FixPageIndicator;
import com.appdsn.qa.ui.viewpagerindicator.PageIndicator;

public class MainActivity extends BaseActivity {

	private int[] tabDrawables = { R.drawable.main_ic_tab_film_selector,
			R.drawable.main_ic_tab_cinema_selector,
			R.drawable.main_ic_tab_find_selector,
			R.drawable.main_ic_tab_mine_selector };
	private String[] tabNames = { "影片", "晒票", "发现", "我的" };
	private HackyViewPager viewPager;
	private FixPageIndicator indicator;

	@Override
	protected int setContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_main;
	}

	@Override
	protected void initVariable() {
		// TODO Auto-generated method stub
	
	}

	@Override
	protected void findViews() {
		// TODO Auto-generated method stub
		viewPager = (HackyViewPager) findViewById(R.id.viewPager);
		viewPager.toggleLock();
		viewPager.setOffscreenPageLimit(4);
		indicator = (FixPageIndicator) findViewById(R.id.indicator);
		hideTopBar();
	}

	@Override
	protected void loadData() {
		// TODO Auto-generated method stub
		FilmFragment mFilmFragment = new FilmFragment();
		MineFragment mMineFragment = new MineFragment();
		FindFragment mFindFragment = new FindFragment();
		CinemaFragment mCinemaFragment = new CinemaFragment();
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(mFilmFragment);
        fragments.add(mCinemaFragment);
        fragments.add(mFindFragment);
        fragments.add(mMineFragment);
        viewPager.setAdapter(new CommonSupportFragmentAdapter(getSupportFragmentManager(), fragments));
        indicator.setViewPager(viewPager, 0);
        indicator.setIndicatorAdapter(new PageIndicator.IndicatorAdapter()
        {
            @Override
            public View getIndicatorView(int position)
            {
                TextView textView = (TextView) mInflater.inflate(R.layout.layout_main_tab_item, null);
                textView.setText(tabNames[position]);
                textView.setCompoundDrawablesWithIntrinsicBounds(0, tabDrawables[position], 0, 0);
                return textView;
            }

            @Override
            public void onPageScrolled(View view, int position, float selectPercent)
            {
            	
            }
        });
       
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doClick(View view) {
		// TODO Auto-generated method stub

	}

}
