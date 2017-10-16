package com.e7yoo.e7.util;

import android.os.Bundle;
import android.text.TextUtils;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.Robot;
import com.umeng.comm.core.beans.CommUser;

/**
 * Created by Administrator on 2017/9/6.
 */

public class CommUserUtil {

    public static String getExtraString(Bundle bundle, String key) {
        if(bundle == null || !bundle.containsKey(key)) {
            return "";
        }
        return bundle.getString(key);
    }

    public static void setExtraString(CommUser commUser, String key, String value) {
        if(commUser == null || commUser.extraData == null) {
            return;
        }
        commUser.extraData.putString(key, value);
    }

    public static String getString(String text) {
        if(text == null) {
            return "";
        }
        return text;
    }

    public static String getSexText(CommUser.Gender gender) {
        switch (gender) {
            case FEMALE:
                return E7App.mApp.getString(R.string.sex_female);
            case MALE:
            default:
                return E7App.mApp.getString(R.string.sex_male);
        }
    }

    public static CommUser.Gender getSex(String sex) {
        if(E7App.mApp.getString(R.string.sex_female).equals(sex)) {
            return CommUser.Gender.FEMALE;
        } else if(E7App.mApp.getString(R.string.sex_male).equals(sex)) {
            return CommUser.Gender.MALE;
        }
        return CommUser.Gender.MALE;
    }

    /*public static int getDefaultIconResId(CommUser commUser) {
        if(commUser != null && TextUtils.isEmpty(commUser.iconUrl)) {
            return commUser.iconUrl;
        } else {
            return R.mipmap.icon_me;
        }
    }*/
}
