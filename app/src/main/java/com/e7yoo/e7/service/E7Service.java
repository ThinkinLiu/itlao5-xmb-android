package com.e7yoo.e7.service;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.VoiceRecognitionService;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.speech.asr.WakeUpControl;
import com.e7yoo.e7.BuildConfig;
import com.e7yoo.e7.ChatActivity;
import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.util.BdVoiceUtil;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.Loc;
import com.e7yoo.e7.util.MyRecognizer;
import com.e7yoo.e7.util.OfflineRecogParams;
import com.e7yoo.e7.util.OsUtil;
import com.e7yoo.e7.util.PreferenceUtil;
import com.e7yoo.e7.util.TastyToastUtil;
import com.e7yoo.e7.util.TtsUtils;
import com.e7yoo.e7.util.WpEventManagerUtil;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class E7Service extends Service implements RecognitionListener {
    public static final String FROM = "from";
    public static final int FROM_SMS_RECEIVER = 1001;
    public static final int FROM_SMS_RECEIVER_LATLNG = 1002;
    private Loc mLoc;

    private SpeechRecognizer mSpeechRecognizer;

    public E7Service() {
    }

    private void registerScreenActionReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);
    }

    private boolean isScreenOn = true;
    private void init(Context context) {
        if (PreferenceUtil.getInt(Constant.PREFERENCE_OPEN_VOICE_FINDPHONE, 0) != 0) {
            try {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
            } catch (Throwable e) {
                e.printStackTrace();
                CrashReport.postCatchedException(e);
            }
            try {
                if (isScreenOn) {
                    KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                    isScreenOn = mKeyguardManager.inKeyguardRestrictedInputMode();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                CrashReport.postCatchedException(e);
            }
            try {
                if (!isScreenOn) {
                    eventWakeUp();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                CrashReport.postCatchedException(e);
            }
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent == null) {
                return;
            }
            switch (intent.getAction()) { // 屏幕关闭才开启语音唤醒，为了解决跟微信等使用语音的app之间的冲突
                case Intent.ACTION_SCREEN_OFF:
                    isScreenOn = false;
                    try {
                        int i = PreferenceUtil.getInt(Constant.PREFERENCE_OPEN_VOICE_FINDPHONE, 0);
                        if (PreferenceUtil.getInt(Constant.PREFERENCE_OPEN_VOICE_FINDPHONE, 0) != 0) {
                            eventWakeUp();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        // // CrashReport.postCatchedException(e);
                    }
                    break;
                case Intent.ACTION_SCREEN_ON:
                    isScreenOn = true;
                    try {
                        if (PreferenceUtil.getInt(Constant.PREFERENCE_OPEN_VOICE_FINDPHONE, 0) != 0) {
                            eventWekeUpStop();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        // // CrashReport.postCatchedException(e);
                    }
                    break;
            }
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Object phoneNumObj = new Object();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            switch (intent.getExtras().getInt(FROM, 0)) {
                case FROM_SMS_RECEIVER:
                    openVoice();
                    // // MobclickAgent.onEvent(E7App.mApp, Constant.Umeng.Send_play_music_sms);
                    break;
                case FROM_SMS_RECEIVER_LATLNG:
                    synchronized (phoneNumObj) {
                        this.phoneNum = intent.getStringExtra("phoneNum");
                        if(this.phoneNum == null) {
                            break;
                        }
                    }
                    mLoc = Loc.getInstance(mLoc);
                    mLoc.startLocation(myListener);
                    break;
            }
        }
        registerScreenActionReceiver();
        init(this);
        return START_STICKY;
    }

    private void sendMsg(String phoneNum, String message) {
        if(phoneNum == null) {
            // // MobclickAgent.onEvent(E7App.mApp, Constant.Umeng.Send_sms_latlng_phoneisnull);
            return;
        }
        String SENT = "sms_sent";
        String DELIVERED = "sms_delivered";
        PendingIntent sentPI = PendingIntent.getActivity(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getActivity(this, 0, new Intent(DELIVERED), 0);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        // // MobclickAgent.onEvent(E7App.mApp, Constant.Umeng.Send_sms_latlng_success);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                    default:
                        // // CrashReport.postCatchedException(new Throwable(getResultData() + getResultCode()));
                        break;
                }
            }
        }, new IntentFilter(SENT));
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        // // MobclickAgent.onEvent(E7App.mApp, Constant.Umeng.Send_sms_latlng_success);
                        break;
                    case Activity.RESULT_CANCELED:
                    default:
                        // // CrashReport.postCatchedException(new Throwable(getResultData() + getResultCode()));
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
        SmsManager smsm = SmsManager.getDefault();
        if(Build.VERSION.SDK_INT >= 22) {
            try {
                List<SubscriptionInfo> mSubInfoList = SubscriptionManager.from(this).getActiveSubscriptionInfoList();
                int mSubCount = (mSubInfoList != null && !mSubInfoList.isEmpty()) ? mSubInfoList.size() : 0;
                int subId = -1;
                if (mSubCount != 0) {
                    subId = (int) mSubInfoList.get(0).getSubscriptionId();
                }
                smsm = SmsManager.getSmsManagerForSubscriptionId(subId);
            } catch (Throwable e) {
                e.printStackTrace();
                // // CrashReport.postCatchedException(e);
            }
        }
        try {
            if (message.length() > 70) {
                ArrayList<String> msgs = smsm.divideMessage(message);
                ArrayList<PendingIntent> sentIntents =  new ArrayList<PendingIntent>();
                for(int i = 0;i<msgs.size();i++){
                    sentIntents.add(sentPI);
                }
                smsm.sendMultipartTextMessage(phoneNum, null, msgs, sentIntents, null);
            } else {
                smsm.sendTextMessage(phoneNum, null, message, sentPI, deliveredPI);
            }
            // smsm.sendTextMessage(phoneNum, null, message, sentPI, deliveredPI);
        } catch (Throwable e) {
            System.out.println("-------------" + e.getMessage());
            e.printStackTrace();
            // // CrashReport.postCatchedException(e);
        }
    }

    private void openVoice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                playMusic(SOUND_PHONE_IS_HERE);
            }
        }).start();
    }

    private SoundPool soundp;
    private HashMap<String, Integer> soundm;
    private static final String SOUND_PHONE_IS_HERE = "here";

    /**
     * 初始化音效
     */
    public void initMusic() {
        soundp = new SoundPool(50, AudioManager.STREAM_MUSIC, 100);
        soundm = new HashMap<String, Integer>();
        soundm.put(SOUND_PHONE_IS_HERE, soundp.load(this, R.raw.findphone, 1));
    }

    /**
     * 播放音效
     */
    public void playMusic(String str) {
        // // MobclickAgent.onEvent(E7App.mApp, Constant.Umeng.Send_play_music);
        if (soundp == null || soundm == null) {
            initMusic();
        }
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int ringMode = am.getRingerMode();
        if (ringMode == AudioManager.RINGER_MODE_VIBRATE || ringMode == AudioManager.RINGER_MODE_SILENT)
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        int now = am.getStreamVolume(AudioManager.STREAM_MUSIC);//得到听筒模式的当前值
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, max, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        soundp.play(soundm.get(str), 1, 1, 0, 0, 1f);
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        am.setStreamVolume(AudioManager.STREAM_MUSIC, now, AudioManager.FLAG_PLAY_SOUND);
    }

    public void releaseMusic() {
        if (soundp != null) {
            soundp.release();
            soundp = null;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        try {
            // eventWakeUp();
        } catch (Throwable e) {
            // // CrashReport.postCatchedException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
//            eventWekeUpStop();
//            mWpEventManager.unregisterListener(eventListener);
//            mWpEventManager = null;
            if(mMyRecognizer != null) {
                mMyRecognizer.release();
            }
            releaseMusic();
        } catch (Throwable e) {
            e.printStackTrace();
            // // CrashReport.postCatchedException(e);
        }
        startService(new Intent(this, E7Service.class));
    }


    private EventManager mWpEventManager;

    public String initEventWakeUp() {
        String key = PreferenceUtil.getString(Constant.PREFERENCE_WAKEUP_KEYWORD, null);
        if(key == null || key.length() > 0 || Arrays.binarySearch(new int[]{0,1,5,6,8}, Arrays.binarySearch(WpEventManagerUtil.KEYWORDS, key)) >= 0) {
            if(mWpEventManager == null) {
                // 唤醒功能打开步骤
                // 1) 创建唤醒事件管理器
                mWpEventManager = EventManagerFactory.create(this, "wp");
                // 2) 注册唤醒事件监听器
                mWpEventManager.registerListener(eventListener);
            }
            return null;
        } else {
            if(mMyRecognizer == null) {
                mMyRecognizer = new MyRecognizer(this, eventListener);
            }
//            if(mSpeechRecognizer == null) {
//                mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(E7Service.this, new ComponentName(E7Service.this, VoiceRecognitionService.class));
//                // 注册监听器
//                mSpeechRecognizer.setRecognitionListener(this);
//            }
            return key;
        }
    }

        private EventListener eventListener = new EventListener() {
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
            try {
                JSONObject json = new JSONObject(params);
                if ("wp.data".equals(name)) { // 每次唤醒成功, 将会回调name=wp.data的时间, 被激活的唤醒词在params的word字段
                    String word = json.getString("word"); // 唤醒词
                    if (WpEventManagerUtil.KEYWORDS[8].equals(word)) {
                        // 语音回复“萌萌在这里”
                        openVoice();
                        // // MobclickAgent.onEvent(E7App.mApp, Constant.Umeng.Send_xmb_wpevent2);
                        TastyToastUtil.toast(E7App.mApp, R.string.mengmeng_is_here);
                    } else {
                        WpEventManagerUtil.doEvent(null, word);
                        // // MobclickAgent.onEvent(E7App.mApp, Constant.Umeng.Send_wpevent2);
                    }
                    /*if (LogUtils.isDebug()) {
                        LogUtils.println("百度语音唤醒" + word);
                    }*/
                } else if ("wp.exit".equals(name)) {
                    // 唤醒已经停止
                }
            } catch (Throwable e) {
                e.printStackTrace();
                // // CrashReport.postCatchedException(e);
            }
        }
    };

    private String mKey = null;
    private MyRecognizer mMyRecognizer;
    private Map<String, Object> mMap = null;
    public void eventWakeUp() {
        String key = initEventWakeUp();

        if(key == null) {
            // 3) 通知唤醒管理器, 启动唤醒功能
            HashMap params = new HashMap();
            params.put("kws-file", "assets:///WakeUp.bin"); // 设置唤醒资源, 唤醒资源请到 http://yuyin.baidu.com/wake#m4 来评估和导出
            mWpEventManager.send("wp.start", new JSONObject(params).toString(), null, 0, 0);
        } else {
            if(!key.equals(mKey)) {
                mMyRecognizer.loadOfflineEngine(OfflineRecogParams.fetchOfflineParams(key));
            }
            if(mMap == null) {
                mMap = new HashMap<>();
                mMap.put(SpeechConstant.DECODER, 2);
                mMap.remove(SpeechConstant.PID); // 去除pid，只支持中文
            }
            mMyRecognizer.start(mMap);
//            BdVoiceUtil.startASR(mSpeechRecognizer, null, true);
        }
    }

    public void eventWekeUpStop() {
        if (mWpEventManager != null) {
            // 停止唤醒监听
            mWpEventManager.send("wp.stop", null, null, 0, 0);
        }
//        if (mSpeechRecognizer != null) {
//            BdVoiceUtil.stopASR(mSpeechRecognizer);
//        }
        if(mMyRecognizer != null) {
            mMyRecognizer.stop();
        }
    }

    private String phoneNum;
    private void sendMsg(double lat, double lng) {
        String message = "http://api.map.baidu.com/geocoder?" +
                "location=" + lat + "," + lng +
                /*"&coord_type=bd0911"  +*/ //gcj02"
                "&output=html" +
                "&src=e7yoo【" + OsUtil.getAppName(this) + "】";
        synchronized (phoneNumObj) {
            if(phoneNum == null) {
                // CrashReport.postCatchedException(new Exception("phoneNum == null"));
                return;
            }
            String phoneNum = this.phoneNum;
            sendMsg(phoneNum, message);
            this.phoneNum = null;
        }
        // MobclickAgent.onEvent(E7App.mApp, Constant.Umeng.Send_sms_latlng);
    }

    private BDLocationListener myListener = new MyLocationListener();

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location != null) {
                int locType = location.getLocType();
                switch (locType) {
                    case BDLocation.TypeNetWorkLocation:
                    case BDLocation.TypeGpsLocation:
                    case BDLocation.TypeOffLineLocation:
                        Loc.getInstance(mLoc).stopLocation();
                        sendMsg(location.getLatitude(), location.getLongitude());
                        break;
                }
            }
            if(BuildConfig.DEBUG) {
                //获取定位结果
                StringBuffer sb = new StringBuffer(256);

                sb.append("time : ");
                sb.append(location.getTime());    //获取定位时间

                sb.append("\nerror code : ");
                sb.append(location.getLocType());    //获取类型类型

                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());    //获取纬度信息

                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());    //获取经度信息

                sb.append("\nradius : ");
                sb.append(location.getRadius());    //获取定位精准度

                if (location.getLocType() == BDLocation.TypeGpsLocation){

                    // GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());    // 单位：公里每小时

                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());    //获取卫星数

                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());    //获取海拔高度信息，单位米

                    sb.append("\ndirection : ");
                    sb.append(location.getDirection());    //获取方向信息，单位度

                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());    //获取地址信息

                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");

                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){

                    // 网络定位结果
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());    //获取地址信息

                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());    //获取运营商信息

                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");

                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                    // 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");

                } else if (location.getLocType() == BDLocation.TypeServerError) {

                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");

                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

                }

                sb.append("\nlocationdescribe : ");
                sb.append(location.getLocationDescribe());    //位置语义化信息

                List<Poi> list = location.getPoiList();    // POI数据
                if (list != null) {
                    sb.append("\npoilist size = : ");
                    sb.append(list.size());
                    for (Poi p : list) {
                        sb.append("\npoi= : ");
                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                    }
                }

                Log.i("BaiduLocationApiDem", sb.toString());

            }
        }
    }
}
