package com.e7yoo.e7.wxapi;


import android.content.Intent;
import android.os.Bundle;

import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

//import cn.jiguang.share.wechat.WeChatHandleActivity;

public class WXEntryActivity extends WXCallbackActivity/*WeChatHandleActivity*/ {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
        } catch (Throwable e) {
            CrashReport.postCatchedException(e);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

}