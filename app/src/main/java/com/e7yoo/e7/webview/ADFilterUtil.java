package com.e7yoo.e7.webview;

import android.content.Context;
import android.content.res.Resources;

import com.e7yoo.e7.R;

public class ADFilterUtil {
	/*public static final String[] adUrls = { 
			"ubmcmm.baidustatic.com", 
			"cpro2.baidustatic.com", 
			"cpro.baidustatic.com",
			"s.lianmeng.360.cn", 
			"nsclick.baidu.com", 
			"pos.baidu.com", 
			"cbjs.baidu.com", 
			"cpro.baidu.com",
			"images.sohu.com/cs/jsfile/js/c.js", 
			"union.sogou.com/", 
			"sogou.com/", 
			"a.baidu.com", 
			"c.baidu.com", 
			};*/

	public static boolean hasAd(Context context, String url) {
		Resources res = context.getResources();
		String[] adUrls = res.getStringArray(R.array.adBlockUrl);
		for (String adUrl : adUrls) {
			if (url.contains(adUrl)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAd(Context context, String url) {
		Resources res = context.getResources();
		String[] adUrls = res.getStringArray(R.array.adUrl);
		for (String adUrl : adUrls) {
			if (url.equals(adUrl)) {
				return true;
			}
		}
		return false;
	}

}
