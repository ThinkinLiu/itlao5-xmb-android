package com.e7yoo.e7.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.e7yoo.e7.BuildConfig;
import com.e7yoo.e7.service.E7Service;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.PreferenceUtil;
import com.e7yoo.e7.util.ServiceUtil;

/**
 * Created by andy on 2017/6/25.
 */
public class SmsReceiver extends BroadcastReceiver {
    // private static MessageListener mMessageListener;

    public SmsReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (Build.VERSION.SDK_INT < 23) {
            Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
            String format = intent.getStringExtra("format");
            int subId = intent.getIntExtra(PhoneConstants.SUBSCRIPTION_KEY, SubscriptionManager.getDefaultSmsSubId());
            int pduCount = messages.length;
            SmsMessage[] msgs = new SmsMessage[pduCount];

            for (int i = 0; i < pduCount; i++) {
                byte[] pdu = (byte[]) messages[i];
                msgs[i] = SmsMessage.createFromPdu(pdu, format);
                msgs[i].setSubId(subId);
            }
        } else {*/
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            int open = PreferenceUtil.getInt(Constant.PREFERENCE_OPEN_SMS_FINDPHONE, 0);
            int openLatlng = PreferenceUtil.getInt(Constant.PREFERENCE_OPEN_SMS_FINDPHONE_LATLNG, 0);
            if(open == 0 && openLatlng == 0) {
                return;
            }
            String str = PreferenceUtil.getString(Constant.PREFERENCE_SMS_FINDPHONE_TEXT, null);
            String strLatLng = PreferenceUtil.getString(Constant.PREFERENCE_SMS_FINDPHONE_TEXT_LATLNG, null);

            if(BuildConfig.DEBUG) {
                System.out.println("----------" + str + "---" + strLatLng);
            }
            if(str == null && strLatLng == null) {
                return;
            }
            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                // String sender = smsMessage.getDisplayOriginatingAddress();
                String content = smsMessage.getMessageBody();
                // long date = smsMessage.getTimestampMillis();
                // Date timeDate = new Date(date);
                // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // String time = simpleDateFormat.format(timeDate);
                if(BuildConfig.DEBUG) {
                    System.out.println("----------"+content + "---" + smsMessage.getOriginatingAddress());
                }
                if (content == null || content.length() < 6) {
                    continue;
                }
                // mMessageListener.OnReceived(content);
                if (content.trim().equals(str)) {
                    Intent intentE7Service = new Intent(context, E7Service.class);
                    intentE7Service.putExtra(E7Service.FROM, E7Service.FROM_SMS_RECEIVER);
                    //context.startService(intentE7Service);
                    ServiceUtil.startService(context, intentE7Service);
                    abortBroadcast();
                    return;
                } else if (content.trim().equals(strLatLng)) {
                    Intent intentE7Service = new Intent(context, E7Service.class);
                    intentE7Service.putExtra(E7Service.FROM, E7Service.FROM_SMS_RECEIVER_LATLNG);
                    intentE7Service.putExtra("phoneNum", smsMessage.getOriginatingAddress());//getDisplayOriginatingAddress());
                    //context.startService(intentE7Service);
                    ServiceUtil.startService(context, intentE7Service);
                    abortBroadcast();
                    return;
                } /*else {
                    Intent intentE7Service = new Intent(context, E7Service.class);
                    context.startService(intentE7Service);
                }*/
            }
            Intent intentE7Service = new Intent(context, E7Service.class);
            //context.startService(intentE7Service);
        ServiceUtil.startService(context, intentE7Service);
        /*}*/
    }

    // 回调接口
    /*public interface MessageListener {
        public void OnReceived(String message);
    }

    public void setOnReceivedMessageListener(MessageListener messageListener) {
        this.mMessageListener = messageListener;
    }*/

}
