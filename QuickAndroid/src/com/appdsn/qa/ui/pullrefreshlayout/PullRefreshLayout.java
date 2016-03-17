package com.appdsn.qa.ui.pullrefreshlayout;

import java.security.InvalidParameterException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.appdsn.qa.R;

public class PullRefreshLayout extends ViewGroup {

	private static final int STYLE_MATERIAL = 0;
	private static final int STYLE_ARROW = 1;
	private static final int STYLE_SMARTISAN = 2;
	private static final int DEFAULT_REFRESH_DISTANCE = 64;// dp,默认拉动释放后可以刷新的距离
	private static final int DEFAULT_HEADVIEW_HEIGHT = 40;// dp,默认头部显示view的高宽度
	private static final float offsetRadio = 2.5f;// 阻尼系数
	// *************************************************************************
	protected boolean isRefreshing = false;
	protected float startRefreshDistance = 0;// 头部下拉多少高度释放后会刷新，默认是64
	protected FrameLayout mHeadViewLayout;// 头部布局
	private BaseHeadView mHeadView;// 头部显示的view
	private View mContentView;// 内容布局
	private boolean isOverlay = false;// 下拉时头部是否覆盖在内容上
	private int refreshColor;
	private int refreshType;
	private OnRefreshListener refreshListener;

	private float mCurrentOffset = 0;// 当前下拉的距离
	private boolean mDispatchTargetTouchDown = false;
	private float xLast;
	private float yLast;
	private float xFirstPoint;
	private float yFirstPoint;
	private float xDistance;
	private float yDistance;
	private VelocityTracker vTracker = null;
	private ObjectAnimator animAutoScroll;
	private boolean isEnabled = true;// 刚开始是否可以拉动

	public PullRefreshLayout(Context context) {
		this(context, null);
	}

