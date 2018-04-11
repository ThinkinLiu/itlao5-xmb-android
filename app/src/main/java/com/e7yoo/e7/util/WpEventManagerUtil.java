package com.e7yoo.e7.util;

import android.app.Activity;
import android.content.Intent;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.GameListActivity;
import com.e7yoo.e7.app.light.FlashLightWidget;

import java.util.ArrayList;

public class WpEventManagerUtil {
	public static final String[] KEYWORDS = {"打开手电筒", "关闭手电筒", "增大音量", "减小音量", "停止", "打开电灯", "关闭电灯", "打开游戏", "小萌小萌", "说个笑话"};

	public static void doEvent(final Activity act, String word) {
		if(KEYWORDS[0].equals(word)) {
			E7App.mApp.sendBroadcast(new Intent(FlashLightWidget.ACTION_LED_ON));
		} else if(KEYWORDS[1].equals(word)) {
			E7App.mApp.sendBroadcast(new Intent(FlashLightWidget.ACTION_LED_OFF));
		} else if(KEYWORDS[2].equals(word)) {

		} else if(KEYWORDS[3].equals(word)) {

		} else if(KEYWORDS[4].equals(word)) {

		} else if(KEYWORDS[5].equals(word)) {
			E7App.mApp.sendBroadcast(new Intent(FlashLightWidget.ACTION_LED_ON));
		} else if(KEYWORDS[6].equals(word)) {
			E7App.mApp.sendBroadcast(new Intent(FlashLightWidget.ACTION_LED_OFF));
		} else if(KEYWORDS[7].equals(word)) {
			if(act != null) {
				Intent intent2Game = new Intent(act, GameListActivity.class);
				act.startActivity(intent2Game);
			} else {
			}
		}/* else if("我要发帖".equals(word)) {
			// 获取CommunitySDK实例, 参数1为Context类型
			CommunitySDK mCommSDK = CommunityFactory.getCommSDK(act);
			// 打开微社区的接口, 参数1为Context类型
			mCommSDK.openCommunity(act);
			MobclickAgent.onEvent(act, Constant.Umeng.Send_community_wpevent);
		} else if("我要分享".equals(word)) {
			ShareUtil.share(act, null, null, null, null);
			MobclickAgent.onEvent(E7App.mApp, Constant.Umeng.Send_share_wpevent);
		}*/ else if(KEYWORDS[8].equals(word)) {
			// 语音回复“萌萌在这里”
		} else if(KEYWORDS[9].equals(word)) {
		}
	}
}
