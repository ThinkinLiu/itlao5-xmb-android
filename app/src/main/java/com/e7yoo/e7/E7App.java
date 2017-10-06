package com.e7yoo.e7;

import android.app.Application;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.e7yoo.e7.util.OsUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;

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
        jPush();
        ali();
        bugly();
    }

    private void jPush() {
        JPushInterface.setDebugMode(false);
        JPushInterface.init(mApp);
        JShareInterface.setDebugMode(false);
        JShareInterface.init(mApp);
    }

    private void ali() {
        FeedbackAPI.init(mApp, "23473106", "f934f0f40717aa6b4416cf3883f28f6d");
    }

    private void bugly() {
        MobclickAgent.setDebugMode(false);
        MobclickAgent.setCatchUncaughtExceptions(false);
        CrashReport.initCrashReport(getApplicationContext(), "ab0c0f5941", false);
        CrashReport.setUserId(OsUtil.getUdid(this));
    }

}
