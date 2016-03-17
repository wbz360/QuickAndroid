package com.appdsn.demo.adapter;

import java.util.List;

import android.content.Context;

import com.appdsn.qa.adapter.CommonBaseAdapter;
import com.appdsn.qa.adapter.ViewHolder;

public class FilmListAdapter extends
		CommonBaseAdapter<String> {

	public int type = 0;// 0按时间最新上映，1默认按月公证指数

	public FilmListAdapter(Context context,
			List<String> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public void convert(ViewHolder holder, String itemData,
			int position) {

		

	}

}
