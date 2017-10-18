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
import com.e7yoo.e7.model.NewsEntity;
import com.e7yoo.e7.view.HeadListView.HeaderAdapter;

import java.util.ArrayList;

public class NewsAdapter extends BaseAdapter implements HeaderAdapter {
	Context mContext;
	ArrayList<NewsEntity> mNewsList;

	public NewsAdapter(Context context, ArrayList<NewsEntity> newsList) {
		this.mContext = context;
		this.mNewsList = new ArrayList<NewsEntity>();
		if (newsList != null) {
			this.mNewsList.addAll(newsList);
		}
	}

	public void deleteData(int position) {
		this.mNewsList.remove(position);
		notifyDataSetChanged();
	}

	public void refresh(ArrayList<NewsEntity> newsList) {
		if (this.mNewsList == null) {
			this.mNewsList = new ArrayList<NewsEntity>();
		}
		this.mNewsList.clear();
		if (newsList != null) {
			this.mNewsList.addAll(newsList);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mNewsList != null ? mNewsList.size() : 0;
	}

	@Override
	public NewsEntity getItem(int position) {
		// TODO Auto-generated method stub
		try {
			return mNewsList.get(position);
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
		NewsEntity news = mNewsList.get(position);

		RequestOptions options = new RequestOptions();
		options.override((int) mContext.getResources().getDimension(R.dimen.list_news_width),
				(int) mContext.getResources().getDimension(R.dimen.list_news_height))
				.centerCrop().placeholder(R.mipmap.log_e7yoo_transport);
		Glide.with(mContext).load(news.getThumbnail_pic_s())
				.apply(options).into(holder.picIv);
		holder.titleTv.setText(news.getTitle());
		holder.authorTv.setText(mContext.getString(R.string.news_from) + news.getAuthor_name());
		holder.timeTv.setText(news.getDate());
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
