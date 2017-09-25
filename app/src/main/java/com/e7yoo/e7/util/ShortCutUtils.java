package com.e7yoo.e7.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.R;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

public class ShortCutUtils {

	private static final String TAG = "AppShortCutUtil";

	// 默认圆角半径
	private static final int DEFAULT_CORNER_RADIUS_DIP = 8;
	// 默认边框宽度
	private static final int DEFAULT_STROKE_WIDTH_DIP = 2;
	// 边框的颜色
	private static final int DEFAULT_STROKE_COLOR = Color.WHITE;
	// 中间数字的颜色
	private static final int DEFAULT_NUM_COLOR = Color.parseColor("#CCFF0000");

	/***
	 * 
	 * 生成有数字的图片(没有边框)
	 * 
	 * @param context
	 * @param icon
	 *            图片
	 * @param isShowNum
	 *            是否要绘制数字
	 * @param num
	 *            数字字符串：整型数字 超过99，显示为"99+"
	 * @return
	 */
	public static Bitmap generatorNumIcon(Context context, Bitmap icon, boolean isShowNum, String num) {

		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		// 基准屏幕密度
		float baseDensity = 1.5f;// 240dpi
		float factor = dm.density / baseDensity;

		Log.e(TAG, "density:" + dm.density);
		Log.e(TAG, "dpi:" + dm.densityDpi);
		Log.e(TAG, "factor:" + factor);

		// 初始化画布
		int iconSize = (int) context.getResources().getDimension(android.R.dimen.app_icon_size);
		Bitmap numIcon = Bitmap.createBitmap(iconSize, iconSize, Config.ARGB_8888);
		Canvas canvas = new Canvas(numIcon);

		// 拷贝图片
		Paint iconPaint = new Paint();
		iconPaint.setDither(true);// 防抖动
		iconPaint.setFilterBitmap(true);// 用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
		Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		Rect dst = new Rect(0, 0, iconSize, iconSize);
		canvas.drawBitmap(icon, src, dst, iconPaint);

		if (isShowNum) {

			if (TextUtils.isEmpty(num)) {
				num = "0";
			}

			if (!TextUtils.isDigitsOnly(num)) {
				// 非数字
				Log.e(TAG, "the num is not digit :" + num);
				num = "0";
			}

			int numInt = Integer.valueOf(num);

			if (numInt > 99) {// 超过99

				num = "99+";

				// 启用抗锯齿和使用设备的文本字体大小
				Paint numPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
				numPaint.setColor(Color.WHITE);
				numPaint.setTextSize(20f * factor);
				numPaint.setTypeface(Typeface.DEFAULT_BOLD);
				int textWidth = (int) numPaint.measureText(num, 0, num.length());

				Log.e(TAG, "text width:" + textWidth);

				int circleCenter = (int) (15 * factor);// 中心坐标
				int circleRadius = (int) (13 * factor);// 圆的半径

				// 绘制左边的圆形
				Paint leftCirPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				leftCirPaint.setColor(Color.RED);
				canvas.drawCircle(iconSize - circleRadius - textWidth + (10 * factor), circleCenter, circleRadius,
						leftCirPaint);

				// 绘制右边的圆形
				Paint rightCirPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				rightCirPaint.setColor(Color.RED);
				canvas.drawCircle(iconSize - circleRadius, circleCenter, circleRadius, rightCirPaint);

				// 绘制中间的距形
				Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				rectPaint.setColor(Color.RED);
				RectF oval = new RectF(iconSize - circleRadius - textWidth + (10 * factor), 2 * factor,
						iconSize - circleRadius, circleRadius * 2 + 2 * factor);
				canvas.drawRect(oval, rectPaint);

				// 绘制数字
				canvas.drawText(num, (float) (iconSize - textWidth / 2 - (24 * factor)), 23 * factor, numPaint);

			} else {// <=99

				// 启用抗锯齿和使用设备的文本字体大小
				Paint numPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
				numPaint.setColor(Color.WHITE);
				numPaint.setTextSize(20f * factor);
				numPaint.setTypeface(Typeface.DEFAULT_BOLD);
				int textWidth = (int) numPaint.measureText(num, 0, num.length());

				Log.e(TAG, "text width:" + textWidth);

				// 绘制外面的圆形
				// Paint outCirPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				// outCirPaint.setColor(Color.WHITE);
				// canvas.drawCircle(iconSize - 15, 15, 15, outCirPaint);

				// 绘制内部的圆形
				Paint inCirPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				inCirPaint.setColor(Color.RED);
				canvas.drawCircle(iconSize - 15 * factor, 15 * factor, 15 * factor, inCirPaint);

				// 绘制数字
				canvas.drawText(num, (float) (iconSize - textWidth / 2 - 15 * factor), 22 * factor, numPaint);
			}
		}
		return numIcon;
	}

