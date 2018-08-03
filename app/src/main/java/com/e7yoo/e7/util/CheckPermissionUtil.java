package com.e7yoo.e7.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.e7yoo.e7.R;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Administrator on 2017/9/14.
 */

public class CheckPermissionUtil {

    /** 权限是否requestPermissions申请过 */
    public static final String SP_PERMISSION = "permission_";

    public static boolean checkPermission(Activity act, String permission, int requestCode, int titleResId, int hintResId) {
        try {
            if (ContextCompat.checkSelfPermission(act, permission) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (PreferenceUtil.getBoolean(SP_PERMISSION + permission, false) && !act.shouldShowRequestPermissionRationale(permission)) {
                        AskForPermission(act, titleResId, hintResId);
                        return false;
                    }
                }
                ActivityCompat.requestPermissions(act, new String[]{permission}, requestCode);
                PreferenceUtil.getBoolean(SP_PERMISSION + permission, true);
                return false;
            }
        } catch (Throwable e) {
        }
        return true;
    }

    public static void AskForInstallAppPermission(final Activity act) {
        final MaterialDialog materialDialog = new MaterialDialog(act);
        materialDialog.setTitle(R.string.dialog_install_hint_title)
                .setMessage(R.string.dialog_install_hint)
                .setPositiveButton(act.getString(R.string.goto_open), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                        requestPer(act);
                    }
                })
                .setNegativeButton(act.getString(R.string.goto_ignore), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                }).show();
    }

    private static void requestPer(Activity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean b = activity.getPackageManager().canRequestPackageInstalls();
            if(!b) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                activity.startActivityForResult(intent, 10086);
            }
        }
    }

    public static void AskForPermission(final Activity act, int titleResId, int hintResId) {
        final MaterialDialog materialDialog = new MaterialDialog(act);
        materialDialog.setTitle(titleResId)
                .setMessage(hintResId)
                .setPositiveButton(act.getString(R.string.goto_open), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                        openAppSettings(act);
                    }
                })
                .setNegativeButton(act.getString(R.string.goto_ignore), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                }).show();
    }

    public static void openAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getApplicationContext().getPackageName())); // 根据包名打开对应的设置界面
        context.startActivity(intent);
    }
}
