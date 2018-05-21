package com.e7yoo.e7.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.e7yoo.e7.R;
import com.e7yoo.e7.adapter.NewsAdapter;
import com.e7yoo.e7.app.news.NewsWebviewActivity;
import com.e7yoo.e7.model.News;
import com.e7yoo.e7.model.NewsEntity;
import com.e7yoo.e7.net.Net;
import com.e7yoo.e7.net.NetCallback;
import com.e7yoo.e7.net.NetHelper;
import com.e7yoo.e7.net.NetUtils;
import com.e7yoo.e7.util.AnimaUtils;
import com.e7yoo.e7.view.HeadListView;
import com.google.gson.Gson;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsFragment extends BaseFragment {
	private final static String TAG = "NewsFragment";
	private Activity activity;
	private ArrayList<NewsEntity> newsList = new ArrayList<NewsEntity>();
	private HeadListView mListView;
	private NewsAdapter mAdapter;
	private String text;
	private int channel_id;
	private ImageView detail_loading;
	public final static int SET_NEWSLIST = 0;
	public final static int SHOW_NEWSLIST = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle args = getArguments();
		text = args != null ? args.getString("text") : News.getTypes(getActivity())[0];
		channel_id = args != null ? args.getInt("id", 1) : 1;
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		this.activity = activity;
		super.onAttach(activity);
	}

	/** 此方法意思为fragment是否可见 ,可见时候加载数据 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			// fragment可见时加载数据
			if (newsList != null && newsList.size() > 0) {
				handler.obtainMessage(SHOW_NEWSLIST).sendToTarget();
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(2);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						handler.obtainMessage(SET_NEWSLIST).sendToTarget();
					}
				}).start();
			}
		} else {
			// fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_news, null);
		mListView = (HeadListView) view.findViewById(R.id.mListView);
		detail_loading = (ImageView) view.findViewById(R.id.detail_loading);
		return view;
	}

	private void initData() {
		newsList = new ArrayList<NewsEntity>();
	}

	@SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case SET_NEWSLIST:
				AnimaUtils.startImageViewAnima(detail_loading);

				if(News.type_weixin.equals(text)) {
					NetHelper.newInstance().wxNewsList(new NetCallback() {
						@Override
						public void callback(JSONObject object) {
							sendNetHandler(SHOW_NEWSLIST, object, 1);
						}
					});
				} else {
					News.NewsType type = News.cnString2NewsType(text);
					String enName = News.newsType2String(type);
					NetHelper.newInstance().newsList(new NetCallback() {
						@Override
						public void callback(JSONObject object) {
							sendNetHandler(SHOW_NEWSLIST, object, 0);
						}
					}, enName);
				}
				break;
			case SHOW_NEWSLIST:
				AnimaUtils.removeImageViewAnima(detail_loading);
				detail_loading.setVisibility(View.GONE);
				JSONObject jo = null;
				if(msg != null && msg.obj != null) {
					jo = (JSONObject) msg.obj;
				}
				if(msg.arg1 == 1) {
					if(jo != null) {
						newsList = parseWeixin(jo);
					}
				} else{
					if(jo != null) {
						newsList = parseNews(jo);
					}
				}
				if(mAdapter == null){
					mAdapter = new NewsAdapter(activity, newsList);
					mListView.setAdapter(mAdapter);
				}
				mAdapter.refresh(newsList);
				mListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
						try {
							Intent i = new Intent(getActivity(), NewsWebviewActivity.class);
							i.putExtra(NewsWebviewActivity.INTENT_URL, mAdapter.getItem(position).getUrl());
							startActivity(i);
						} catch(Exception e) {
							e.printStackTrace();
							TastyToast.makeText(getActivity(), getString(R.string.open_news_failed), TastyToast.LENGTH_SHORT, TastyToast.INFO);
						}
					}
				});
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 
	 * @param what
	 * @param object
	 * @param isWeixin 是否微信接口 1 微信 其他非微信
	 */
	private void sendNetHandler(int what, JSONObject object, int isWeixin) {
		Message msg = new Message();
		msg.what = what;
		msg.obj = object;
		msg.arg1 = isWeixin;
		handler.sendMessage(msg);
	}

	private ArrayList<NewsEntity> parseNews(JSONObject object) {
		ArrayList<NewsEntity> list = new ArrayList<NewsEntity>();
		try {
			if (object != null) {
				if (object.getInt("error_code") == 0) {
					JSONObject jo = object.optJSONObject("result");
					if (jo != null) {
						JSONArray array = jo.optJSONArray("data");
						if (array != null && array.length() > 0) {
							NewsEntity entity;
							Gson gson = new Gson();
							for(int i = 0; i < array.length(); i++) {
								try {
									entity = gson.fromJson(array.getString(i), NewsEntity.class);
									list.add(entity);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private ArrayList<NewsEntity> parseWeixin(JSONObject object) {
		ArrayList<NewsEntity> list = new ArrayList<NewsEntity>();
		try {
			if (object != null) {
				if (object.getInt("error_code") == 0) {
					JSONObject jo = object.optJSONObject("result");
					if (jo != null) {
						JSONArray array = jo.optJSONArray("list");
						if (array != null && array.length() > 0) {
							NewsEntity entity;
							for(int i = 0; i < array.length(); i++) {
								try {
									JSONObject jobj = array.getJSONObject(i);
									entity = new NewsEntity();
									entity.setUniquekey(jobj.getString("id"));
									entity.setTitle(jobj.getString("title"));
									entity.setAuthor_name(jobj.getString("source"));
									entity.setThumbnail_pic_s(jobj.getString("firstImg"));
									entity.setCategory(jobj.getString("mark"));
									entity.setUrl(jobj.getString("url"));
									list.add(entity);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				} else {
					TastyToast.makeText(getActivity(), object.optString("reason"), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/* 摧毁视图 */
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.d("onDestroyView", "channel_id = " + channel_id);
		mAdapter = null;
	}

	/* 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "channel_id = " + channel_id);
	}

	@Override
	public void onEventMainThread(Message msg) {

	}
}
