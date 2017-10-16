package com.e7yoo.e7.util;

import android.os.Bundle;
import android.text.TextUtils;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.Robot;
import com.umeng.comm.core.beans.CommUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/9/6.
 */

public class CommUserUtil {

    public static String getExtraString(CommUser commUser, String key) {
        if(commUser == null || TextUtils.isEmpty(commUser.customField)) {
            return "";
        }
        try {
            return new JSONObject(commUser.customField).getString(key);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setExtraString(CommUser commUser, String key, String value) {
        if(commUser == null) {
            return;
        }
        if(TextUtils.isEmpty(commUser.customField)) {
            commUser.extraData = new Bundle();
        }
        JSONObject jo;
        try {
            jo = new JSONObject(commUser.customField);
        } catch (Exception e) {
            e.printStackTrace();
            jo = new JSONObject();
        }
        try {
            jo.put(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        commUser.customField = jo.toString();
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
