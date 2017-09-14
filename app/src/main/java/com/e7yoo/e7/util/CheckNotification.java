package com.e7yoo.e7.util;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;

import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Administrator on 2017/9/13.
 */

public class CheckNotification {
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    /**
     * 检测是否有通知栏权限，没有则跳往设置页
     * @param context
     */
    public static void checkAndOpenNotification(final Context context) {
        if(!isNotificationEnabled(context)) {
            final MaterialDialog materialDialog = new MaterialDialog(context);
            materialDialog.setTitle(context.getString(R.string.dialog_notify_hint_title))
                    .setMessage(context.getString(R.string.dialog_notify_hint))
                    .setPositiveButton(context.getString(R.string.goto_open), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                            requestPermission(context);
                        }
                    })
                    .setNegativeButton(context.getString(R.string.goto_ignore), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                        }
                    }).show();
        }
    }

    /**
     * 用来判断是否开启通知权限（主要是Toast能否弹出的判断）
     */
    public static boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        } else {
            try {
                AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                ApplicationInfo appInfo = context.getApplicationInfo();
                String pkg = context.getApplicationContext().getPackageName();
                int uid = appInfo.uid;
                Class appOpsClass = null; /* Context.APP_OPS_MANAGER */
                appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public static void requestPermission(Context context) {

        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        context.startActivity(intent);
        /*// TODO Auto-generated method stub
        // 6.0以上系统才可以判断权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 进入设置系统应用权限界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 运行系统在5.x环境使用
            // 进入设置系统应用权限界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
            return;
        }
        return;*/
    }
}
