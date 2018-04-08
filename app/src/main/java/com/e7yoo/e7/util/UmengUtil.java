package com.e7yoo.e7.util;

import com.e7yoo.e7.E7App;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by andy on 2017/10/5.
 */

public class UmengUtil {
    public static final void onEvent(String event) {
        MobclickAgent.onEvent(E7App.mApp, event);
    }

    public static final String FP_VOICE_1 = "3_fp_voice_1";
    public static final String FP_VOICE_0 = "3_fp_voice_0";
    public static final String FP_SMS_1 = "3_fp_sms_1";
    public static final String FP_SMS_0 = "3_fp_sms_0";
    public static final String FP_LATLNG_1 = "3_fp_latlng_1";
    public static final String FP_LATLNG_0 = "3_fp_latlng_0";
    public static final String TTS = "3_tts";
    public static final String PULL_UP = "3_pull_up";
    public static final String VOICE = "3_voice";
    public static final String SEND_TXT = "3_send_txt";
    public static final String SEND_IMG = "3_send_img";
    public static final String SEND_JOKE = "3_send_joke";
    public static final String SEND_NEWS = "3_send_news";
    public static final String SEND_GAME = "3_send_game";
    public static final String SEND_VOICE = "3_send_voice";
    public static final String CHAT_TO_BIGPIC = "3_chat_to_bigpic";
    public static final String CHAT_TO_GAME = "3_chat_to_game";
    public static final String CHAT_TO_LIGHT = "3_chat_to_light";
    public static final String CHAT_TO_NEWS = "3_chat_to_news";
    public static final String CHAT_TO_FINDPHONE = "3_chat_to_findphone";
    public static final String CHAT_TO_CESHI = "3_chat_to_ceshi";
    public static final String CHAT_TO_ZYJ = "3_chat_to_zyj";
    public static final String CHAT_TO_GIF = "3_chat_to_gif";
    public static final String CHAT_TO_BIG_PIC = "3_chat_to_big_pic";
    public static final String CHAT_TO_SHARE = "3_chat_to_share";
    public static final String CHAT_TO_HISTORY = "3_chat_to_history";
    public static final String CHAT_TO_CIRCLE = "3_chat_to_circle";
    public static final String CHAT_TO_WEB = "3_chat_to_web";
    public static final String GAME_2048 = "3_game_2048";
    public static final String GAME_BIRD = "3_game_bird";
    public static final String GAME_PLANE = "3_game_plane";
    public static final String GAME_ZYJ = "3_game_zyj";
    public static final String GAME_ZQC = "3_game_zqc";
    public static final String GAME_WCGD = "3_game_wcgd";
    public static final String GAME_GSC = "3_game_gsc";
    public static final String GAME_KD = "3_game_kd";
    public static final String GAME_BK = "3_game_bk";
    public static final String GAME_JFCJ = "3_game_jfcj";
    public static final String GAME_JXMT = "3_game_jxmt";
    public static final String GAME_OTHER = "3_game_other";
    public static final String GAME_SHARE = "3_game_share";
    public static final String ABOUT_SHARE = "3_about_share";
    public static final String ABOUT_LIKE = "3_about_like";
    public static final String FEED_DETAILS = "3_feed_details";


    public static final String POST_FROM_HOME_HEADER = "3_post_from_home_header";
    public static final String GAME_FROM_HOME_HEADER = "3_game_from_home_header";
    public static final String FINEPHONE_FROM_HOME_HEADER = "3_findphone_from_home_header";
    public static final String FLASHLIGHT_FROM_HOME_HEADER = "3_flashlight_from_home_header";
    public static final String JOKE_FROM_HOME_HEADER = "3_joke_from_home_header";

    public static final String JOKE_LIST_JOKE_REFRESH = "3_joke_list_joke_refresh";
    public static final String JOKE_LIST_JOKE_MORE = "3_joke_list_joke_more";
    public static final String JOKE_LIST_PIC_REFRESH = "3_joke_list_pic_refresh";
    public static final String JOKE_LIST_PIC_MORE = "3_joke_list_pic_more";
}
