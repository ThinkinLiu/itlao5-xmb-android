package com.e7yoo.e7.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.e7yoo.e7.service.E7Service;

/**
 * Created by andy on 2018/4/13.
 */

public class ServiceUtil {
    public static void startE7Service(Context context, String[] keys, String[] values
            , String[] keys_int, int[] values_int
            , String[] keys_long, long[] values_long
            , String[] keys_boolean, boolean[] values_boolean) {
        Intent startIntent = new Intent();
        ComponentName componentName = new ComponentName(
                "com.e7yoo.e7",
                "com.e7yoo.e7.service.E7Service");
        startIntent.setComponent(componentName);
        if(keys != null && keys.length > 0) {
            startIntent.putExtra("keys", keys);
            startIntent.putExtra("values",values);
        }
        if(keys_int != null && keys_int.length > 0) {
            startIntent.putExtra("keys_int", keys_int);
            startIntent.putExtra("values_int",values_int);
        }
        if(values_long != null && values_long.length > 0) {
            startIntent.putExtra("keys_long", keys_long);
            startIntent.putExtra("values_long",values_long);
        }
        if(values_boolean != null && values_boolean.length > 0) {
            startIntent.putExtra("keys_boolean", keys_boolean);
            startIntent.putExtra("values_boolean", values_boolean);
        }
        startIntent.putExtra(E7Service.FROM, E7Service.FROM_SMS_RECEIVER_PREFERENCE);
        context.startService(startIntent);
    }

    /**
     * 与startE7Service对应， 处理SharedPreference
     * @param bundle
     */
    public static void commitPreference(Bundle bundle) {
        if(bundle.containsKey("keys") && bundle.containsKey("values")) {
            String[] keys = bundle.getStringArray("keys");
            String[] values = bundle.getStringArray("values");
            if(keys.length == values.length) {
                for (int i = 0; i < keys.length; i++) {
                    PreferenceUtil.commitString(keys[i], values[i]);
                }
            }
        }
        if(bundle.containsKey("keys_int") && bundle.containsKey("values_int")) {
            String[] keys = bundle.getStringArray("keys_int");
            int[] values = bundle.getIntArray("values_int");
            if(keys.length == values.length) {
                for(int i = 0; i < keys.length; i++) {
                    PreferenceUtil.commitInt(keys[i], values[i]);
                }
            }
        }
        if(bundle.containsKey("keys_long") && bundle.containsKey("values_long")) {
            String[] keys = bundle.getStringArray("keys_long");
            long[] values = bundle.getLongArray("values_long");
            if(keys.length == values.length) {
                for (int i = 0; i < keys.length; i++) {
                    PreferenceUtil.commitLong(keys[i], values[i]);
                }
            }
        }
        if(bundle.containsKey("keys_boolean") && bundle.containsKey("values_boolean")) {
            String[] keys = bundle.getStringArray("keys_boolean");
            boolean[] values = bundle.getBooleanArray("values_boolean");
            if(keys.length == values.length) {
                for(int i = 0; i < keys.length; i++) {
                    PreferenceUtil.commitBoolean(keys[i], values[i]);
                }
            }
        }
    }
}
