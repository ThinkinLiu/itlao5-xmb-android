package com.e7yoo.e7.app.light;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.e7yoo.e7.R;

import me.drakeet.materialdialog.MaterialDialog;

public class ShotCut {

	public static void createShortCut(Activity act){
		//创建快捷方式的Intent
		Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		//不允许重复创建
		shortcutintent.putExtra("duplicate", false);
		//需要现实的名称
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, act.getApplicationContext().getString(R.string.flashlight));
		//快捷图片
		Parcelable icon = Intent.ShortcutIconResource.fromContext(act.getApplicationContext(), R.mipmap.bg_led_off_widget);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		//点击快捷图片，运行的程序主入口
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, toFlashLight());
		//发送广播。OK
		act.sendBroadcast(shortcutintent);
	}
	
	private static Intent toFlashLight() {
		Intent intent = new Intent("com.e7yoo.e7.intent.action.CREATE_SHORTCUT");
		intent.putExtra(FlashLightActivity.INTENT_FROM, "shotcut");
		return intent;
	}
}
