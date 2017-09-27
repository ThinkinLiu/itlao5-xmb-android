package com.e7yoo.e7.app.history;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.TodayHisEntity;

public class TodayHisDetailsActivity extends BaseActivity implements OnClickListener {
	TodayHisEntity entity;
	private View rootView;
	private ImageView iv;
	private TextView des;
	private TextView content;
	private ImageView iv_big;

	@Override
	protected String initTitle() {
		if (getIntent() != null) {
			entity = (TodayHisEntity) getIntent().getSerializableExtra("TodayHisEntity");
			if(entity != null) {
                String title = entity.getTitle();
                if(!TextUtils.isEmpty(title)) {
                    return title;
                }
			}
		}
		return getString(R.string.more_history);
	}

	@Override
	protected int initLayoutResId() {
		return R.layout.activity_today_history_details;
	}

	@Override
	protected void initView() {
		rootView = findViewById(R.id.rootView);
		iv = (ImageView) findViewById(R.id.pic);
		des = (TextView) findViewById(R.id.des);
		content = (TextView) findViewById(R.id.content);
		iv_big = (ImageView) findViewById(R.id.loading_big);
		iv_big.setVisibility(View.GONE);
		iv_big.setOnClickListener(this);
		rootView.setOnClickListener(this);
	}

	@Override
	protected void initSettings() {
			Glide.with(this).load(entity.getPic())
					.override((int) getResources().getDimension(R.dimen.list_news_width),
							(int) getResources().getDimension(R.dimen.list_news_height))
					.centerCrop().placeholder(R.mipmap.log_e7yoo_transport).into(iv);
			des.setText(entity.getDes());
			content.setText("　　" + entity.getContent().replace("\n", "\n\n　　"));

	}

	@Override
	protected void initViewListener() {
		iv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pic:
			if(iv_big.getVisibility() == View.GONE) {
				Glide.with(this).load(entity.getPic()).placeholder(R.mipmap.log_e7yoo_transport).into(iv_big);
				iv_big.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.loading_big:
			iv_big.setVisibility(View.GONE);
			break;
		case R.id.rootView:
			if(iv_big.getVisibility() == View.VISIBLE) {
				iv_big.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if(iv_big.getVisibility() == View.VISIBLE) {
			iv_big.setVisibility(View.GONE);
			return;
		}
		super.onBackPressed();
	}
}
