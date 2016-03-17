package com.appdsn.demo.fragment;

import java.util.ArrayList;

import android.view.View;
import android.widget.ImageView;

import com.appdsn.demo.R;
import com.appdsn.demo.adapter.FilmListAdapter;
import com.appdsn.qa.adapter.ViewHolder;
import com.appdsn.qa.fragment.BaseFragment;
import com.appdsn.qa.ui.LoadMoreListView;
import com.appdsn.qa.ui.LoadMoreListView.OnLoadListener;
import com.appdsn.qa.ui.loopbanner.LoopBanner;
import com.appdsn.qa.ui.loopbanner.LoopPageAdapter;
import com.appdsn.qa.ui.pullrefreshlayout.PullRefreshLayout;
import com.appdsn.qa.ui.pullrefreshlayout.PullRefreshLayout.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FindFragment extends BaseFragment{

	private ArrayList<String> filmListDatas;
	private FilmListAdapter filmListAdapter;
	private LoadMoreListView listFilmHot;
	private PullRefreshLayout refreshLayout;
	private LoopBanner findBanner;
	
	@Override
	protected void initVariable() {
		// TODO Auto-generated method stub
		filmListDatas = new ArrayList<String>();// 观影指数
		filmListAdapter = new FilmListAdapter(mContext, filmListDatas,
				R.layout.list_item_show_film_list);
	}

	@Override
	protected int setContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_find;
	}

	@Override
	protected void findViews(View view) {
		// TODO Auto-generated method stub
		refreshLayout = (PullRefreshLayout) view
				.findViewById(R.id.refreshLayout);
		listFilmHot = (LoadMoreListView) view.findViewById(R.id.listFilmHot);
		View headerView=mInflater.inflate(R.layout.layout_find_header_view, null);
		findBanner=(LoopBanner) headerView.findViewById(R.id.findBanner);
		listFilmHot.addHeaderView(headerView);
	}

	@Override
	protected void loadData() {
		for (int i = 0; i < 10; i++) {
			filmListDatas.add("1");
		}
		listFilmHot.setAdapter(filmListAdapter);
		listFilmHot.onLoadSucess(true);
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(PullRefreshLayout pullRefreshLayout) {
				// TODO Auto-generated method stub
				pullRefreshLayout.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						refreshLayout.finishRefresh();
					}
				}, 2000);
			}
		});
		ArrayList<String> bannerDatas = new ArrayList<String>();
		bannerDatas
				.add("http://weixin-10007714.image.myqcloud.com/weixin56a84a6144c3e1453869665");
		bannerDatas
				.add("http://weixin-10007714.image.myqcloud.com/weixin56a83f937f7cb1453866899");
		bannerDatas
				.add("http://weixin-10007714.image.myqcloud.com/weixin56a8396bdf49d1453865323");

		findBanner.setPageAdapter(new LoopPageAdapter<String>(mContext,
				bannerDatas, R.layout.layout_main_banner_item) {

			@Override
			public void convert(ViewHolder holder, String itemData, int position) {
				// TODO Auto-generated method stub
				ImageView imageView = (ImageView) holder.getConvertView();
				ImageLoader.getInstance().displayImage(itemData, imageView);

			}
		});
		
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		listFilmHot.setOnLoadListener(new OnLoadListener() {
			
			@Override
			public void onLoad(LoadMoreListView listView) {
				// TODO Auto-generated method stub
				listFilmHot.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						for (int i = 0; i < 10; i++) {
							filmListDatas.add("1");
						}
						filmListAdapter.notifyDataSetChanged();
						listFilmHot.onLoadSucess(true);
					}
				}, 2000);
			}
		});
	}
	
	
	
}
