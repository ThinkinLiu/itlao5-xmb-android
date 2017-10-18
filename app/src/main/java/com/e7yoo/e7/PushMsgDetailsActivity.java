package com.e7yoo.e7;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e7yoo.e7.app.news.NewsWebviewActivity;
import com.e7yoo.e7.model.PushMsg;
import com.e7yoo.e7.model.TodayHisEntity;
import com.e7yoo.e7.util.ActivityUtil;

public class PushMsgDetailsActivity extends BaseActivity implements OnClickListener {
	PushMsg entity;
	private View rootView;
	private ImageView iv;
	private TextView title;
	private TextView time;
	private TextView content;
	private TextView urlHint;
	private ImageView iv_big;

	@Override
	protected String initTitle() {
		return getString(R.string.push_msg_details);
	}

	@Override
	protected int initLayoutResId() {
		return R.layout.activity_push_msg_details;
	}

	@Override
	protected void initView() {
		rootView = findViewById(R.id.rootView);
		iv = (ImageView) findViewById(R.id.push_msg_details_pic);
		title = (TextView) findViewById(R.id.push_msg_details_title);
		time = (TextView) findViewById(R.id.push_msg_details_time);
		content = (TextView) findViewById(R.id.push_msg_details_content);
		urlHint = (TextView) findViewById(R.id.push_msg_details_url_hint);
		iv_big = (ImageView) findViewById(R.id.push_msg_details_loading_big);
	}

	@Override
	protected void initSettings() {
		iv_big.setVisibility(View.GONE);
		if (getIntent() != null) {
			entity = (PushMsg) getIntent().getSerializableExtra("PushMsg");
			if(entity != null) {
				RequestOptions options = new RequestOptions();
				options.override((int) getResources().getDimension(R.dimen.list_news_width),
						(int) getResources().getDimension(R.dimen.list_news_height))
						.centerCrop().placeholder(R.mipmap.log_e7yoo_transport);
				Glide.with(this).load(entity.getContent_pic_url())
						.apply(options).into(iv);
				title.setText(entity.getTitle());
				content.setText("　　" + entity.getContent().replace("\n", "\n\n　　"));
				if(!TextUtils.isEmpty(entity.getContent_url())) {
					if(!TextUtils.isEmpty(entity.getContent_url_hint())) {
						urlHint.setText(entity.getContent_url_hint());
					}
				} else {
					urlHint.setVisibility(View.GONE);
				}
				time.setText(entity.getMsg_time());
			}
		}

	}

	@Override
	protected void initViewListener() {
		iv.setOnClickListener(this);
		urlHint.setOnClickListener(this);
		iv_big.setOnClickListener(this);
		rootView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pic:
			if(iv_big.getVisibility() == View.GONE) {
				RequestOptions options = new RequestOptions();
				options.placeholder(R.mipmap.log_e7yoo_transport);
				Glide.with(this).load(entity.getContent_pic_url()).apply(options).into(iv_big);
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
		case R.id.push_msg_details_url_hint:
			ActivityUtil.toNewsWebviewActivity(this, entity.getContent_url(), NewsWebviewActivity.INTENT_FROM_PUSH_MSG_DETAILS);
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
