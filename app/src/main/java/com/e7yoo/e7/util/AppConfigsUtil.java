package com.e7yoo.e7.util;

import com.e7yoo.e7.model.AppConfigs;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AppConfigsUtil {
    private static final String APP_CONFIG_SHARE_URL = "app_config_share_url";

    public static void queryConfigs() {
        new BmobQuery<AppConfigs>().findObjects(new FindListener<AppConfigs>() {
            @Override
            public void done(List<AppConfigs> list, BmobException e) {
                if(e==null){
                    if(list != null && list.size() > 0) {
                        setShareUrl(list.get(0).getShareUrl());
                    }
                }
            }
        });
    }

    public static void setShareUrl(String shareUrl) {
        if(shareUrl == null || shareUrl.trim().length() == 0) {
            PreferenceUtil.removeKey(APP_CONFIG_SHARE_URL);
        } else {
            PreferenceUtil.commitString(APP_CONFIG_SHARE_URL, shareUrl);
        }
    }

    public static String getShareUrl(String defaultShareUrl) {
        return PreferenceUtil.getString(APP_CONFIG_SHARE_URL, defaultShareUrl);
    }
}
