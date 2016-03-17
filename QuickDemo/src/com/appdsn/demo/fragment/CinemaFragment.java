package com.appdsn.demo.fragment;

import java.util.ArrayList;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.appdsn.demo.R;
import com.appdsn.qa.adapter.CommonSupportFragmentAdapter;
import com.appdsn.qa.fragment.BaseFragment;
import com.appdsn.qa.ui.HackyViewPager;
import com.appdsn.qa.ui.viewpagerindicator.PageIndicator;
import com.appdsn.qa.ui.viewpagerindicator.ScrollPageIndicator;
import com.appdsn.qa.ui.viewpagerindicator.scrollbar.BitmapBar;
import com.appdsn.qa.ui.viewpagerindicator.scrollbar.ColorBar;
import com.appdsn.qa.utils.DimenUtils;

public class CinemaFragment extends BaseFragment{

	private HackyViewPager viewPager;
	private ScrollPageIndicator indicator;
	private String[] tabNames = { "全部", "前端开发" ,"后端开发", "设计","移动开发", "其他类干货","正在热映", "即将上映"};
	@Override
	protected void initVariable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int setContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_cinema;
	}

	@Override
	protected void findViews(View view) {
		// TODO Auto-generated method stub
		viewPager = (HackyViewPager) view.findViewById(R.id.viewPager);
		// viewPager.toggleLock();
		viewPager.setOffscreenPageLimit(5);
		indicator = (ScrollPageIndicator) view.findViewById(R.id.indicator);
	}

	@Override
	protected void loadData() {
		// TODO Auto-generated method stub
		ShowFilmListFragment filmShowListFragment1 = new ShowFilmListFragment();
		PreFilmListFragment filmPreListFragment1 = new PreFilmListFragment();
		ShowFilmListFragment filmShowListFragment2 = new ShowFilmListFragment();
		PreFilmListFragment filmPreListFragment2 = new PreFilmListFragment();
		ShowFilmListFragment filmShowListFragment3 = new ShowFilmListFragment();
		PreFilmListFragment filmPreListFragment3 = new PreFilmListFragment();
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(filmShowListFragment1);
		fragments.add(filmPreListFragment1);
		fragments.add(filmShowListFragment2);
		fragments.add(filmPreListFragment2);
		fragments.add(filmShowListFragment3);
		fragments.add(filmPreListFragment3);
		// TODO Auto-generated method stub
		viewPager.setAdapter(new CommonSupportFragmentAdapter(getChildFragmentManager(),
				fragments));
		indicator.setViewPager(viewPager, 0);
		indicator.setIndicatorAdapter(new PageIndicator.IndicatorAdapter() {
			@Override
			public View getIndicatorView(int position) {
				 TextView tabView = new TextView(mContext);
				  tabView.setTextColor(Color.BLACK);
			      tabView.setText(tabNames[position]);
			      tabView.setGravity(Gravity.CENTER);
			      tabView.setTextColor(Color.WHITE);
			      tabView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			      tabView.setPadding(DimenUtils.dp2px(mContext, 15), 0, DimenUtils.dp2px(mContext, 15), 0);
				return tabView;
			}

			@Override
			public void onPageScrolled(View view, int position,
					float selectPercent) {

			}
		});
		ColorBar colorBar = new ColorBar(Color.WHITE,com.appdsn.qa.ui.viewpagerindicator.scrollbar.ScrollBar.Gravity.BOTTOM);
		colorBar.setHeight(DimenUtils.dp2px(mContext, 3));
		indicator.setScrollBar(colorBar);
//		indicator.setScrollBar(new BitmapBar(mContext,R.drawable.ic_launcher));
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
