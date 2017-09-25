package com.e7yoo.e7.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.model.PushMsg;
import com.e7yoo.e7.service.E7Service;
import com.e7yoo.e7.sql.MessageDbHelper;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.PreferenceUtil;
import com.e7yoo.e7.util.ShortCutUtils;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by andy on 2017/7/8.
 */
public class JPushReceiver extends BroadcastReceiver {
    // private static MessageListener mMessageListener;

    public JPushReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        /*Intent intentE7Service = new Intent(context, E7Service.class);
        context.startService(intentE7Service);*/
        if(intent == null) {
            return;
        }
        String action = intent.getAction();
        if(action == null ) {
            return;
        }
        if(action.equals(JPushInterface.ACTION_REGISTRATION_ID)) {
            // 注册所得到的注册 ID
        } else if(action.equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
            // 收到了自定义消息 Push 。
            actionMessageReceived(intent);
        } else if(action.equals(JPushInterface.ACTION_NOTIFICATION_RECEIVED)) {
            // 收到了通知 Push。
        } else if(action.equals(JPushInterface.ACTION_NOTIFICATION_OPENED)) {
            // 用户点击了通知
        } else if(action.equals(JPushInterface.ACTION_NOTIFICATION_CLICK_ACTION)) {
            // 用户点击了通知栏中自定义的按钮。
        } else {
            // 其他action
        }
    }

    /**
     * 自定义消息
     * @param intent
     */
    private void actionMessageReceived(Intent intent) {
        Bundle bundle = intent.getExtras();
        final String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String msgId = bundle.getString(JPushInterface.EXTRA_MSG_ID);
        final PushMsg pushMsg = new PushMsg();
        pushMsg.setTime(System.currentTimeMillis());
        pushMsg.setTitle(title);
        pushMsg.setContent(message);
        pushMsg.setExtras(extras);
        pushMsg.setMsgId(msgId);
        pushMsg.setDesc("");
        pushMsg.setUnread(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MessageDbHelper.getInstance(E7App.mApp).insertPushMsg(pushMsg, true);
                    int unRead = PreferenceUtil.getInt(Constant.PREFERENCE_PUSH_MSG_UNREAD, 0);
                    PreferenceUtil.commitInt(Constant.PREFERENCE_PUSH_MSG_UNREAD, ++unRead);
                    ShortCutUtils.addNumShortCut(E7App.mApp, MainActivity.class, true, String.valueOf(unRead));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