	/***
	 * 
	 * 生成有数字的图片(没有边框)
	 * 
	 * @param context
	 * @param icon
	 *            图片
	 * @param isShowNum
	 *            是否要绘制数字
	 * @param num
	 *            数字字符串：整型数字 超过99，显示为"99+"
	 * @return
	 */
	public static Bitmap generatorNumIcon2(Context context, Bitmap icon, boolean isShowNum, String num) {

		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		// 基准屏幕密度
		float baseDensity = 1.5f;// 240dpi
		float factor = dm.density / baseDensity;

		Log.e(TAG, "density:" + dm.density);
		Log.e(TAG, "dpi:" + dm.densityDpi);
		Log.e(TAG, "factor:" + factor);

		// 初始化画布
		int iconSize = (int) context.getResources().getDimension(android.R.dimen.app_icon_size);
		Bitmap numIcon = Bitmap.createBitmap(iconSize, iconSize, Config.ARGB_8888);
		Canvas canvas = new Canvas(numIcon);

		// 拷贝图片
		Paint iconPaint = new Paint();
		iconPaint.setDither(true);// 防抖动
		iconPaint.setFilterBitmap(true);// 用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
		Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		Rect dst = new Rect(0, 0, iconSize, iconSize);
		canvas.drawBitmap(icon, src, dst, iconPaint);

		if (isShowNum) {

			if (TextUtils.isEmpty(num)) {
				num = "0";
			}

			if (!TextUtils.isDigitsOnly(num)) {
				// 非数字
				Log.e(TAG, "the num is not digit :" + num);
				num = "0";
			}

			int numInt = Integer.valueOf(num);

			if (numInt > 99) {// 超过99
				num = "99+";
			}

			// 启用抗锯齿和使用设备的文本字体大小
			// 测量文本占用的宽度
			Paint numPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
			numPaint.setColor(Color.WHITE);
			numPaint.setTextSize(20f * factor);
			numPaint.setTypeface(Typeface.DEFAULT_BOLD);
			int textWidth = (int) numPaint.measureText(num, 0, num.length());
			Log.e(TAG, "text width:" + textWidth);

			/**
			 * ----------------------------------* TODO 绘制圆角矩形背景 start
			 * ------------------------------------
			 */
			// 圆角矩形背景的宽度
			int backgroundHeight = (int) (2 * 15 * factor);
			int backgroundWidth = textWidth > backgroundHeight ? (int) (textWidth + 10 * factor) : backgroundHeight;

			canvas.save();// 保存状态

			ShapeDrawable drawable = getDefaultBackground(context);
			drawable.setIntrinsicHeight(backgroundHeight);
			drawable.setIntrinsicWidth(backgroundWidth);
			drawable.setBounds(0, 0, backgroundWidth, backgroundHeight);
			canvas.translate(iconSize - backgroundWidth, 0);
			drawable.draw(canvas);

			canvas.restore();// 重置为之前保存的状态

			/**
			 * ----------------------------------* TODO 绘制圆角矩形背景 end
			 * ------------------------------------
			 */

			// 绘制数字
			canvas.drawText(num, (float) (iconSize - (backgroundWidth + textWidth) / 2), 22 * factor, numPaint);
		}
		return numIcon;
	}

