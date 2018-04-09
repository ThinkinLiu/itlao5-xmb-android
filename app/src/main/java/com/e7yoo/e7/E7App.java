package com.e7yoo.e7;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.OsUtil;
import com.e7yoo.e7.util.PreferenceUtil;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;

import cn.jiguang.share.android.api.JShareInterface;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/8/30.
 */

public class E7App extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initHotFix();
    }

    private void initHotFix() {
        // initialize必须放在attachBaseContext最前面，初始化代码直接写在Application类里面，切勿封装到其他类。
        String appVersion = OsUtil.getAppVersionName(this);
        if(appVersion.length() < 5) {
            return;
        }
        SophixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                        }
                    }
                }).initialize();
    }

    public void queryAndLoadNewPatch() {
        long last = PreferenceUtil.getLong(Constant.PREFERENCE_LAST_GET_PATCH_TIME, 0);
        long now = System.currentTimeMillis();
        if(now - last > 5 * 60 * 1000) { // 控制最多5分钟一次
            PreferenceUtil.commitLong(Constant.PREFERENCE_LAST_GET_PATCH_TIME, 0);
            // queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
            SophixManager.getInstance().queryAndLoadNewPatch();
        }
    }

    public static boolean auth = false;
    public static E7App mApp;
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        jPush();
        ali();
        bugly();
        auth = "c51334ce1b84a1efd36e603b88185f9b".equals(OsUtil.getUdid(mApp));
    }

    private void jPush() {
        JPushInterface.setDebugMode(false);
        JPushInterface.init(mApp);
        JShareInterface.setDebugMode(false);
        JShareInterface.init(mApp);
    }

    private void ali() {
        FeedbackAPI.init(mApp, "24846408", "379523f21cdc6c4379d25c0914b96846");
    }

    private void bugly() {
        MobclickAgent.setDebugMode(false);
        MobclickAgent.setCatchUncaughtExceptions(false);
        CrashReport.initCrashReport(getApplicationContext(), "ab0c0f5941", false);
        CrashReport.setUserId(OsUtil.getUdid(this));
    }

}
