package com.e7yoo.e7.app.history;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.e7yoo.e7.BaseActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.TodayHisAdapter;
import com.e7yoo.e7.model.TodayHisEntity;
import com.e7yoo.e7.net.NetUtils;
import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.AnimaUtils;
import com.e7yoo.e7.util.CommonUtil;
import com.google.gson.Gson;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TodayHisActivity extends BaseActivity implements OnClickListener {

	private ListView mListView;
	private ImageView loadingIv;
	private ArrayList<TodayHisEntity> newsList = new ArrayList<TodayHisEntity>();
	private TodayHisAdapter mAdapter;
	public static final int HANDLER_TODAY_HISTORY = 1005;
	public static final int HANDLER_TODAY_HISTORY_DETAILS = 1006;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_TODAY_HISTORY:
				AnimaUtils.removeImageViewAnima(loadingIv);
				loadingIv.setVisibility(View.GONE);
				newsList = parseTodayHistory((JSONObject) msg.obj);
				mAdapter.refresh(newsList);
				mListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
						try {
							NetUtils.todayHistoryDetails(mHandler, HANDLER_TODAY_HISTORY_DETAILS, mAdapter.getItem(position).get_id());
						} catch(Exception e) {
							e.printStackTrace();
							TastyToast.makeText(TodayHisActivity.this, getString(R.string.open_history_details_failed), TastyToast.LENGTH_SHORT, TastyToast.INFO);
						}
					}
				});
				break;
			case HANDLER_TODAY_HISTORY_DETAILS:
				parseTodayHistoryDetails((JSONObject) msg.obj);
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected String initTitle() {
		return getString(R.string.today_history);
	}

	@Override
	protected int initLayoutResId() {
		return R.layout.activity_today_history;
	}

	@Override
	protected void initView() {
		mListView = (ListView) findViewById(R.id.mListView);
		loadingIv = (ImageView) findViewById(R.id.loading);
	}

	@Override
	protected void initSettings() {
		newsList = new ArrayList<TodayHisEntity>();
		mAdapter = new TodayHisAdapter(this, newsList);
		mListView.setAdapter(mAdapter);
		AnimaUtils.startImageViewAnima(loadingIv);
		NetUtils.todayHistory(mHandler, HANDLER_TODAY_HISTORY);
	}

	@Override
	protected void initViewListener() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			default:
				break;
		}
	}
	
	private ArrayList<TodayHisEntity> parseTodayHistory(JSONObject object) {
		ArrayList<TodayHisEntity> entitys = new ArrayList<TodayHisEntity>();
		try {
			if (object != null) {
				if (object.getInt("error_code") == 0) {
					JSONArray ja = object.optJSONArray("result");
					if (ja != null && ja.length() > 0) {
						TodayHisEntity entity;
						Gson gson = new Gson();
						for(int i = 0; i < ja.length(); i++) {
							String jo = ja.optString(i);
							if(!CommonUtil.isEmptyTrimNull(jo)) {
								try {
									entity = gson.fromJson(jo, TodayHisEntity.class);
									entitys.add(entity);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return entitys;
	}

	private void parseTodayHistoryDetails(JSONObject object) {
		try {
			if (object != null) {
				if (object.getInt("error_code") == 0) {
					String jo = object.optString("result");
					if (!CommonUtil.isEmptyTrimNull(jo)) {
						if(jo.trim().startsWith("[")) {
							jo = new JSONArray(jo).optString(0);
						}
						TodayHisEntity entity = new Gson().fromJson(jo, TodayHisEntity.class);
						Intent i = new Intent(this, TodayHisDetailsActivity.class);
						i.putExtra("TodayHisEntity", entity);
						ActivityUtil.toActivity(this, i);
						return;
						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TastyToast.makeText(TodayHisActivity.this, getString(R.string.open_history_details_failed), TastyToast.LENGTH_SHORT, TastyToast.INFO);
	}
}
