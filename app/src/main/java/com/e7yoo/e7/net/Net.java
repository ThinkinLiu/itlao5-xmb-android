package com.e7yoo.e7.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Net {
	public static final String DEF_CHATSET = "UTF-8";
	public static final int DEF_CONN_TIMEOUT = 5000;
	public static final int DEF_READ_TIMEOUT = 5000;
	public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

	/** 机器人 */
	public static final String APPKEY_ROBOT = "04ee52a93c5725d16bfeb688b10bf089";
	/** 笑话 */
	public static final String APPKEY_JOKE = "4ee798382c48aca4b432b33160c6659c";
	/** 新闻 */
	public static final String APPKEY_NEWS = "30b43e7f23b34dd13ba46c872913db2a";
	/** 天气 */
	public static final String APPKEY_WEATHER = "30b43e7f23b34dd13ba46c872913db2a";
	/** 电视 */
	public static final String APPKEY_TV = "ccb9229ac90acb10278161cc334c2e83";
	/** 星座 */
	public static final String APPKEY_CONSTELLATION = "157da320753e24fa8a5248ddca45c2fd";
	/** 历史上的今天 */
	public static final String APPKEY_TODAY_HISTORY = "83c9b89d2f44e0c282fedd945bcc3dc3";
	/** 微信精选 */
	public static final String APPKEY_WEIXIN_NEWS = "f833a7b593ca90e0fde7e145f6b1d3d0";
	

	/**
	 * 问答机器人
	 * 
	 * @param callback
	 * @param info
	 */
	public void robotAsk(NetCallback callback, String info, String userid) {
		String url = "http://op.juhe.cn/robot/index";// 请求接口地址
		Map params = new HashMap();// 请求参数
		params.put("key", APPKEY_ROBOT);// 您申请到的本接口专用的APPKEY
		params.put("info", info);// 要发送给机器人的内容，不要超过30个字符
		params.put("dtype", "json");// 返回的数据的格式，json或xml，默认为json
		params.put("loc", "");// 地点，如北京中关村
		params.put("lon", "");// 经度，东经116.234632（小数点后保留6位），需要写为116234632
		params.put("lat", "");// 纬度，北纬40.234632（小数点后保留6位），需要写为40234632
		params.put("userid", userid);// 1~32位，此userid针对您自己的每一个用户，用于上下文的关联
		doNet(callback, url, params);
	}

	/**
	 * 问答机器人 数据类型
	 *
	 * @param callback
	 */
	public void robotType(NetCallback callback) {
		String url = "http://op.juhe.cn/robot/code";// 请求接口地址
		Map params = new HashMap();// 请求参数
		params.put("dtype", "");// 返回的数据格式，json或xml，默认json
		params.put("key", APPKEY_ROBOT);// 您申请本接口的APPKEY，请在应用详细页查询
		doNet(callback, url, params);
	}

	/**
	 * 2.最新笑话
	 */
	public void jokeNew(NetCallback callback, int page, int pagesize) {
		//String url = "http://japi.juhe.cn/joke/content/text.from";// 请求接口地址
		String url = "http://v.juhe.cn/joke/content/text.php";
		Map params = new HashMap();// 请求参数
		params.put("page", page);// 当前页数,默认1
		params.put("pagesize", pagesize);// 每次返回条数,默认1,最大20
		params.put("key", APPKEY_JOKE);// 您申请的key
		doNet(callback, url, params);
	}

	/**
	 * 2.随机获取笑话or趣图
	 */
	public void jokeRand(NetCallback callback, boolean isPic) {
		String url = "http://v.juhe.cn/joke/randJoke.php";
		Map params = new HashMap();// 请求参数
		params.put("type", isPic ? "pic" : "");// 类型 pic 趣图， 其他 笑话
		params.put("key", APPKEY_JOKE);// 您申请的key
		doNet(callback, url, params);
	}

	/**
	 * 头条新闻
	 * 类型,,top(头条，默认),shehui(社会),guonei(国内),guoji(国际),yule(娱乐),tiyu(体育)junshi
	 * (军事),keji(科技),caijing(财经),shishang(时尚)
	 */
	public void newsList(NetCallback callback, String type) {
		String url = "http://v.juhe.cn/toutiao/index";// 请求接口地址
		Map params = new HashMap();// 请求参数
		params.put("type", type);// 类型
		params.put("key", APPKEY_NEWS);// 您申请的key
		doNet(callback, url, params);
	}

	/**
	 * 微信精选
	 * @param callback
	 * @param ps 每页多少条数据
	 * @param pno 第几页
	 */
	public void wxNewsList(NetCallback callback, int ps, int pno) {
		String url = "http://v.juhe.cn/weixin/query";// 请求接口地址
		Map params = new HashMap();// 请求参数
		params.put("ps", ps);// 类型
		params.put("pno", pno);// 类型
		params.put("key", APPKEY_WEIXIN_NEWS);// 您申请的key
		doNet(callback, url, params);
	}

	// 1.事件列表
	public void getToadyHistory(NetCallback callback) {
		String url = "http://api.juheapi.com/japi/toh";// 请求接口地址
		Map params = new HashMap();// 请求参数
		params.put("key", APPKEY_TODAY_HISTORY);// 应用APPKEY(应用详细页查询)
		params.put("v", "1.0");// 版本，当前：1.0
		Calendar c = Calendar.getInstance();
		params.put("month", c.get(Calendar.MONTH) + 1);// 月份，如：10
		params.put("day", c.get(Calendar.DATE));// 日，如：1
		doNet(callback, url, params);
	}

	// 2.根据ID查询事件详情
	public void getToadyHistoryDetails(NetCallback callback, String id) {
		String url = "http://api.juheapi.com/japi/tohdet";// 请求接口地址
		Map params = new HashMap();// 请求参数
		params.put("key", APPKEY_TODAY_HISTORY);// 应用APPKEY(应用详细页查询)
		params.put("v", "1.0");// 版本，当前：1.0
		params.put("id", id);// 事件ID
		doNet(callback, url, params);
	}

	private void doNet(NetCallback callback, String url, Map params, String... method) {
		JSONObject object = null;
		try {
			String strMethod;
			if(method == null || method.length != 1) {
				strMethod = "GET";
			} else {
				strMethod = "POST";
			}
			String result = net(url, params, strMethod);
			object = new JSONObject(result);
			if (object.getInt("error_code") == 0) {
				System.out.println(object.get("result"));
			} else {
				System.out.println(object.get("error_code") + ":" + object.get("reason"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		callback.callback(object);
	}
	
	/**
	 * 
	 * @param strUrl
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param method
	 *            请求方法
	 * @return 网络请求字符串
	 * @throws Exception
	 */
	public static String net(String strUrl, Map params, String method) throws Exception {
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		String rs = null;
		try {
			StringBuffer sb = new StringBuffer();
			if (method == null || method.equals("GET")) {
				strUrl = strUrl + "?" + urlencode(params);
			}
			URL url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			if (method == null || method.equals("GET")) {
				conn.setRequestMethod("GET");
			} else {
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
			}
			conn.setRequestProperty("User-agent", userAgent);
			conn.setUseCaches(false);
			conn.setConnectTimeout(DEF_CONN_TIMEOUT);
			conn.setReadTimeout(DEF_READ_TIMEOUT);
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			if (params != null && method.equals("POST")) {
				try {
					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
					out.writeBytes(urlencode(params));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			InputStream is = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sb.append(strRead);
			}
			rs = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return rs;
	}

	// 将map型转为请求参数型
	public static String urlencode(Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry i : data.entrySet()) {
			try {
				sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	private static ConnectivityManager cnmger;
	private static NetworkInfo networkInfo;

	public static boolean isNetWorkConnected(Context context) {
		if (cnmger == null) {
			cnmger = (ConnectivityManager) context.getApplicationContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		networkInfo = cnmger.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return true;
	}
	
	public static boolean isWifi(Context context) {
		if (cnmger == null) {
			cnmger = (ConnectivityManager) context.getApplicationContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		NetworkInfo ni = cnmger.getActiveNetworkInfo();
		if (ni != null && ni.getTypeName() != null && ni.getTypeName().toUpperCase(Locale.CHINA).equals("WIFI")) {
			/*
			 * ni.getTypeNmae()可能取值如下 WIFI，表示WIFI联网 MOBILE，表示GPRS、EGPRS
			 * 3G网络没有测试过 WIFI和(E)GPRS不能共存，如果两个都打开，系统仅支持WIFI
			 */
			return true;
		}
		return false;
	}
}
