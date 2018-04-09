package com.e7yoo.e7.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.e7yoo.e7.R;
import com.tencent.bugly.crashreport.CrashReport;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class OsUtil {
    public static final String PREFERENCE_DEVICE_UDID = "preference_device_udid";

    /**
     * @param context
     * @return
     * @see {@link #getUnSavedUdid(Context)}
     */
    public static String getUdid(Context context) {
        PreferenceUtil.init(context);
        String udid = PreferenceUtil.getString(PREFERENCE_DEVICE_UDID, null);
        if (TextUtils.isEmpty(udid)) {
            udid = getUnSavedUdid(context);
            PreferenceUtil.commitString(PREFERENCE_DEVICE_UDID, udid);
        }
        return udid;
    }

    /**
     * get udid</BR> imei -> androidId -> mac address -> uuid
     *
     * @param context
     * @return
     */
    private static String getUnSavedUdid(Context context) {
        return FingerprintUtil.createFingerprint(context);
    }

    public static String toMD5(String encTarget) {
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
            CrashReport.postCatchedException(e);
            return encTarget;
        }
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        /**
         * 会将开始的0忽略，生成的字符串长度<=32
         */
        byte b[] = mdEnc.digest();
        int i;
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }

    public static int getCurrentSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * get application version name writed in the manifest
     *
     * @param context
     * @return
     */
    public static int getAppVersion(Context context) {
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (NameNotFoundException e) {
            Log.e("VersionInfo", "Exception", e);
            CrashReport.postCatchedException(e);
        }
        return versionCode;
    }

    /**
     * get application version name writed in the manifest
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (NameNotFoundException e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public static Intent getAndroidShareIntent(CharSequence chooseTitle,
                                               CharSequence subject, CharSequence content) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        return Intent.createChooser(shareIntent, chooseTitle);
    }

    public static Intent getAndroidImageShareIntent(CharSequence chooseTitle,
                                                    String pathfile) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(pathfile));
        return Intent.createChooser(share, chooseTitle);
    }

    public static String getAppName(Context context) {
        String versionName = context.getString(R.string.app_name);
        return versionName;
    }

}

