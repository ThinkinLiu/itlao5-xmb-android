package com.e7yoo.e7.util;

import android.text.TextUtils;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.umeng.comm.core.beans.BaseBean;
import com.umeng.comm.core.beans.CommUser;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/9/6.
 */

public class BaseBeanUtil {
    public static final String WELCOME = "welcome";
    public static final String TEXT_MORE = "text_more";

    public static String getExtraString(BaseBean baseBean, String key) {
        if(baseBean == null || TextUtils.isEmpty(baseBean.customField)) {
            return "";
        }
        try {
            return new JSONObject(baseBean.customField).getString(key);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 注意customField长度限制为0-50；
     * @param baseBean
     * @param key
     * @param value
     */
    public static void setExtraString(BaseBean baseBean, String key, String value) {
        if(baseBean == null) {
            return;
        }
        JSONObject jo;
        try {
            jo = new JSONObject(baseBean.customField);
        } catch (Exception e) {
            e.printStackTrace();
            jo = new JSONObject();
        }
        try {
            jo.put(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        baseBean.customField = jo.toString();
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
