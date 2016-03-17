package com.appdsn.demo.fragment;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.appdsn.demo.R;
import com.appdsn.qa.adapter.CommonSupportFragmentAdapter;
import com.appdsn.qa.fragment.BaseFragment;
import com.appdsn.qa.ui.HackyViewPager;
import com.appdsn.qa.ui.viewpagerindicator.FixPageIndicator;
import com.appdsn.qa.ui.viewpagerindicator.PageIndicator;
import com.appdsn.qa.ui.viewpagerindicator.scrollbar.ColorBar;
import com.appdsn.qa.utils.DimenUtils;

public class FilmFragment extends BaseFragment {

	private HackyViewPager viewPager;
	private FixPageIndicator indicator;
	private String[] tabNames = { "正在热映", "即将上映" };

	@Override
	protected void initVariable() {
		// TODO Auto-generated method stub

	}

	@Override
	protected int setContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_film;
	}

	@Override
	protected void findViews(View view) {
		// TODO Auto-generated method stub
		viewPager = (HackyViewPager) view.findViewById(R.id.viewPager);
		// viewPager.toggleLock();
		viewPager.setOffscreenPageLimit(2);
		indicator = (FixPageIndicator) view.findViewById(R.id.indicator);
		
	}

	@Override
	protected void loadData() {
		ShowFilmListFragment filmShowListFragment = new ShowFilmListFragment();
		PreFilmListFragment filmPreListFragment = new PreFilmListFragment();
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(filmShowListFragment);
		fragments.add(filmPreListFragment);

		// TODO Auto-generated method stub
		viewPager.setAdapter(new CommonSupportFragmentAdapter(getChildFragmentManager(),
				fragments));
		indicator.setViewPager(viewPager, 0);
		indicator.setIndicatorAdapter(new PageIndicator.IndicatorAdapter() {
			@Override
			public View getIndicatorView(int position) {
				TextView textView = (TextView) mInflater.inflate(
						R.layout.layout_main_film_tab_item, null);
				textView.setText(tabNames[position]);
				return textView;
			}

			@Override
			public void onPageScrolled(View view, int position,
					float selectPercent) {

			}
		});
		ColorBar colorBar = new ColorBar(mContext, 0xffffffff);
		colorBar.setRadius(DimenUtils.dp2px(mContext, 5));
		indicator.setScrollBar(colorBar);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

}
