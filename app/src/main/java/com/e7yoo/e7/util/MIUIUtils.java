package com.e7yoo.e7.util;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 小米MIUI系统功能类<br/>
 * Created by ZJUN on 2016/5/26.
 */
public class MIUIUtils {
    private static final String TAG = "MIUIUtils";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";

    /**
	 * 判断是小米手机是否需要小米手的通知栏而不是我们自定义的通知栏
	 */
	public static boolean isMIUIAndNotNeedNotify() {
		if (!Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
			// 不是小米手机，直接告诉外面还需要自己的通知栏
			return false;
		} else {
			if (!MIUIUtils.isMIUIV6Later()) {
				// 是小米手机，但是版本低，还需要自己的通知栏
				return false;
			}
		}
		return true;
	}
    
    /**
     * 判断系统是否为MIUI
     */
    public static boolean isMIUI() {
        String miuiVersion = getSystemProperty(KEY_MIUI_VERSION_NAME);
        return !TextUtils.isEmpty(miuiVersion);
    }

    /**
     * 判断系统是否为MIUI_V6或以后的版本(V6之前的版本不支持沉浸式状态栏)
     */
    public static boolean isMIUIV6Later() {
        String miuiVersion = getSystemProperty(KEY_MIUI_VERSION_NAME);
        if (!TextUtils.isEmpty(miuiVersion)) {
            if (miuiVersion.equals("V6") || miuiVersion.equals("V7") || miuiVersion.equals("V8")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取MIUI系统属性
     */
    private static String getSystemProperty(String propertyName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propertyName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read sysprop " + propertyName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    /**
     * 设置MIUI系统沉浸式状态栏
     */
    public static void setImmerseStatusBar(Activity activity, boolean darkMode) {
        Window window = activity.getWindow();
        Class<? extends Window> clazz = window.getClass();
        try {
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
            int tranceFlag = field.getInt(layoutParams); // 透明状态栏
            field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, darkMode ? tranceFlag | darkModeFlag : 0,
                    darkMode ? tranceFlag | darkModeFlag : darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
