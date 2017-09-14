package com.e7yoo.e7.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.e7yoo.e7.AddRobotActivity;
import com.e7yoo.e7.ChatActivity;
import com.e7yoo.e7.InputActivity;
import com.e7yoo.e7.SexActivity;
import com.e7yoo.e7.model.Robot;

/**
 * Created by Administrator on 2017/8/31.
 */

public class ActivityUtil {

    public static void toChatActivity(Context context, Robot robot) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constant.INTENT_ROBOT, robot);
        context.startActivity(intent);
    }

    public static void toAddRobotActivityForResult(Activity activity, Robot robot, int requestCode) {
        Intent intent = new Intent(activity, AddRobotActivity.class);
        intent.putExtra(Constant.INTENT_ROBOT, robot);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void toInputActivityForResult(Activity activity, int titleResId, int maxLength, int minLength, String text, String hint, int requestCode) {
        Intent intent = new Intent(activity, InputActivity.class);
        intent.putExtra(Constant.INTENT_TITLE_RES_ID, titleResId);
        intent.putExtra(Constant.INTENT_MAX_LENGTH, maxLength);
        intent.putExtra(Constant.INTENT_MIN_LENGTH, minLength);
        intent.putExtra(Constant.INTENT_TEXT, text);
        intent.putExtra(Constant.INTENT_HINT, hint);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void toSexActivityForResult(Activity activity, int titleResId, int requestCode) {
        Intent intent = new Intent(activity, SexActivity.class);
        intent.putExtra(Constant.INTENT_TITLE_RES_ID, titleResId);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void toActivity(Activity activity, Class toActivity) {
        Intent intent = new Intent(activity, toActivity);
        activity.startActivity(intent);
    }

    public static void toActivityForResult(Activity activity, Class toActivity, int requestCode) {
        Intent intent = new Intent(activity, toActivity);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void toActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
    }
}
