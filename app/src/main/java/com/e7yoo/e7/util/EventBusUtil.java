package com.e7yoo.e7.util;

import android.os.Message;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/9/4.
 */

public class EventBusUtil {

    public static void post(int messageId, Object... obj) {
        Message msg = new Message();
        msg.what = messageId;
        if(obj != null && obj.length == 1) {
            msg.obj = obj[0];
        }
        EventBus.getDefault().post(msg);
    }
}