	/***
	 * 
	 * 生成有数字的图片(有边框)
	 * 
	 * @param context
	 * @param icon
	 *            图片
	 * @param isShowNum
	 *            是否要绘制数字
	 * @param num
	 *            数字字符串：整型数字 超过99，显示为"99+"
	 * @return
	 */
	public static Bitmap generatorNumIcon3(Context context, Bitmap icon, boolean isShowNum, String num) {

		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		// 基准屏幕密度
		float baseDensity = 1.5f;// 240dpi
		float factor = dm.density / baseDensity;

		Log.e(TAG, "density:" + dm.density);
		Log.e(TAG, "dpi:" + dm.densityDpi);
		Log.e(TAG, "factor:" + factor);

		// 初始化画布
		int iconSize = (int) context.getResources().getDimension(android.R.dimen.app_icon_size);
		Bitmap numIcon = Bitmap.createBitmap(iconSize, iconSize, Config.ARGB_8888);
		Canvas canvas = new Canvas(numIcon);

		// 拷贝图片
		Paint iconPaint = new Paint();
		iconPaint.setDither(true);// 防抖动
		iconPaint.setFilterBitmap(true);// 用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
		Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		Rect dst = new Rect(0, 0, iconSize, iconSize);
		canvas.drawBitmap(icon, src, dst, iconPaint);

		if (isShowNum) {

			if (TextUtils.isEmpty(num)) {
				num = "0";
			}

			if (!TextUtils.isDigitsOnly(num)) {
				// 非数字
				Log.e(TAG, "the num is not digit :" + num);
				num = "0";
			}

			int numInt = Integer.valueOf(num);

			if (numInt > 99) {// 超过99
				num = "99+";
			}

			// 启用抗锯齿和使用设备的文本字体大小
			// 测量文本占用的宽度
			Paint numPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
			numPaint.setColor(Color.WHITE);
			numPaint.setTextSize(20f * factor);
			numPaint.setTypeface(Typeface.DEFAULT_BOLD);
			int textWidth = (int) numPaint.measureText(num, 0, num.length());
			Log.e(TAG, "text width:" + textWidth);

			/**
			 * ----------------------------------* TODO 绘制圆角矩形背景：先画边框，再画内部的圆角矩形
			 * start ------------------------------------
			 */
			// 圆角矩形背景的宽度
			int backgroundHeight = (int) (2 * 15 * factor);
			int backgroundWidth = textWidth > backgroundHeight ? (int) (textWidth + 10 * factor) : backgroundHeight;
			// 边框的宽度
			int strokeThickness = (int) (2 * factor);

			canvas.save();// 保存状态

			int strokeHeight = backgroundHeight + strokeThickness * 2;
			int strokeWidth = textWidth > strokeHeight ? (int) (textWidth + 10 * factor + 2 * strokeThickness)
					: strokeHeight;
			ShapeDrawable outStroke = getDefaultStrokeDrawable(context);
			outStroke.setIntrinsicHeight(strokeHeight);
			outStroke.setIntrinsicWidth(strokeWidth);
			outStroke.setBounds(0, 0, strokeWidth, strokeHeight);
			canvas.translate(iconSize - strokeWidth - strokeThickness, strokeThickness);
			outStroke.draw(canvas);

			canvas.restore();// 重置为之前保存的状态

			canvas.save();// 保存状态

			ShapeDrawable drawable = getDefaultBackground(context);
			drawable.setIntrinsicHeight((int) (backgroundHeight + 2 * factor));
			drawable.setIntrinsicWidth((int) (backgroundWidth + 2 * factor));
			drawable.setBounds(0, 0, backgroundWidth, backgroundHeight);
			canvas.translate(iconSize - backgroundWidth - 2 * strokeThickness, 2 * strokeThickness);
			drawable.draw(canvas);

			canvas.restore();// 重置为之前保存的状态

			/**
			 * ----------------------------------* TODO 绘制圆角矩形背景 end
			 * ------------------------------------
			 */

			// 绘制数字
			canvas.drawText(num, (float) (iconSize - (backgroundWidth + textWidth + 4 * strokeThickness) / 2),
					(22) * factor + 2 * strokeThickness, numPaint);
		}
		return numIcon;
	}

