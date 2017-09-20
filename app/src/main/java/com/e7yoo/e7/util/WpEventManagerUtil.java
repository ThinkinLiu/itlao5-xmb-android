package com.e7yoo.e7.util;

import android.app.Activity;
import android.content.Intent;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.GameListActivity;
import com.e7yoo.e7.app.light.FlashLightWidget;

public class WpEventManagerUtil {

	public static void doEvent(final Activity act, String word) {
		if("打开手电筒".equals(word)) {
			E7App.mApp.sendBroadcast(new Intent(FlashLightWidget.ACTION_LED_ON));
		} else if("关闭手电筒".equals(word)) {
			E7App.mApp.sendBroadcast(new Intent(FlashLightWidget.ACTION_LED_OFF));
		} else if("增大音量".equals(word)) {

		} else if("减小音量".equals(word)) {

		} else if("停止".equals(word)) {

		} else if("打开电灯".equals(word)) {
			E7App.mApp.sendBroadcast(new Intent(FlashLightWidget.ACTION_LED_ON));
		} else if("关闭电灯".equals(word)) {
			E7App.mApp.sendBroadcast(new Intent(FlashLightWidget.ACTION_LED_OFF));
		} else if("打开游戏".equals(word)) {
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
		}*/ else if("小萌小萌".equals(word)) {
			// 语音回复“萌萌在这里”
		} else if("说个笑话".equals(word)) {
		}
	}
}
