package com.e7yoo.e7.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.TodayHisEntity;
import com.e7yoo.e7.util.CommonUtil;
import com.e7yoo.e7.view.HeadListView.HeaderAdapter;

import java.util.ArrayList;

public class TodayHisAdapter extends BaseAdapter implements HeaderAdapter {
	Context mContext;
	ArrayList<TodayHisEntity> mTodayHisEntity;

	public TodayHisAdapter(Context context, ArrayList<TodayHisEntity> entitysList) {
		this.mContext = context;
		this.mTodayHisEntity = new ArrayList<TodayHisEntity>();
		if (entitysList != null) {
			this.mTodayHisEntity.addAll(entitysList);
		}
	}

	public void refresh(ArrayList<TodayHisEntity> entitysList) {
		if (this.mTodayHisEntity == null) {
			this.mTodayHisEntity = new ArrayList<TodayHisEntity>();
		}
		this.mTodayHisEntity.clear();
		if (entitysList != null) {
			this.mTodayHisEntity.addAll(entitysList);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mTodayHisEntity != null ? mTodayHisEntity.size() : 0;
	}

	@Override
	public TodayHisEntity getItem(int position) {
		// TODO Auto-generated method stub
		try {
			return mTodayHisEntity.get(position);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_news, null);
			holder.picIv = (ImageView) convertView.findViewById(R.id.list_news_iv1);
			holder.titleTv = (TextView) convertView.findViewById(R.id.list_news_title);
			holder.authorTv = (TextView) convertView.findViewById(R.id.list_news_author);
			holder.timeTv = (TextView) convertView.findViewById(R.id.list_news_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TodayHisEntity entity = mTodayHisEntity.get(position);
		RequestOptions options = new RequestOptions();
		options.override((int) mContext.getResources().getDimension(R.dimen.list_news_width),
				(int) mContext.getResources().getDimension(R.dimen.list_news_height))
				.centerCrop().placeholder(R.mipmap.log_e7yoo_transport);
		Glide.with(mContext).load(entity.getPic())
				.apply(options).into(holder.picIv);
		holder.titleTv.setText(entity.getTitle());
		holder.authorTv.setText("");
		String lunar = entity.getLunar();
		if(CommonUtil.isEmptyTrimNull(lunar)) {
			lunar = "";
		} else {
			lunar = "(农历:" + lunar +")";
		}
		holder.timeTv.setText(entity.getYear() + "/" + entity.getMonth()  + "/"+ entity.getDay() + lunar);
		return convertView;
	}

	class ViewHolder {
		private ImageView picIv;
		private TextView titleTv;
		private TextView authorTv;
		private TextView timeTv;
	}

	@Override
	public int getHeaderState(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void configureHeader(View header, int position, int alpha) {
		// TODO Auto-generated method stub

	}

}
