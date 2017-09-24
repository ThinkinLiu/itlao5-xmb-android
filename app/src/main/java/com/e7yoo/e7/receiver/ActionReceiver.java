package com.e7yoo.e7.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.e7yoo.e7.service.E7Service;

/**
 * Created by andy on 2017/7/8.
 */
public class ActionReceiver extends BroadcastReceiver {
    // private static MessageListener mMessageListener;

    public ActionReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentE7Service = new Intent(context, E7Service.class);
        context.startService(intentE7Service);
    }

}
