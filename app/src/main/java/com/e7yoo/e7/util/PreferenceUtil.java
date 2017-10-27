package com.e7yoo.e7.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import com.e7yoo.e7.E7App;

import java.util.ArrayList;
import java.util.Set;

public class PreferenceUtil {
    private static SharedPreferences mSharedPreferences = null;
    private static Editor mEditor = null;

    public static void init(Context context) {
        if (null == mSharedPreferences) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static void removeKey(String key) {
        init(E7App.mApp);
        mEditor = mSharedPreferences.edit();
        mEditor.remove(key);
        mEditor.commit();
    }

    public static void removeAll() {
        init(E7App.mApp);
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.commit();
    }

    public static void commitStringSet(String key, Set<String> value) {
        init(E7App.mApp);
        mEditor = mSharedPreferences.edit();
        mEditor.putStringSet(key, value);
        mEditor.commit();
    }

    public static Set<String> getStringSet(String key, Set<String> faillValue) {
        init(E7App.mApp);
        return mSharedPreferences.getStringSet(key, faillValue);
    }

    public static void commitString(String key, String value) {
        init(E7App.mApp);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public static String getString(String key, String faillValue) {
        init(E7App.mApp);
        return mSharedPreferences.getString(key, faillValue);
    }

    public static void commitInt(String key, int value) {
        init(E7App.mApp);
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public static int getInt(String key, int failValue) {
        init(E7App.mApp);
        return mSharedPreferences.getInt(key, failValue);
    }

    public static void commitLong(String key, long value) {
        init(E7App.mApp);
        mEditor = mSharedPreferences.edit();
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    public static long getLong(String key, long failValue) {
        init(E7App.mApp);
        return mSharedPreferences.getLong(key, failValue);
    }

    public static void commitBoolean(String key, boolean value) {
        init(E7App.mApp);
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public static Boolean getBoolean(String key, boolean failValue) {
        init(E7App.mApp);
        return mSharedPreferences.getBoolean(key, failValue);
    }

}
