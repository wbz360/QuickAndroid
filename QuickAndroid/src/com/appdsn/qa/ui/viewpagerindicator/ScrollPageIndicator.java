package com.appdsn.qa.ui.viewpagerindicator;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.appdsn.qa.ui.viewpagerindicator.scrollbar.ScrollBar;

@SuppressLint("NewApi")
public class ScrollPageIndicator extends HorizontalScrollView implements
		PageIndicator {

	private ViewPager mViewPager;
	private ViewPager.OnPageChangeListener mListener;
	private int mSelectedTabIndex = -1;
	final float density;
	private IndicatorAdapter indicatorAdapter;
	private int count;
	private int mPosition = 0;
	private LinearLayout mTabLayout;
	private Runnable mTabSelector;

	public ScrollPageIndicator(Context context) {
		this(context, null);
	}

	public ScrollPageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		setHorizontalScrollBarEnabled(false);
		mTabLayout = new TabLayout(context);
		addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT,
				MATCH_PARENT));
		density = context.getResources().getDisplayMetrics().density;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		if (mListener != null) {
			mListener.onPageScrollStateChanged(arg0);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

		if (mListener != null) {
			mListener.onPageScrolled(position, positionOffset,
					positionOffsetPixels);
		}
		this.mPosition = position;
		this.mPositionOffset = positionOffset;
		Log.i("123", "position-" + position);
		Log.i("123", "mPositionOffset-" + mPositionOffset);
		changeTabView();
		mTabLayout.invalidate();
	}

	@Override
	public void onPageSelected(int arg0) {
		Log.i("123", "mPosition:" + arg0);
		setCurrentTab(arg0);
		if (mListener != null) {
			mListener.onPageSelected(arg0);
		}
	}

	private void changeTabView() {
		if (indicatorAdapter != null) {
			View curView = mTabLayout.getChildAt(mSelectedTabIndex);
			View nextView;
			if (mSelectedTabIndex != mPosition) {
				nextView = mTabLayout.getChildAt(mPosition);
				indicatorAdapter.onPageScrolled(nextView, mPosition,
						1 - mPositionOffset);
				indicatorAdapter.onPageScrolled(curView, mSelectedTabIndex,
						mPositionOffset);
			} else {
				indicatorAdapter.onPageScrolled(curView, mSelectedTabIndex,
						1 - mPositionOffset);
				if (mPosition == count - 1) {
					return;
				}
				nextView = mTabLayout.getChildAt(mPosition + 1);
				indicatorAdapter.onPageScrolled(nextView, mPosition + 1,
						mPositionOffset);

			}

		}

	}

	public void setCurrentTab(int item) {

		mSelectedTabIndex = item;
		final int tabCount = mTabLayout.getChildCount();
		for (int i = 0; i < tabCount; i++) {
			final View child = mTabLayout.getChildAt(i);
			final boolean isSelected = (i == mSelectedTabIndex);
			child.setSelected(isSelected);
			if (isSelected) {
				animateToTab(item);
			}
		}
	}

	/* ��ѡ�е�tab�ƶ����м� */
	private void animateToTab(final int position) {
		final View tabView = mTabLayout.getChildAt(position);
		if (mTabSelector != null) {
			removeCallbacks(mTabSelector);
		}
		mTabSelector = new Runnable() {
			public void run() {
				final int scrollPos = tabView.getLeft()
						- (getWidth() - tabView.getWidth()) / 2;
				smoothScrollTo(scrollPos, 0);
				mTabSelector = null;
			}
		};
		post(mTabSelector);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mTabSelector != null) {
			// Re-post the selector we saved
			post(mTabSelector);
		}
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mTabSelector != null) {
			removeCallbacks(mTabSelector);
		}
	}

	@Override
	public void setViewPager(ViewPager view) {
		setViewPager(view, 0);
	}

	@Override
	public void setViewPager(ViewPager view, int initPosition) {

		if (mViewPager == view) {
			return;
		}
		if (mViewPager != null) {
			mViewPager.addOnPageChangeListener(null);
		}
		final PagerAdapter adapter = view.getAdapter();
		if (adapter == null) {
			throw new IllegalStateException(
					"ViewPager does not have adapter instance.");
		}
		mViewPager = view;
		view.setOffscreenPageLimit(adapter.getCount()-1);
		view.addOnPageChangeListener(this);
		mSelectedTabIndex = initPosition;

	}

	public void setIndicatorAdapter(IndicatorAdapter adapter) {
		indicatorAdapter = adapter;
		notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {
		mTabLayout.removeAllViews();
		final PagerAdapter adapter = mViewPager.getAdapter();
		count = adapter.getCount();
		if (mSelectedTabIndex > count) {
			mSelectedTabIndex = count - 1;
		} else if (mSelectedTabIndex < 0) {
			mSelectedTabIndex = 0;
		}
		mPosition = mSelectedTabIndex;
		mPositionOffset = 0;
		for (int i = 0; i < count; i++) {
			if (indicatorAdapter != null) {
				addTabView(i);
			}
		}
		mViewPager.setCurrentItem(mSelectedTabIndex, false);
		if (indicatorAdapter != null) {

			indicatorAdapter.onPageScrolled(
					mTabLayout.getChildAt(mSelectedTabIndex),
					mSelectedTabIndex, 1);

		}
	}

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mListener = listener;
	}

	private void addTabView(int index) {
		final View tabView = indicatorAdapter.getIndicatorView(index);
		tabView.setTag(index);
		tabView.setFocusable(true);
		tabView.setOnClickListener(mTabClickListener);
		mTabLayout.addView(tabView, new LinearLayout.LayoutParams(WRAP_CONTENT,
				MATCH_PARENT));
	}

	private final OnClickListener mTabClickListener = new OnClickListener() {
		public void onClick(View view) {
			final int oldSelected = mViewPager.getCurrentItem();
			final int newSelected = (Integer) view.getTag();
			mPosition = newSelected;
			mPositionOffset = 0;
			if (newSelected != oldSelected) {
				if (indicatorAdapter != null) {
					indicatorAdapter.onPageScrolled(
							mTabLayout.getChildAt(newSelected), newSelected, 1);
					indicatorAdapter.onPageScrolled(
							mTabLayout.getChildAt(oldSelected), oldSelected, 0);

				}
				invalidate();
				mViewPager.setCurrentItem(newSelected, false);

			}

		}
	};

	/* ������scrollBar������÷��� */

	private ScrollBar scrollBar;
	private float mPositionOffset = 0;

	public void setScrollBar(ScrollBar scrollBar) {
		this.scrollBar = scrollBar;
		invalidate();
	}

	class TabLayout extends LinearLayout {

		public TabLayout(Context context) {
			super(context);
			ColorDrawable colorDrawable = new ColorDrawable(Color.WHITE);
			setDividerDrawable(colorDrawable);
			setBackgroundColor(Color.TRANSPARENT);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			drawScrollBar(canvas);
		}

		private void drawScrollBar(Canvas canvas) {
			if (mViewPager == null || scrollBar == null || count == 0) {
				// Log.i("123", "return:");
				return;
			}

			View curView = mTabLayout.getChildAt(mPosition);
			float offsetX = 0;
			int curtabWidth = curView.getWidth();
			int curtabHeight = curView.getHeight();
			int curbarWidth = scrollBar.getWidth(curtabWidth);
			int curbarHeight = scrollBar.getHeight(curtabHeight);
			int curCenterX = curView.getLeft() + curtabWidth / 2;

			View nextView = null;
			int nextCenterX;
			int nexttabWidth;
			int nextbarWidth=0;
			if (mPosition == count - 1) {
				nextCenterX = 0;
				nextbarWidth = 0;
			} else {
				nextView = mTabLayout.getChildAt(mPosition + 1);
				nexttabWidth = nextView.getWidth();
				nextbarWidth= scrollBar.getWidth(nexttabWidth);
				nextCenterX = nextView.getLeft() + nexttabWidth / 2;
			}

			float barCenterX = curCenterX + (nextCenterX - curCenterX)
					* mPositionOffset;
			float barWidth = curbarWidth + mPositionOffset
					* (nextbarWidth - curbarWidth);

			offsetX = barCenterX - barWidth / 2;

			int offsetY = 0;
			switch (scrollBar.getGravity()) {
			case CENTENT:
				// offsetY = (curtabHeight - curbarHeight) / 2;不考虑padingTop
				offsetY = curView.getTop() + (curtabHeight - curbarHeight) / 2;
				break;
			case TOP:
				offsetY = curView.getTop();// offsetY =0,不考虑pading
				break;
			case BOTTOM:
			default:
				// offsetY = curtabHeight - curbarHeight;//不考虑pading
				offsetY = curView.getBottom() - curbarHeight;
				break;
			}

			scrollBar.draw(canvas, offsetX, offsetY, offsetX + barWidth,
					offsetY + curbarHeight);
		}

	}

}
