package com.e7yoo.e7.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 获取设备指纹的工具类
 * 
 * @author HZJ
 * @DATE 2015-8-19
 */

public class FingerprintUtil {
	/**
	 * 获取设备指纹 如果从SharedPreferences文件中拿不到，那么重新生成一个， 并保存到SharedPreferences文件中。
	 * 
	 * @param context
	 * @return fingerprint 设备指纹
	 */
	/*
	 * public static String getFingerprint(Context context){ String fingerprint
	 * = null; fingerprint = readFingerprintFromFile();
	 * if(TextUtils.isEmpty(fingerprint)){ fingerprint =
	 * createFingerprint(context); } else{ LogUtil.i("从文件中获取设备指纹："+fingerprint);
	 * } return fingerprint; }
	 */

	/**
	 * 生成一个设备指纹（耗时一般在50毫秒以内）： 1.IMEI + 设备硬件信息 + ANDROID_ID 拼接成的字符串
	 * 2.用MessageDigest将以上字符串处理成32位的16进制字符串
	 * 
	 * @param context
	 * @return 设备指纹
	 */
	@SuppressLint("DefaultLocale")
	public static String createFingerprint(Context context) {

		// 1.IMEI
		TelephonyManager TelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = null;
		try
		{
			imei = TelephonyMgr.getDeviceId();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// 2.android 设备硬件信息 //小米note2上Build.CPU_ABI会在armeabi-v7a和arm64-v8a不同时变化
		final String hardwareInfo = Build.PRODUCT + Build.DEVICE + Build.BOARD
				+ Build.MANUFACTURER + Build.BRAND + Build.MODEL
				+ Build.HARDWARE + Build.FINGERPRINT;

		/*
		 * 3. Android_id 恢复出厂会变 A 64-bit number (as a hex string) that is
		 * randomly generated when the user first sets up the device and should
		 * remain constant for the lifetime of the user's device. The value may
		 * change if a factory reset is performed on the device.
		 */
		final String androidId = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);


		// Combined Device ID
		final String deviceId = imei + hardwareInfo + androidId;

		// 创建一个 messageDigest 实例
		MessageDigest msgDigest = null;
		try {
			msgDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		// 用 MessageDigest 将 deviceId 处理成32位的16进制字符串
		msgDigest.update(deviceId.getBytes(), 0, deviceId.length());
		// get md5 bytes
		byte md5ArrayData[] = msgDigest.digest();

		// create a hex string
		String deviceUniqueId = new String();
		for (int i = 0; i < md5ArrayData.length; i++) {
			int b = (0xFF & md5ArrayData[i]);
			// if it is a single digit, make sure it have 0 in front (proper
			// padding)
			if (b <= 0xF)
				deviceUniqueId += "0";
			deviceUniqueId += Integer.toHexString(b);
		} 
		deviceUniqueId = deviceUniqueId.toUpperCase();
		;
		return OsUtil.toMD5(deviceId);
	}

}
