package com.e7yoo.e7.util;

import android.content.Context;

import com.e7yoo.e7.AddRobotActivity;
import com.e7yoo.e7.MainActivity;
import com.sdsmdg.tastytoast.TastyToast;

/**
 * Created by Administrator on 2017/9/4.
 */

public class TastyToastUtil {
    public static void toast(Context context, int stringId, Object... formatArgs) {
        TastyToast.makeText(context, context.getString(stringId, formatArgs), TastyToast.LENGTH_SHORT, TastyToast.INFO);
    }


}