	/***
	 * 
	 * 生成有数字的图片(有边框的)
	 * 
	 * @param context
	 * @param icon
	 *            图片
	 * @param isShowNum
	 *            是否要绘制数字
	 * @param num
	 *            数字字符串：整型数字 超过99，显示为"99+"
	 * @return
	 */
	public static Bitmap generatorNumIcon4(Context context, Bitmap icon, boolean isShowNum, String num) {

		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		// 基准屏幕密度
		float baseDensity = 1.5f;// 240dpi
		float factor = dm.density / baseDensity;

		Log.e(TAG, "density:" + dm.density);
		Log.e(TAG, "dpi:" + dm.densityDpi);
		Log.e(TAG, "factor:" + factor);

		// 初始化画布
		int iconSize = (int) context.getResources().getDimension(android.R.dimen.app_icon_size);
		Bitmap numIcon = Bitmap.createBitmap(iconSize, iconSize, Config.ARGB_8888);
		Canvas canvas = new Canvas(numIcon);

		// 拷贝图片
		Paint iconPaint = new Paint();
		iconPaint.setDither(true);// 防抖处理
		iconPaint.setFilterBitmap(true);// 用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
		Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		Rect dst = new Rect(0, 0, iconSize, iconSize);
		canvas.drawBitmap(icon, src, dst, iconPaint);

		if (isShowNum) {

			if (TextUtils.isEmpty(num)) {
				num = "0";
			}

			if (!TextUtils.isDigitsOnly(num)) {
				// 非数字
				Log.e(TAG, "the num is not digit :" + num);
				num = "0";
			}

			int numInt = Integer.valueOf(num);

			if (numInt > 99) {// 超过99
				num = "99+";
			}

			// 启用抗锯齿和使用设备的文本字体
			// 测量文本占用的宽度
			Paint numPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
			numPaint.setColor(Color.WHITE);
			numPaint.setTextSize(25f * factor);
			numPaint.setTypeface(Typeface.DEFAULT_BOLD);
			int textWidth = (int) numPaint.measureText(num, 0, num.length());
			Log.e(TAG, "text width:" + textWidth);

			/**
			 * ----------------------------------* TODO 绘制圆角矩形背景 start
			 * ------------------------------------
			 */
			// 边框的宽度
			int strokeThickness = (int) (DEFAULT_STROKE_WIDTH_DIP * factor);
			// 圆角矩形背景的宽度
			float radiusPx = 15 * factor;
			int backgroundHeight = (int) (2 * (radiusPx + strokeThickness));// 2*(半径+边框宽度)
			int backgroundWidth = textWidth > backgroundHeight ? (int) (textWidth + 10 * factor + 2 * strokeThickness)
					: backgroundHeight;

			canvas.save();// 保存状态

			ShapeDrawable drawable = getDefaultBackground2(context);
			drawable.setIntrinsicHeight(backgroundHeight);
			drawable.setIntrinsicWidth(backgroundWidth);
			drawable.setBounds(0, 0, backgroundWidth, backgroundHeight);
			canvas.translate(iconSize - backgroundWidth - strokeThickness, 2 * strokeThickness);
			drawable.draw(canvas);

			canvas.restore();// 重置为之前保存的状态

			/**
			 * ----------------------------------* TODO 绘制圆角矩形背景 end
			 * ------------------------------------
			 */

			// 绘制数字
			canvas.drawText(num, (float) (iconSize - (backgroundWidth + textWidth + 2 * strokeThickness) / 2),
					(float) (25 * factor + 2.5 * strokeThickness), numPaint);
		}
		return numIcon;
	}

	/***
	 * 是否已经创建了快捷方式
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isAddShortCut(Context context) {

		Log.i(TAG, "isAddShortCut....");

		boolean isInstallShortcut = false;

		final ContentResolver cr = context.getContentResolver();

		String AUTHORITY = getAuthorityFromPermission(context, "com.android.launcher.permission.READ_SETTINGS");

		Log.i(TAG, "AUTHORITY  :  " + AUTHORITY);

		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");

		Cursor c = cr.query(CONTENT_URI, new String[] { "title" }, "title=?",
				new String[] { context.getString(R.string.app_name) }, null);

		if (c != null && c.getCount() > 0) {
			isInstallShortcut = true;
		}

		if (c != null) {
			c.close();
		}

		Log.i(TAG, "isAddShortCut....isInstallShortcut=" + isInstallShortcut);

		return isInstallShortcut;
	}

	/***
	 * 取得权限相应的认证URI
	 * 
	 * @param context
	 * @param permission
	 * @return
	 */
	public static String getAuthorityFromPermission(Context context, String permission) {
		if (TextUtils.isEmpty(permission)) {
			return null;
		}
		List<PackageInfo> packInfos = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
		if (packInfos == null) {
			return null;
		}
		for (PackageInfo info : packInfos) {
			ProviderInfo[] providers = info.providers;
			if (providers != null) {
				for (ProviderInfo provider : providers) {
					if (permission.equals(provider.readPermission) || permission.equals(provider.writePermission)) {
						return provider.authority;
					}
				}
			}
		}
		return null;
	}

