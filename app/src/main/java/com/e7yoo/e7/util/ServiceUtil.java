package com.e7yoo.e7.util;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import com.e7yoo.e7.R;
import com.e7yoo.e7.service.E7Service;

/**
 * Created by andy on 2018/4/13.
 */

public class ServiceUtil {
    public static final int E7ServiceNotifyId = 101;
    public static final int JpushServiceNotifyId = 102;

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
        // context.startService(startIntent);
        ServiceUtil.startService(context, startIntent);
    }

    /**
     * 与startE7Service对应， 处理SharedPreference
     * @param bundle
     */
    public static void commitPreference(Bundle bundle) {
        if(bundle.containsKey("keys") && bundle.containsKey("values")) {
            String[] keys = bundle.getStringArray("keys");
            String[] values = bundle.getStringArray("values");
            if(keys != null && values != null && keys.length == values.length) {
                for (int i = 0; i < keys.length; i++) {
                    PreferenceUtil.commitString(keys[i], values[i]);
                }
            }
        }
        if(bundle.containsKey("keys_int") && bundle.containsKey("values_int")) {
            String[] keys = bundle.getStringArray("keys_int");
            int[] values = bundle.getIntArray("values_int");
            if(keys != null && values != null && keys.length == values.length) {
                for(int i = 0; i < keys.length; i++) {
                    PreferenceUtil.commitInt(keys[i], values[i]);
                }
            }
        }
        if(bundle.containsKey("keys_long") && bundle.containsKey("values_long")) {
            String[] keys = bundle.getStringArray("keys_long");
            long[] values = bundle.getLongArray("values_long");
            if(keys != null && values != null && keys.length == values.length) {
                for (int i = 0; i < keys.length; i++) {
                    PreferenceUtil.commitLong(keys[i], values[i]);
                }
            }
        }
        if(bundle.containsKey("keys_boolean") && bundle.containsKey("values_boolean")) {
            String[] keys = bundle.getStringArray("keys_boolean");
            boolean[] values = bundle.getBooleanArray("values_boolean");
            if(keys != null && values != null && keys.length == values.length) {
                for(int i = 0; i < keys.length; i++) {
                    PreferenceUtil.commitBoolean(keys[i], values[i]);
                }
            }
        }
    }

    public static void startService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void startForeground(Service service, int id, Context context, int textResId, int titleResId, int smallIconResId, int largeIconResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            service.startForeground(id, getNotification(context, textResId, titleResId, smallIconResId, largeIconResId)); //这个id不要和应用内的其他同志id一样，不行就写 int.maxValue()        //context.startForeground(SERVICE_ID, builder.getNotification());
        }
    }


    private static Notification getNotification(Context context, int textResId, int titleResId, int smallIconResId, int largeIconResId){
        Notification.Builder mBuilder = new Notification.Builder(context);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mBuilder.setShowWhen(false);
        }
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setAutoCancel(false);
        if(textResId == 0) {
            textResId = R.string.notify_findphone;
        }
        if(titleResId == 0) {
            titleResId = R.string.app_name;
        }
        if(smallIconResId == 0) {
            smallIconResId = R.mipmap.logo_round;
        }
        if(largeIconResId == 0) {
            largeIconResId = R.mipmap.logo;
        }
        mBuilder.setSmallIcon(smallIconResId);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIconResId));
        mBuilder.setContentText(context.getString(textResId));
        mBuilder.setContentTitle(context.getString(titleResId));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return mBuilder.build();
        } else {
            return mBuilder.getNotification();
        }
    }

}
