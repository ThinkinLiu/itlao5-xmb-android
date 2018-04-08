package com.e7yoo.e7.util;

import com.baidu.speech.core.ASREngine;

/**
 * Created by Administrator on 2018/4/8.
 */

public class MyIconUtil {

    public static String getMyIcon() {
        String myIcon = PreferenceUtil.getString(Constant.PREFERENCE_MY_ICON, null);
        if(myIcon != null) {
            String result = myIcon.trim();
            return result.length() > 5 ? result : null;
        }
        return null;
    }

    public static void setMyIcon(String myIcon) {
        PreferenceUtil.commitString(Constant.PREFERENCE_MY_ICON, myIcon);
    }
}
