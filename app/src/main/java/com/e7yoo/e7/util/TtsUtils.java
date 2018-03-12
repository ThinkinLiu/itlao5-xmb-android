package com.e7yoo.e7.util;

import android.content.Context;
import android.os.Environment;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import java.io.File;

public class TtsUtils {


	public static final String EXTRA_SOUND_START = "sound_start";
	public static final String EXTRA_SOUND_END = "sound_end";
	public static final String EXTRA_SOUND_SUCCESS = "sound_success";
	public static final String EXTRA_SOUND_ERROR = "sound_error";
	public static final String EXTRA_SOUND_CANCEL = "sound_cancel";

	private static String mDirPath;
	private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_ch_speech_female.dat";
	private static final String TEXT_MODEL_NAME = "bd_etts_ch_text.dat";
	private static final String DIR_NAME = "baiduTTS";
	/*private static final String S_2_INPUTMETHOD = "s_2_InputMethod.txt";*/

	private static void initialEnv() {
		if (mDirPath == null) {
			String sdcardPath = Environment.getExternalStorageDirectory().toString();
			mDirPath = sdcardPath + "/" + DIR_NAME;
		}
		makeDir(mDirPath);
        /*new Thread(new Runnable() {
			@Override
			public void run() {*/
				FileUtil.copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
				FileUtil.copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mDirPath + "/" + TEXT_MODEL_NAME);
				/*copyFromAssetsToSdcard(false, S_2_INPUTMETHOD, mDirPath + "/" + S_2_INPUTMETHOD);*/
			/*}
		}).start();*/
	}

	private static void makeDir(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 *
	 * @param mSpeechSynthesizer
	 * @param speaker 0 (普通女声), 1 (普通男声), 2 (特别男声), 3 (情感男声), 4 (童声)
	 */
	public static void changeSpeaker(SpeechSynthesizer mSpeechSynthesizer, int speaker) {
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, String.valueOf(speaker));
	}


	/** 初始化语音合成客户端并启动
	 * @param speaker 0 (普通女声), 1 (普通男声), 2 (特别男声), 3 (情感男声), 4 (童声)
	 */
	public static SpeechSynthesizer getSpeechSynthesizer(Context context, SpeechSynthesizerListener speechSynthesizerListener, int speaker) {
		initialEnv();
		// 获取语音合成对象实例
		SpeechSynthesizer mSpeechSynthesizer = SpeechSynthesizer.getInstance();
		// 设置context
		mSpeechSynthesizer.setContext(context);
		// 设置语音合成状态监听器
		mSpeechSynthesizer.setSpeechSynthesizerListener(speechSynthesizerListener);
		// 设置在线语音合成授权，需要填入从百度语音官网申请的api_key和secret_key
		mSpeechSynthesizer.setApiKey("sdUF1rdsEihOc2wqxc4CfW2k3cRP3Y6T", "Frs7YCTuyBW9FeZ3xiVARAfcGD7G4COq");
		// 设置离线语音合成授权，需要填入从百度语音官网申请的app_id
		mSpeechSynthesizer.setAppId("8389593");
		// 设置语音合成文本模型文件
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mDirPath + "/"
				+ TEXT_MODEL_NAME);
		// 设置语音合成声音模型文件
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mDirPath + "/"
				+ SPEECH_FEMALE_MODEL_NAME);
		// 设置语音合成声音授权文件--测试使用
		/**mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, "your_licence_path");*/
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, String.valueOf(speaker));
		// 获取语音合成授权信息
		AuthInfo authInfo = mSpeechSynthesizer.auth(TtsMode.MIX);
		// 判断授权信息是否正确，如果正确则初始化语音合成器并开始语音合成，如果失败则做错误处理
		if (authInfo.isSuccess()) {
			try {
				mSpeechSynthesizer.initTts(TtsMode.MIX);
			} catch (Throwable e) {
				// TODO
				try {
					mSpeechSynthesizer.initTts(TtsMode.ONLINE);
				} catch (Throwable e2) {
					// TODO
				}
			}
			// mSpeechSynthesizer.speak("百度语音合成示例程序正在运行");
		} else {
			// 授权失败
			AuthInfo authInfo2 = mSpeechSynthesizer.auth(TtsMode.ONLINE);
			if (authInfo2.isSuccess()) {
				mSpeechSynthesizer.initTts(TtsMode.ONLINE);
				// TODO
				// CrashReport.postCatchedException(new Exception("百度tts MIX授权失败"));
			} else {// TODO
				// CrashReport.postCatchedException(new Exception("百度tts MIX & ONLINE授权失败"));
			}
		}
		return mSpeechSynthesizer;
	}

}
