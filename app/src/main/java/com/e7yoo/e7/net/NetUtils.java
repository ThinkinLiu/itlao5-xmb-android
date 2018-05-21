package com.e7yoo.e7.net;

import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

public class NetUtils {

	public static void todayHistory(final Handler mHandler, final int mHandlerWhat) {
		NetHelper.newInstance().getToadyHistory(new NetCallback() {
			@Override
			public void callback(JSONObject object) {
				sendNetHandler(mHandler, mHandlerWhat, object);
			}
		});
	}
	
	public static void todayHistoryDetails(final Handler mHandler, final int mHandlerWhat, final String id) {
		NetHelper.newInstance().getToadyHistoryDetails(new NetCallback() {
			@Override
			public void callback(JSONObject object) {
				sendNetHandler(mHandler, mHandlerWhat, object);
			}
		}, id);
	}
	
	public static void sendNetHandler(Handler mHandler, int what, JSONObject object) {
		Message msg = new Message();
		msg.what = what;
		msg.obj = object;
		mHandler.sendMessage(msg);
	}
}
