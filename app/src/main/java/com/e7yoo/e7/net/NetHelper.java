package com.e7yoo.e7.net;

import android.os.Message;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.OsUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.e7yoo.e7.fragment.NewsFragment.SHOW_NEWSLIST;

public class NetHelper {
	private static NetHelper mInstance;
	private ExecutorService executorService;

	private NetHelper() {
		executorService = Executors.newFixedThreadPool(5);
	}

	public static NetHelper newInstance() {
		if(mInstance == null) {
			synchronized (NetHelper.class) {
				if(mInstance == null) {
					mInstance = new NetHelper();
				}
			}
		}
		return mInstance;
	}

	//private Net net = new ShowApiNet();
	private Net net = new Net();

	/**
	 * 历史上的今天，列表
	 */
	public void todayHistory() {
		executorService.execute(new Runnable() {
			public void run() {
				net.getToadyHistory(new NetCallback() {
					@Override
					public void callback(JSONObject object) {
						sendNetHandler(Constant.EVENT_BUS_NET_todayHistory, object);
					}
				});
			}
		});
	}

	/**
	 * 历史上的今天详情
	 * @param id
	 */
	public void todayHistoryDetails(final String id) {
		executorService.execute(new Runnable() {
			public void run() {
				net.getToadyHistoryDetails(new NetCallback() {
					@Override
					public void callback(JSONObject object) {
						sendNetHandler(Constant.EVENT_BUS_NET_todayHistoryDetails, object);
					}
				}, id);
			}
		});
	}

	/**
	 * 机器人，聊天
	 * @param content
	 */
	public void rootAsk(final String robotId, final String content, final String apikey) {
		executorService.execute(new Runnable() {
			public void run() {
				net.robotAsk(new NetCallback() {
					@Override
					public void callback(JSONObject object) {
						sendNetHandler(Constant.EVENT_BUS_NET_tobotAsk, object);
					}
				}, content, OsUtil.getUdid(E7App.mApp) + robotId, apikey);
			}
		});
	}

	/**
	 *
	 * @param page
	 * @param pagesize
	 */
	public void jokeNew(final int page, final int pagesize) {
		executorService.execute(new Runnable() {
			public void run() {
				net.jokeNew(new NetCallback() {
					@Override
					public void callback(JSONObject object) {
						sendNetHandler(Constant.EVENT_BUS_NET_jokeNew, object);
					}
				}, page, pagesize);
			}
		});
	}

	/**
	 *
	 * @param isPic
	 */
	public void jokeRand(final boolean isPic) {
		executorService.execute(new Runnable() {
			public void run() {
				net.jokeRand(new NetCallback() {
					@Override
					public void callback(JSONObject object) {
						if(isPic) {
							sendNetHandler(Constant.EVENT_BUS_NET_jokeRand_pic, object);
						} else {
							sendNetHandler(Constant.EVENT_BUS_NET_jokeRand, object);
						}
					}
				}, isPic);
			}
		});
	}

	public void wxNewsList(final NetCallback callback) {
		executorService.execute(new Runnable() {
			public void run() {
				net.wxNewsList(callback, 20, 1);
			}
		});
	}

	public void newsList(final NetCallback callback, final String newsEnName) {
		executorService.execute(new Runnable() {
			public void run() {
				net.newsList(callback, newsEnName);
			}
		});
	}

	public void getToadyHistory(final NetCallback callback) {
		executorService.execute(new Runnable() {
			public void run() {
				net.getToadyHistory(callback);
			}
		});
	}

	public void getToadyHistoryDetails(final NetCallback callback, final String id) {
		executorService.execute(new Runnable() {
			public void run() {
				net.getToadyHistoryDetails(callback, id);
			}
		});
	}

	public void sendNetHandler(int what, Object object) {
		Message msg = new Message();
		msg.what = what;
		msg.obj = object;
		EventBus.getDefault().post(msg);
	}
}
