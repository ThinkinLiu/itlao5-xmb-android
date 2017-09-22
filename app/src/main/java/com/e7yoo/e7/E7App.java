package com.e7yoo.e7;

import android.app.Application;

import cn.jiguang.share.android.api.JShareInterface;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/8/30.
 */

public class E7App extends Application {

    public static E7App mApp;
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        JPushInterface.setDebugMode(true);
        JPushInterface.init(mApp);
        JShareInterface.setDebugMode(true);
        JShareInterface.init(mApp);
    }

}
