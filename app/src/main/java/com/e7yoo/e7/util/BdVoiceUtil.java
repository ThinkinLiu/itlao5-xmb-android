package com.e7yoo.e7.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.SpeechRecognizer;
import android.util.AndroidRuntimeException;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.tts.client.SpeechSynthesizer;
import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/9/18.
 */

public class BdVoiceUtil {

    // 开始识别(会先停止SpeechSynthesizer)
    public static void startASR(SpeechRecognizer speechRecognizer, SpeechSynthesizer mSpeechSynthesizer) {
        stopTTS(mSpeechSynthesizer);
        Intent intent = new Intent();
        bindParams(intent);
        if(speechRecognizer != null) {
            speechRecognizer.startListening(intent);
        }
    }

    public static void stopASR(SpeechRecognizer speechRecognizer) {
        //  说完了
        if(speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    public static void cancelASR(SpeechRecognizer speechRecognizer) {
        // 取消
        if(speechRecognizer != null) {
            speechRecognizer.cancel();
        }
    }

    public static void destroyASR(SpeechRecognizer speechRecognizer) {
        cancelASR(speechRecognizer);
        if(speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    public static void bindParams(Intent intent) {
        // 设置识别参数
        intent.putExtra(TtsUtils.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
        intent.putExtra(TtsUtils.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
        intent.putExtra(TtsUtils.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
        intent.putExtra(TtsUtils.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
        intent.putExtra(TtsUtils.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        intent.putExtra("sample", 16000); // 离线仅支持16000采样率
        intent.putExtra("language", "cmn-Hans-CN"); // 离线仅支持中文普通话
        intent.putExtra("prop", 20000); // 输入
        // 语音输入附加资源，value替换为资源文件实际路径
        // 离线包过大，暂不考虑支持 intent.putExtra("lm-res-file-path", "/path/to/s_2_InputMethod");
    }

    public static EventManager initEventWakeUp(final Activity context) {
        // 唤醒功能打开步骤
        // 1) 创建唤醒事件管理器
        EventManager mWpEventManager = EventManagerFactory.create(context, "wp");
        // 2) 注册唤醒事件监听器
        mWpEventManager.registerListener(new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                try {
                    if(params == null) {
                        return;
                    }
                    JSONObject json = new JSONObject(params);
                    if ("wp.data".equals(name)) { // 每次唤醒成功, 将会回调name=wp.data的时间, 被激活的唤醒词在params的word字段
                        String word = json.getString("word"); // 唤醒词
                        WpEventManagerUtil.doEvent(context, word);
                        if(Logs.isDebug()) {
                            Logs.logI(BdVoiceUtil.class.getSimpleName(), "百度语音唤醒" + word);
                        }
                    } else if ("wp.exit".equals(name)) {
                        // 唤醒已经停止
                    }
                } catch (JSONException e) {
                    throw new AndroidRuntimeException(e);
                }
            }
        });
        return mWpEventManager;
    }
    public static EventManager eventWakeUp(final Activity context, EventManager mWpEventManager) {
        if(mWpEventManager == null) {
            mWpEventManager = initEventWakeUp(context);
        }
        // 3) 通知唤醒管理器, 启动唤醒功能
        HashMap params = new HashMap();
        params.put("kws-file", "assets:///WakeUp.bin"); // 设置唤醒资源, 唤醒资源请到 http://yuyin.baidu.com/wake#m4 来评估和导出
        mWpEventManager.send("wp.start", new JSONObject(params).toString(), null, 0, 0);
        return mWpEventManager;
    }

    public static void eventWekeUpStop(EventManager mWpEventManager) {
        if(mWpEventManager != null) {
            // 停止唤醒监听
            mWpEventManager.send("wp.stop", null, null, 0, 0);
        }
    }


    public static void stopTTS(SpeechSynthesizer mSpeechSynthesizer) {
        if(mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
        }
    }

    public static void releaseTTS(SpeechSynthesizer mSpeechSynthesizer) {
        stopTTS(mSpeechSynthesizer);
        if(mSpeechSynthesizer != null) {
            mSpeechSynthesizer.release();
        }
    }

    public static void startTTS(SpeechSynthesizer mSpeechSynthesizer, String text) {
        stopTTS(mSpeechSynthesizer);
        if(mSpeechSynthesizer != null) {
            mSpeechSynthesizer.speak(text);
        }
    }
}
