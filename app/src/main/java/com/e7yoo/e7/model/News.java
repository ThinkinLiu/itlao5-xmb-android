package com.e7yoo.e7.model;

import android.annotation.SuppressLint;
import android.content.Context;

import com.e7yoo.e7.util.CommonUtil;

public class News {
	public static final String type_weixin = "微信精选";
	private static final String[] types_all = { "头条", "社会", "国内", "国际", "娱乐", "体育", "军事", "科技", "财经", "时尚" };
	private static final String[] types_no_junshi = { "头条", "社会", "国内", "国际", "娱乐", "体育", "科技", "财经", "时尚" };
	private static String[] types;
	
	@SuppressLint("DefaultLocale")
	private static void init(Context context) {
		if(CommonUtil.isChannel(context, "bd")) {
			types = types_no_junshi;
		} else {
			types = types_all;
		}
	}
	
	public static String[] getTypes(Context context) {
		if(types == null) {
			init(context);
		}
		return types;
	}
	
	public static String newsType2String(NewsType type) {
		switch (type) {
		case top:
			return "top";
		case shehui:
			return "shehui";
		case guonei:
			return "guonei";
		case guoji:
			return "guoji";
		case yule:
			return "yule";
		case tiyu:
			return "tiyu";
		case junshi:
			return "junshi";
		case keji:
			return "keji";
		case caijing:
			return "caijing";
		case shishang:
			return "shishang";
		default:
			return "top";
		}
	}

	public static String newsType2CnString(NewsType type) {
		switch (type) {
		case top:
			return "头条";
		case shehui:
			return "社会";
		case guonei:
			return "国内";
		case guoji:
			return "国际";
		case yule:
			return "娱乐";
		case tiyu:
			return "体育";
		case junshi:
			return "军事";
		case keji:
			return "科技";
		case caijing:
			return "财经";
		case shishang:
			return "时尚";
		default:
			return "头条";
		}
	}

	public static NewsType cnString2NewsType(String type) {
		if ("头条".equals(type)) {
			return NewsType.top;
		} else if ("社会".equals(type)) {
			return NewsType.shehui;
		} else if ("国内".equals(type)) {
			return NewsType.guonei;
		} else if ("国际".equals(type)) {
			return NewsType.guoji;
		} else if ("娱乐".equals(type)) {
			return NewsType.yule;
		} else if ("体育".equals(type)) {
			return NewsType.tiyu;
		} else if ("军事".equals(type)) {
			return NewsType.junshi;
		} else if ("科技".equals(type)) {
			return NewsType.keji;
		} else if ("财经".equals(type)) {
			return NewsType.caijing;
		} else if ("时尚".equals(type)) {
			return NewsType.shishang;
		} else {
			return NewsType.top;
		}
	}

	public static NewsType string2NewsType(String type) {
		if ("top".equals(type)) {
			return NewsType.top;
		} else if ("shehui".equals(type)) {
			return NewsType.shehui;
		} else if ("guonei".equals(type)) {
			return NewsType.guonei;
		} else if ("guoji".equals(type)) {
			return NewsType.guoji;
		} else if ("yule".equals(type)) {
			return NewsType.yule;
		} else if ("tiyu".equals(type)) {
			return NewsType.tiyu;
		} else if ("junshi".equals(type)) {
			return NewsType.junshi;
		} else if ("keji".equals(type)) {
			return NewsType.keji;
		} else if ("caijing".equals(type)) {
			return NewsType.caijing;
		} else if ("shishang".equals(type)) {
			return NewsType.shishang;
		} else {
			return NewsType.top;
		}
	}

	public enum NewsType {
		top// (头条，默认)
		, shehui// (社会)
		, guonei// (国内)
		, guoji// (国际)
		, yule// (娱乐)
		, tiyu// (体育)
		, junshi// (军事)
		, keji// (科技)
		, caijing// (财经)
		, shishang
		// (时尚)
	}

	public static String typeToUmType(String type) {
		if ("top".equals(type)) {
			return "click_news_type_top";
		} else if ("shehui".equals(type)) {
			return "click_news_type_shehui";
		} else if ("guonei".equals(type)) {
			return "click_news_type_guonei";
		} else if ("guoji".equals(type)) {
			return "click_news_type_guoji";
		} else if ("yule".equals(type)) {
			return "click_news_type_yule";
		} else if ("tiyu".equals(type)) {
			return "click_news_type_tiyu";
		} else if ("junshi".equals(type)) {
			return "click_news_type_junshi";
		} else if ("keji".equals(type)) {
			return "click_news_type_keji";
		} else if ("caijing".equals(type)) {
			return "click_news_type_caijing";
		} else if ("shishang".equals(type)) {
			return "click_news_type_shishang";
		} else {
			return "click_news_type_other";
		}
	}
}