	public static boolean isXiaomiUI6() {
		boolean bIs = true;

		try {
			Class<?> miuiNotificationClass = Class.forName("android.app.MiuiNotification");

			Object miuiNotification = miuiNotificationClass.newInstance();

			if (miuiNotification == null) {
				bIs = false;
			}
		} catch (Exception e) {
			bIs = false;
		}

		return bIs;
	}

	/***
	 * 在小米应用图标的快捷方式上加数字<br>
	 * 
	 * 
	 * @param context
	 * @param num
	 *            显示的数字：大于99，为"99"，当为""时，不显示数字，相当于隐藏了)<br>
	 *            <br>
	 * 
	 *            注意点： context.getPackageName()+"/."+clazz.getSimpleName()
	 *            （这个是启动activity的路径）中的"/."不能缺少
	 * 
	 */
	public static void xiaoMiShortCut(Context context, Class<?> clazz, String num) {
		Log.e(TAG, "xiaoMiShortCut....");

		// if(ShortCutUtils.isXiaomiUI6()) return;
		if (!MIUIUtils.isMIUIV6Later())
			return;

		if (TextUtils.isEmpty(num) || num.equals("0")) {
			num = "";
		} else {

			int numInt = Integer.valueOf(num);

			if (numInt < 1) {
				num = "";
			}
			if (numInt > 99) {

				num = "99";

			} else {
				// 不处理

			}
		}

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		if (TextUtils.isEmpty(num)) {

			nm.cancel(101010);

			return;
		}

		Notification notification = null;

		boolean isMiUIV6 = true;

		try {

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

			String str = "您有" + num + "条新消息";
			builder.setContentTitle(str);

			// APP的名称
			PackageManager packageManager = context.getPackageManager();
			String appname = (String) packageManager.getApplicationLabel(context.getApplicationInfo());
			String notifyText = appname + "提醒：您收到一条新消息";
			builder.setTicker(notifyText);

			builder.setAutoCancel(true);

			builder.setSmallIcon(R.mipmap.logo);

			builder.setDefaults(Notification.DEFAULT_LIGHTS);
			
			/** 设置点击通知栏跳转事件 **/
			// 这里采用直接跳转到主界面，不要跳转到具体的聊天页面
			Intent msgIntent = new Intent(context, MainActivity.class);
			msgIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 101010, msgIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(pendingIntent);

			notification = builder.build();

			Class<?> miuiNotificationClass = Class.forName("android.app.MiuiNotification");
			Object miuiNotification = miuiNotificationClass.newInstance();
			Field field = miuiNotification.getClass().getDeclaredField("messageCount");
			field.setAccessible(true);

			int nNumber = 0;

			if (num.length() != 0) {
				nNumber = Integer.valueOf(num);
			}

			field.set(miuiNotification, nNumber);// 设置信息数-->这种发送必须是miui 6才行

			field = notification.getClass().getField("extraNotification");

			field.setAccessible(true);

			field.set(notification, miuiNotification);

		} catch (Exception e) {

			isMiUIV6 = false;

			Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");

			String pkName = context.getPackageName() + "/." + clazz.getSimpleName();

			localIntent.putExtra("android.intent.extra.update_application_component_name", pkName);

			localIntent.putExtra("android.intent.extra.update_application_message_text", num);

			context.sendBroadcast(localIntent);

		} finally {

			if (notification != null && isMiUIV6) {

				// miui6以上版本需要使用通知发送

				nm.notify(101010, notification);

			}
		}

	}

	/***
	 * 索尼手机：应用图标的快捷方式上加数字
	 * 
	 * @param context
	 * @param num
	 */
	public static void sonyShortCut(Context context, String num, boolean isShow) {

		String activityName = getLaunchActivityName(context);

		if (activityName == null) {
			return;
		}

		Intent intent = new Intent();

		int numInt = Integer.valueOf(num);

		if (numInt < 1) {
			num = "";
			isShow = false;
		} else if (numInt > 99) {
			num = "99";
		}

		intent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");

		intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);

		intent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", activityName);

		intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", num);

