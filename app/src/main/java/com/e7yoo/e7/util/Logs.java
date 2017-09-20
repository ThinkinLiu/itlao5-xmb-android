package com.e7yoo.e7.util;

import android.util.Log;

/**
 * Created by Administrator on 2017/8/28.
 */

public class Logs {
    private static boolean mDebug = false;
    private static final String TAG = "e7yoo";

    public static void setDebug(boolean debug) {
        mDebug = debug;
    }

    public static boolean isDebug() {
        return mDebug;
    }

    public static void logE(String error, Throwable e, String... tags) {
        if(mDebug)
            Log.e(getTag(tags), error, e);
    }

    public static void logE(Throwable e, String... tags) {
        if(mDebug)
            Log.wtf(getTag(tags), e);
    }

    public static void logE(String error, String... tags) {
        if(mDebug)
            Log.e(getTag(tags), error);
    }

    public static void logI(String info, String... tags) {
        if(mDebug)
            Log.i(getTag(tags), info);
    }

    private static String getTag(String... tags) {
        if(tags != null && tags.length > 0) {
            return tags[0];
        }
        return TAG;
    }
}
