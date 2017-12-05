package com.e7yoo.e7.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;


public class CommonUtil {
	
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
	public static boolean isEmptyTrim(String str) {
		return isEmpty(str) || str.trim().length() == 0;
	}
	
	public static boolean isEmptyTrimNull(String str) {
		return isEmptyTrim(str) || str.trim().equalsIgnoreCase("NULL");
	}
	
	/** 获取屏幕的宽度 */
	public final static int getWindowsWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/** 获取屏幕的宽度 */
	public final static int[] getWindowsWidthHeight(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return new int[]{dm.widthPixels, dm.heightPixels};
	}

	/** 获取topBar高度 */
	public final static int getStatusBarHeight(Activity activity) {
		int statusBarHeight = -1;
	 	try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");  
            Object object = clazz.newInstance();  
            int height = Integer.parseInt(clazz.getField("status_bar_height")  
	                    .get(object).toString());  
            statusBarHeight = activity.getResources().getDimensionPixelSize(height);
        } catch (Throwable e) {
			// TODO: 2017/9/12
			// CrashReport.postCatchedException(e);
		}
        if(statusBarHeight <= 0) {
			try {
				//获取status_bar_height资源的ID
				int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
				if (resourceId > 0) {
					//根据资源ID获取响应的尺寸值
					statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
				}
			} catch (Throwable e) {
				// TODO: 2017/9/12
				// CrashReport.postCatchedException(e);
			}
		}
		if(statusBarHeight <= 0) {
			try {
				Rect rectangle= new Rect();
				activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
				statusBarHeight = rectangle.top;
			} catch (Throwable e) {
				// TODO: 2017/9/12
				// CrashReport.postCatchedException(e);
			}
		}
		if(statusBarHeight <= 0) {
			try {
				statusBarHeight = activity.getResources().getDimensionPixelSize(R.dimen.space_5x);
			} catch (Throwable e) {
				// TODO: 2017/9/12
				// CrashReport.postCatchedException(e);
			}
		}
        return statusBarHeight;
	}

	public static Spanned getHtmlStr(String str) {
		str = str.replace("    ","&nbsp;&nbsp;&nbsp;&nbsp;").replace("\r\n","<br />").replace("\n","<br />");
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			return Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT);
		} else {
			return (Html.fromHtml(str));
		}

	}

	public static boolean isChannel(Context context, String... channels) {
		if(channels == null || channels.length <= 0) {
			return false;
		}
		ApplicationInfo appInfo = null;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		if(appInfo != null) {
			String msg = appInfo.metaData.getString("UMENG_CHANNEL").toLowerCase();
			for(String str : channels) {
				if (str != null && str.equals(msg)) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getUrlString(String font, String text) {
		return E7App.mApp.getString(R.string.blue_feed_content_url, font, text);
	}
}