	/* 构造函数里初始化一些属性数据，此时view还没有被画出来，构造时view布局中的子view还没有被添加进来，getChildCount==0 */
	public PullRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) {
			return;
		}
		TypedArray t = context.obtainStyledAttributes(attrs,
				R.styleable.PullRefreshLayout);
		isOverlay = t
				.getBoolean(R.styleable.PullRefreshLayout_isOverlay, false);
		refreshColor = t.getColor(R.styleable.PullRefreshLayout_refreshColor,
				Color.DKGRAY);
		refreshType = t.getInteger(R.styleable.PullRefreshLayout_refreshType,
				STYLE_ARROW);
		t.recycle();
	}

	public void setRefreshStyle(int type) {
		switch (type) {
		case STYLE_MATERIAL:
			MaterialHeadView mHeadView = new MaterialHeadView(getContext());
			mHeadView.setColorSchemeColors(new int[] { refreshColor });
			isOverlay = true;
			setHeaderView(mHeadView);
			break;

		case STYLE_ARROW:
			ArrowHeadView arrowHeadView = new ArrowHeadView(getContext());
			arrowHeadView.setColor(refreshColor);
			isOverlay = false;
			setHeaderView(arrowHeadView);
			break;

		case STYLE_SMARTISAN:
			SmartisanHeadView smartisanHeadView = new SmartisanHeadView(
					getContext());
			smartisanHeadView.setColor(refreshColor);
			isOverlay = false;
			setHeaderView(smartisanHeadView);
			break;
		default:
			throw new InvalidParameterException("Type does not exist");
		}
	}

	public void setHeaderView(BaseHeadView headerView) {
		if (headerView == null) {
			return;
		}
		mHeadView = headerView;
		post(new Runnable() {
			@Override
			public void run() {
				startRefreshDistance = mHeadView.getStartRefreshDistance();
				if (startRefreshDistance <= 0) {
					startRefreshDistance = dip2px(DEFAULT_REFRESH_DISTANCE);
				}
				FrameLayout.LayoutParams layoutParams = mHeadView
						.getFrameLayoutParams();
				if (layoutParams == null) {
					layoutParams = new FrameLayout.LayoutParams(
							dip2px(DEFAULT_HEADVIEW_HEIGHT),
							dip2px(DEFAULT_HEADVIEW_HEIGHT));
					layoutParams.gravity = Gravity.CENTER;
				}
				mHeadViewLayout.removeAllViews();
				mHeadViewLayout.addView((View) mHeadView, layoutParams);
				mHeadViewLayout.getLayoutParams().height = 0;
				ViewCompat.setTranslationY(mContentView, 0);
				isRefreshing = false;
			}
		});
	}

	public void setIsOverLay(boolean isOverLay) {
		this.isOverlay = isOverLay;
	}

	public void setRefreshDistance(float refreshDistance) {
		this.startRefreshDistance = refreshDistance;
	}

	public void setHeadViewLayoutBackgroundColor(int color) {
		mHeadViewLayout.setBackgroundColor(color);
	}

	public void setHeadViewLayoutBackgroundDrawable(int resId) {
		mHeadViewLayout.setBackgroundResource(resId);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		// removeCallbacks(null);
		if (vTracker != null) {
			vTracker.clear();
		}
	}

	/*
	 * 初始化view，该方法在onMeasure之前，在构造方法以后 这里需要注意：不能在该方法之前来使用view
	 */
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Context context = getContext();
		// 这里给头部添加一个FrameLayout，主要是为了扩展性考虑，后面可以自定义头部view，以及头部view的位置
		FrameLayout headViewLayout = new FrameLayout(context);
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 0);
		headViewLayout.setLayoutParams(layoutParams);
		headViewLayout.setBackgroundColor(Color.TRANSPARENT);
		mHeadViewLayout = headViewLayout;
		addView(mHeadViewLayout);// 该view覆盖在mContentView上

		int count = getChildCount();
		if (count > 2) {
			throw new RuntimeException("can only have one child widget");
		}
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child != mHeadViewLayout) {
				mContentView = child;
				break;
			}
		}

		if (mHeadView == null) {// 如果在代码里没有设置一个headerview，这里就初始化一个headview
			setRefreshStyle(refreshType);
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mContentView == null) {
			return;
		}

		mHeadViewLayout.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth()
				- getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(
						mHeadViewLayout.getLayoutParams().height,
						MeasureSpec.EXACTLY));
		mContentView.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth()
				- getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(getMeasuredHeight()
						- getPaddingTop() - getPaddingBottom(),
						MeasureSpec.EXACTLY));

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (mContentView == null) {
			return;
		}
		final int width = getMeasuredWidth();
		final int height = getMeasuredHeight();
		final int childLeft = getPaddingLeft();
		final int childTop = getPaddingTop();
		final int childWidth = width - getPaddingLeft() - getPaddingRight();
		final int childHeight = height - getPaddingTop() - getPaddingBottom();
		int headHeight = mHeadViewLayout.getMeasuredHeight();

		mHeadViewLayout.layout(childLeft, childTop, childLeft + childWidth,
				headHeight);
		mContentView.layout(childLeft, childTop, childLeft + childWidth,
				childTop + childHeight);
	}

	@SuppressLint("Recycle")
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// Log.i("123", "onInterceptTouchEvent");
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDispatchTargetTouchDown = false;
			xDistance = yDistance = 0f;
			xFirstPoint = ev.getX();
			yFirstPoint = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (mContentView == null) {
				return false;
			}
			int mTouchSlop = ViewConfiguration.get(getContext())
					.getScaledTouchSlop();
			float deltaY = ev.getY() - yFirstPoint;
			float absDiff = Math.abs(deltaY);// 取绝对值
			if (absDiff < mTouchSlop) {
				return false;
			}
			if (vTracker == null) {
				vTracker = VelocityTracker.obtain();
			} else {
				vTracker.clear();
			}
			if (animAutoScroll != null && animAutoScroll.isRunning()) {
				animAutoScroll.cancel();
			}
			if (!isRefreshing) {
				mHeadView.onStart(this);
			}
			isEnabled = true;
			xLast = ev.getX();// 初始化位置，防止突然下拉一定距离
			yLast = ev.getY();
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// Log.i("123", "onTouchEvent-"+e.getAction());
		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			vTracker.addMovement(e);
			/* 解决冲突 */
			final float curX = e.getX();
			final float curY = e.getY();
			xDistance = Math.abs(curX - xLast);
			yDistance = Math.abs(curY - yLast);
			float dy = curY - yLast;
			float offsetY = dy / offsetRadio;
			xLast = curX;
			yLast = curY;
			mCurrentOffset = mHeadViewLayout.getLayoutParams().height;

			if (!isEnabled) {
				return true;// 如果在完成动画的过程中，拉动啥也不做
			}

			if (xDistance > yDistance && mCurrentOffset == 0) {
				dispatchTouchEventToChild(e);
				return true;
			}
			/* 下面判断逻辑 */
			// Log.i("123", "mCurrentOffset" + mCurrentOffset);
			if (dy < 0 && mCurrentOffset == 0) {// 当位置在0且上拉
				dispatchTouchEventToChild(e);
			} else if (dy > 0 && canChildScrollUp()) {
				dispatchTouchEventToChild(e);
			} else if (isOverlay && isRefreshing) {
				dispatchTouchEventToChild(e);// 覆盖在上方的时候，当刷新时，头部不会挤上去，也不会下拉了
			} else {
				double offsetY2 = Math.rint(offsetY);// 转换成整型数，因为height是整数，而TranslationY是float，两个数值要统一
//				Log.i("123","offsetY1:"+ offsetY);
//				Log.i("123","offsetY2:"+ offsetY2);
				if (offsetY2 == 0) {
					return true;
				}
				mCurrentOffset += offsetY2;
				if (mCurrentOffset < 0) {// 这里很重要，不然快速滑动时mCurrentOffset不会等于0
					mCurrentOffset = 0;
				}
				if (mContentView != null) {
					pullHeaderLayout(mCurrentOffset);// 在当前位置的基础上，移动头部布局
					if (!isOverlay) {// 在当前位置的基础上，移动内容view
						ViewCompat
								.setTranslationY(mContentView, mCurrentOffset);
					}

				}
			}

			return true;
		case MotionEvent.ACTION_CANCEL:
			// Log.i("123", "ACTION_CANCEL");
		case MotionEvent.ACTION_UP:
			vTracker.computeCurrentVelocity(1);// 微妙单位
			// Log.i("123", "YVelocity1" + vTracker.getYVelocity());
			if (mDispatchTargetTouchDown) {
				dispatchTouchEventToChild(e);
			}
			if (mContentView != null) {
				if (isOverlay) {
					if (isRefreshing) {
						autoScrollView(vTracker.getYVelocity());
						return true;// 正在刷新，只实现自动滑动效果
					}
					int height = mHeadViewLayout.getLayoutParams().height;
					if (height > startRefreshDistance) {
						setRefreshing();
						translationHeaderLayout(startRefreshDistance,
								mHeadViewLayout, null);
						// mHeadViewLayout.getLayoutParams().height = (int)
						// startRefreshDistance;
						// mHeadViewLayout.requestLayout();
					} else if (height != 0) {
						translationHeaderLayout(0, mHeadViewLayout, null);
						// mHeadViewLayout.getLayoutParams().height = 0;
						// mHeadViewLayout.requestLayout();
					} else {
						// 实现自动滑动效果
						autoScrollView(vTracker.getYVelocity());
					}

				} else {
					float transY = ViewCompat.getTranslationY(mContentView);
					if (transY >= startRefreshDistance) {
						createAnimatorTranslationY(mContentView,
								startRefreshDistance, mHeadViewLayout, null);
						setRefreshing();
					} else if (transY != 0) {
						createAnimatorTranslationY(mContentView, 0,
								mHeadViewLayout, null);
					} else {
						// 实现自动滑动效果
						autoScrollView(vTracker.getYVelocity());
					}
				}

			}
			return true;
		}
		return super.onTouchEvent(e);
	}

	private void dispatchTouchEventToChild(MotionEvent e) {
		if (mDispatchTargetTouchDown) {
			mContentView.dispatchTouchEvent(e);
		} else {
			// 两个事件同时，ACTION_DOWN是必须的，后面接着ACTION_MOVE，是为了防止触发按下的效果
			MotionEvent obtain = MotionEvent.obtain(e);
			obtain.setAction(MotionEvent.ACTION_DOWN);
			obtain.setLocation(xFirstPoint, yFirstPoint);
			mContentView.dispatchTouchEvent(obtain);
			obtain.recycle();
			MotionEvent obtain2 = MotionEvent.obtain(e);
			obtain2.setAction(MotionEvent.ACTION_MOVE);
			mContentView.dispatchTouchEvent(obtain2);
			obtain2.recycle();
			mDispatchTargetTouchDown = true;
		}

	}

	protected void pullHeaderLayout(float offsetY) {
		mHeadViewLayout.getLayoutParams().height = (int) offsetY;
		mHeadViewLayout.requestLayout();// 必须要请求重新布局，包括布局子view
		float percent = offsetY / startRefreshDistance;// 下拉高度的百分比
		if (percent > 1) {
			percent = 1;
		}
		if (mHeadView != null && !isRefreshing) {// 不在刷新时才会调用（可以更具需要修改此处逻辑）
			mHeadView.onPull(PullRefreshLayout.this, percent);
		}
	}

	@SuppressLint("NewApi")
	private void autoScrollView(float yVelocity) {
		// Log.i("123", "yVelocity:" + yVelocity);
		if (-1 < yVelocity && yVelocity < 1) {
			return;
		}

		animAutoScroll = ObjectAnimator//
				.ofFloat(mContentView, "tag", -yVelocity, 0)// yVelocity初始速度
				.setDuration(500);//
		animAutoScroll.setInterpolator(new DecelerateInterpolator((float) 0.5));// 差值器，越大变化的幅度越大
		animAutoScroll.start();
		animAutoScroll
				.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					long lastPlayTime = 0;

					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						long difTime = animation.getCurrentPlayTime()
								- lastPlayTime;// 相差时间
						lastPlayTime = animation.getCurrentPlayTime();// 每次间隔差不多15，和Duration大小没关系
						// Log.i("123", "lastPlayTime" + lastPlayTime);
						float s = (Float) animation.getAnimatedValue()
								* difTime;// difTime时间内滚动的距离
						if (mContentView instanceof AbsListView) {
							AbsListView absListView = (AbsListView) mContentView;
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
								absListView.scrollListBy((int) s);
							} else {
								absListView.smoothScrollBy((int) s, 0);
							}
						} else if (mContentView instanceof ViewGroup) {
							mContentView.scrollBy(0, (int) s);
						}
					}
				});

	}

	public void autoRefresh() {
		autoRefresh(500);
	}

	public void autoRefresh(long delayTime) {
		if (!isRefreshing) {
			postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mContentView == null) {
						return;
					}
					setRefreshing();
					if (isOverlay) {
						// mHeadViewLayout.getLayoutParams().height = (int)
						// startRefreshDistance;
						// mHeadViewLayout.requestLayout();
						translationHeaderLayout(startRefreshDistance,
								mHeadViewLayout, null);
					} else {
						createAnimatorTranslationY(mContentView,
								startRefreshDistance, mHeadViewLayout, null);
					}
				}
			}, delayTime);

		}

	}

	private void setRefreshing() {
		if (!isRefreshing) {
			isRefreshing = true;
			if (mHeadView != null) {
				mHeadView.onRefresh(PullRefreshLayout.this);
			}
			if (refreshListener != null) {
				refreshListener.onRefresh(PullRefreshLayout.this);
			}
		}
	}

	public void finishRefresh() {

		if (mContentView != null && isRefreshing) {
			// ViewPropertyAnimatorCompat viewPropertyAnimatorCompat =
			// ViewCompat.animate(mContentView);
			// viewPropertyAnimatorCompat.setDuration(200);
			// viewPropertyAnimatorCompat.y(ViewCompat.getTranslationY(mContentView));
			// viewPropertyAnimatorCompat.translationY(0);
			// viewPropertyAnimatorCompat.setInterpolator(new
			// DecelerateInterpolator());
			// viewPropertyAnimatorCompat.start();

			if (mHeadView != null) {
				mHeadView.onComplete(this);
			}
			if (isOverlay) {
				postDelayed(new Runnable() {
					@Override
					public void run() {

						translationHeaderLayout(0, mHeadViewLayout,
								new AnimatorListenerAdapter() {
									@Override
									public void onAnimationEnd(
											Animator animation) {
										super.onAnimationEnd(animation);
										isRefreshing = false;
										isEnabled = false;
									}
								});
					}
				}, 200);

			} else {
				postDelayed(new Runnable() {
					@Override
					public void run() {
						createAnimatorTranslationY(mContentView, 0,
								mHeadViewLayout,
								new ViewPropertyAnimatorListenerAdapter() {
									@Override
									public void onAnimationEnd(View view) {
										super.onAnimationEnd(view);
										isRefreshing = false;
										isEnabled = false;
									}
								});
					}
				}, 200);

			}
		}

	}

	public void createAnimatorTranslationY(final View v, final float h,
			final FrameLayout fl, ViewPropertyAnimatorListener animatorListener) {

		ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = ViewCompat
				.animate(v);
		viewPropertyAnimatorCompat.setDuration(200);
		viewPropertyAnimatorCompat
				.setInterpolator(new DecelerateInterpolator());
		viewPropertyAnimatorCompat.translationY(h);
		viewPropertyAnimatorCompat.setListener(animatorListener);
		viewPropertyAnimatorCompat
				.setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(View view) {
						float height = ViewCompat.getTranslationY(v);
						fl.getLayoutParams().height = (int) height;
						fl.requestLayout();

						float percent = height / startRefreshDistance;// 下拉高度的百分比
						if (percent > 1) {
							percent = 1;
						}
						if (mHeadView != null && !isRefreshing) {// 不在刷新时才会调用（可以更具需要修改此处逻辑）
							mHeadView.onPull(PullRefreshLayout.this, percent);
						}
					}
				});
		viewPropertyAnimatorCompat.start();

	}

	public void translationHeaderLayout(final float h, final FrameLayout fl,
			Animator.AnimatorListener animatorListener) {

		ObjectAnimator anim = ObjectAnimator//
				.ofFloat(fl, "tag", fl.getHeight(), h)//
				.setDuration(200);//
		if (animatorListener != null) {
			anim.addListener(animatorListener);
		}
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float height = (Float) animation.getAnimatedValue();
				fl.getLayoutParams().height = (int) height;
				fl.requestLayout();

				float percent = height / startRefreshDistance;// 下拉高度的百分比
				if (percent > 1) {
					percent = 1;
				}
				if (mHeadView != null && !isRefreshing) {// 不在刷新时才会调用（可以更具需要修改此处逻辑）
					mHeadView.onPull(PullRefreshLayout.this, percent);
				}
			}
		});
		anim.start();

	}

	/**
	 * contentview是否上滚了一段，0初始位置不包括在内
	 */
	public boolean canChildScrollUp() {
		if (mContentView == null) {
			return false;
		}
		if (Build.VERSION.SDK_INT < 14) {
			if (mContentView instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) mContentView;
				return absListView.getChildCount() > 0
						&& (absListView.getFirstVisiblePosition() > 0 || absListView
								.getChildAt(0).getTop() < absListView
								.getPaddingTop());
			} else {
				return ViewCompat.canScrollVertically(mContentView, -1)
						|| mContentView.getScrollY() > 0;
			}
		} else {
			return ViewCompat.canScrollVertically(mContentView, -1);
		}
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		this.refreshListener = listener;
	}

	public int dip2px(float dpValue) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static interface OnRefreshListener {
		public void onRefresh(PullRefreshLayout pullRefreshLayout);

	}
}