		intent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());

		context.sendBroadcast(intent);

	}

	/***
	 * 三星手机：应用图标的快捷方式上加数字
	 * 
	 * @param context
	 * @param num
	 */
	public static void samsungShortCut(Context context, int num) {

		if (num < 1) {
			num = 0;
		} else if (num > 99) {
			num = 99;
		}

		String activityName = getLaunchActivityName(context);

		Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");

		intent.putExtra("badge_count", num);

		intent.putExtra("badge_count_package_name", context.getPackageName());

		intent.putExtra("badge_count_class_name", activityName);

		context.sendBroadcast(intent);

	}

	public static void htcShortCut(Context context, Class<?> clazz, int number) {
		if (number < 1) {
			number = 0;
		} else if (number > 99) {
			number = 99;
		}

		String packageName = context.getPackageName() + "/." + clazz.getSimpleName();

		Intent updateIntent = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");

		updateIntent.putExtra("packagename", packageName);

		updateIntent.putExtra("count", number);

		context.sendBroadcast(updateIntent);

		Intent setNotificationIntent = new Intent("com.htc.launcher.action.SET_NOTIFICATION");

		ComponentName localComponentName = new ComponentName(packageName, clazz.getSimpleName());

		setNotificationIntent.putExtra("com.htc.launcher.extra.COMPONENT", localComponentName.flattenToShortString());

		setNotificationIntent.putExtra("com.htc.launcher.extra.COUNT", number);

		context.sendBroadcast(setNotificationIntent);

	}

	/**
	 * 华为手机红点
	 */
	private static void huaWeiShortCut(Context context, Class<?> clazz, int number) {
		if (number < 1) {
			number = 0;
		} else if (number > 99) {
			number = 99;
		}

		// 检测华为该版本是否支持桌面角标
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo("com.huawei.android.launcher", 0);
			if (info.versionCode < 63029) {
				// 小于63029的不能使用
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 异常也直接抛出
			return;
		}

		try {
			Bundle extra = new Bundle();

			String packageName = context.getPackageName();

			// 获取当前类名
			String className = getLaunchActivityName(context);

			extra.putString("package", packageName);

			extra.putString("class", className);

			extra.putInt("badgenumber", number);

			context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"),
					"change_launcher_badge", null, extra);

		} catch (Exception e) {
			e.printStackTrace();
			// 异常也直接抛出
			return;
		}
	}

	/**
	 * 删除快捷方式
	 * 
	 * @param context
	 * @param clazz
	 */
	public static void deleteShortCut(Context context, Class<?> clazz) {

		Log.i(TAG, "delShortcut....");

		if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
			// 小米
			// 当为""时，不显示数字，相当于隐藏了)
			xiaoMiShortCut(context, clazz, "");

		} else if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
			// 三星
			samsungShortCut(context, 0);

		} else if (Build.MANUFACTURER.equalsIgnoreCase("htc")) {
			htcShortCut(context, clazz, 0);

		} else if (Build.MANUFACTURER.equalsIgnoreCase("sony")) {
			sonyShortCut(context, "0", false);

		} else if (Build.MANUFACTURER.equalsIgnoreCase("huawei")) {
			// 华为(暂时不能用)
			// huaWeiShortCut(context, clazz, 0);

		} else {
			// 其他原生系统手机
			// 删除显示数字的快捷方式
			// deleteRawShortCut(context, clazz);

		}
	}

	/***
	 * 在应用图标的快捷方式上加数字
	 * 
	 * @param clazz
	 *            启动的activity
	 * @param isShowNum
	 *            是否显示数字
	 * @param num
	 *            显示的数字：整型
	 * 
	 */
	public static void addNumShortCut(Context context, Class<?> clazz, boolean isShowNum, String num) {

		Log.e(TAG, "manufacturer=" + Build.MANUFACTURER + ",num=" + num);

		if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
			// 小米
			xiaoMiShortCut(context, clazz, num);

		} else if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
			// 三星
			samsungShortCut(context, Integer.valueOf(num));

		} else if (Build.MANUFACTURER.equalsIgnoreCase("htc")) {
			// htc
			htcShortCut(context, clazz, Integer.valueOf(num));

		} else if (Build.MANUFACTURER.equalsIgnoreCase("sony")) {
			// 索尼
			sonyShortCut(context, num, true);

		} else if (Build.MANUFACTURER.equalsIgnoreCase("huawei")) {
			// 华为(暂时不能用)
			// huaWeiShortCut(context, clazz, Integer.valueOf(num));

		} else {
			// 其他原生系统手机

		}

	}

	/***
	 * 取得当前应用的启动activity的名称： mainfest.xml中配置的 android:name:"
	 * 
	 * @param context
	 * @return
	 */
	public static String getLaunchActivityName(Context context) {
		PackageManager localPackageManager = context.getPackageManager();
		Intent localIntent = new Intent("android.intent.action.MAIN");
		localIntent.addCategory("android.intent.category.LAUNCHER");
		try {
			Iterator<ResolveInfo> localIterator = localPackageManager.queryIntentActivities(localIntent, 0).iterator();
			while (localIterator.hasNext()) {
				ResolveInfo localResolveInfo = localIterator.next();
				if (!localResolveInfo.activityInfo.applicationInfo.packageName
						.equalsIgnoreCase(context.getPackageName()))
					continue;
				String str = localResolveInfo.activityInfo.name;
				return str;
			}
		} catch (Exception localException) {
			return null;
		}
		return null;
	}

	/***
	 * 得到一个默认的背景：圆角矩形<br>
	 * <br>
	 * 使用代码来生成一个背景：相当于用<shape>的xml的背景
	 * 
	 * @return
	 */
	private static ShapeDrawable getDefaultBackground(Context context) {

		// 这个是为了应对不同分辨率的手机，屏幕兼容性
		int r = dipToPixels(context, DEFAULT_CORNER_RADIUS_DIP);
		float[] outerR = new float[] { r, r, r, r, r, r, r, r };

		// 圆角矩形
		RoundRectShape rr = new RoundRectShape(outerR, null, null);
		ShapeDrawable drawable = new ShapeDrawable(rr);
		drawable.getPaint().setColor(DEFAULT_NUM_COLOR);// 设置颜色
		return drawable;

	}

	/***
	 * 得到一个默认的背景：圆角矩形<br>
	 * <br>
	 * 使用代码来生成一个背景：相当于用<shape>的xml的背景
	 * 
	 * @return
	 */
	private static ShapeDrawable getDefaultBackground2(Context context) {

		// 这个是为了应对不同分辨率的手机，屏幕兼容性
		int r = dipToPixels(context, DEFAULT_CORNER_RADIUS_DIP);
		float[] outerR = new float[] { r, r, r, r, r, r, r, r };
		int distance = dipToPixels(context, DEFAULT_STROKE_WIDTH_DIP);

		// 圆角矩形
		RoundRectShape rr = new RoundRectShape(outerR, null, null);
		CustomBorderDrawable drawable = new CustomBorderDrawable(context, rr);
		drawable.getFillpaint().setColor(DEFAULT_NUM_COLOR);// 设置填充颜色
		drawable.getStrokepaint().setColor(DEFAULT_STROKE_COLOR);// 设置边框颜色
		drawable.getStrokepaint().setStrokeWidth(distance);// 设置边框宽度
		return drawable;

	}

	/***
	 * 得到一个默认的背景：圆角矩形<br>
	 * <br>
	 * 使用代码来生成一个背景：相当于用<shape>的xml的背景
	 * 
	 * @return
	 */
	private static ShapeDrawable getDefaultStrokeDrawable(Context context) {

		// 这个是为了应对不同分辨率的手机，屏幕兼容性
		int r = dipToPixels(context, DEFAULT_CORNER_RADIUS_DIP);
		int distance = dipToPixels(context, DEFAULT_STROKE_WIDTH_DIP);
		float[] outerR = new float[] { r, r, r, r, r, r, r, r };

		// 圆角矩形
		RoundRectShape rr = new RoundRectShape(outerR, null, null);
		ShapeDrawable drawable = new ShapeDrawable(rr);
		drawable.getPaint().setStrokeWidth(distance);
		drawable.getPaint().setStyle(Paint.Style.FILL);
		drawable.getPaint().setColor(DEFAULT_STROKE_COLOR);// 设置颜色
		return drawable;
	}

	/***
	 * dp to px
	 * 
	 * @param dip
	 * @return
	 */
	public static int dipToPixels(Context context, int dip) {
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
		return (int) px;
	}

}
