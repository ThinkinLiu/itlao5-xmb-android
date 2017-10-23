package com.e7yoo.e7.util;

/**
 * Created by Administrator on 2017/8/30.
 */

public class Constant {

    public static final int NET_NO = -10000;

    public static final int EVENT_BUS_REFRESH_RecyclerView = 1;
    public static final int EVENT_BUS_REFRESH_RecyclerView_NOMORE = 2;
    public static final int EVENT_BUS_REFRESH_RecyclerView_FROM_SQL = 3;
    public static final int EVENT_BUS_REFRESH_RecyclerView_ADD_ROBOT = 4;
    public static final int EVENT_BUS_REFRESH_RecyclerView_UPDATE_ROBOT = 5;
    public static final int EVENT_BUS_REFRESH_RecyclerView_INIT_ROBOT = 6;
    public static final int EVENT_BUS_NET_tobotAsk = 101;
    public static final int EVENT_BUS_NET_todayHistory = 102;
    public static final int EVENT_BUS_NET_todayHistoryDetails = 103;
    public static final int EVENT_BUS_NET_jokeNew = 104;
    public static final int EVENT_BUS_CIRCLE_LOGIN = 1001;
    public static final int EVENT_BUS_CIRCLE_REGISTER = 1002;
    public static final int EVENT_BUS_CIRCLE_LOGOUT = 1003;
    public static final int EVENT_BUS_COMMUSER_MODIFY = 1004;
    public static final int EVENT_BUS_POST_FEED_SUCCESS= 1005;
    public static final int EVENT_BUS_DELETE_FEED_SUCCESS= 1006;

    public static final String FILE_ROBOT_LIST = "file_robot_list";
    public static final String FILE_FAVORITES_NEWS_LIST = "file_favorites_news_list";

    public static final String INTENT_TITLE_RES_ID = "title_res_id";
    public static final String INTENT_MAX_LENGTH = "max_length";
    public static final String INTENT_MIN_LENGTH = "min_length";
    public static final String INTENT_ROBOT = "robot";
    public static final String INTENT_TEXT = "text";
    public static final String INTENT_HINT = "hint";
    public static final String INTENT_INT = "int";
    public static final String INTENT_SHOW_UNKNOW_SEX = "show_unknow_sex";
    public static final String INTENT_SEX = "sex";


    public static final String SP_IS_NEED_INIT_ROBOT = "is_need_init_robot";

    public static final String PREFERENCE_SMS_FINDPHONE_TEXT = "preference_sms_findphone_text";
    public static final String PREFERENCE_SMS_FINDPHONE_TEXT_LATLNG = "preference_sms_findphone_text_latlng";
    public static final String PREFERENCE_OPEN_SMS_FINDPHONE = "preference_open_sms_findphone";
    public static final String PREFERENCE_OPEN_SMS_FINDPHONE_LATLNG = "preference_open_sms_findphone_latlng";
    public static final String PREFERENCE_OPEN_VOICE_FINDPHONE = "preference_open_voice_findphone";
    /** 回复是否自动语音播报，0 不播报，1 播报，2 播报所有非提示性的返回值，3 仅播报通过语音输入时的回复 */
    public static final String PREFERENCE_REPLY_TTS_TYPE = "preference_reply_tts_type";
    public static final String PREFERENCE_REPLY_TTS_ISHINT = "preference_reply_tts_ishint";
    public static final String PREFERENCE_REPLY_PUSH_DISTURB = "preference_reply_push_disturb";
    public static final String PREFERENCE_PUSH_MSG_UNREAD = "preference_push_msg_unread";
    public static final String PREFERENCE_CHAT_PULL_UP_TIMES = "preference_chat_pull_up_times";
    public static final String PREFERENCE_CHAT_OPEN_TIMES = "preference_chat_open_times";
}
