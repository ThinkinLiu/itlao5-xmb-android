package com.e7yoo.e7.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.e7yoo.e7.AddRobotActivity;
import com.e7yoo.e7.ChatActivity;
import com.e7yoo.e7.CircleChatActivity;
import com.e7yoo.e7.FriendActivity;
import com.e7yoo.e7.InfoActivity;
import com.e7yoo.e7.InputActivity;
import com.e7yoo.e7.LoginActivity;
import com.e7yoo.e7.PostActivity;
import com.e7yoo.e7.PushMsgDetailsActivity;
import com.e7yoo.e7.R;
import com.e7yoo.e7.RegisterActivity;
import com.e7yoo.e7.SexActivity;
import com.e7yoo.e7.app.news.NewsWebviewActivity;
import com.e7yoo.e7.community.CollectFeedListActivity;
import com.e7yoo.e7.community.FeedDetailActivity;
import com.e7yoo.e7.community.SpaceActivity;
import com.e7yoo.e7.community.TopicListActivity;
import com.e7yoo.e7.game.GameActivity;
import com.e7yoo.e7.game.GameLandscapeActivity;
import com.e7yoo.e7.model.GameInfo;
import com.e7yoo.e7.model.PushMsg;
import com.e7yoo.e7.model.Robot;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Comment;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Like;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.utils.CommonUtils;

/**
 * Created by Administrator on 2017/8/31.
 */

public class ActivityUtil {

    public static void toChatActivity(Context context, Robot robot) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constant.INTENT_ROBOT, robot);
        context.startActivity(intent);
    }

    public static void toAddRobotActivityForResult(Activity activity, Robot robot, int requestCode) {
        Intent intent = new Intent(activity, AddRobotActivity.class);
        intent.putExtra(Constant.INTENT_ROBOT, robot);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void toPushMsgDetailsActivity(Context context, PushMsg pushMsg) {
        Intent intent = new Intent(context, PushMsgDetailsActivity.class);
        intent.putExtra("PushMsg", pushMsg);
        context.startActivity(intent);
    }

    public static void toNewsWebviewActivity(Context context, String url, String from) {
        Intent intent = new Intent(context, NewsWebviewActivity.class);
        intent.putExtra(NewsWebviewActivity.INTENT_URL, url);
        intent.putExtra(NewsWebviewActivity.INTENT_FROM, from);
        context.startActivity(intent);
    }

    public static void toInputActivityForResult(Activity activity, int titleResId, int maxLength, int minLength, String text, String hint, int requestCode) {
        Intent intent = new Intent(activity, InputActivity.class);
        intent.putExtra(Constant.INTENT_TITLE_RES_ID, titleResId);
        intent.putExtra(Constant.INTENT_MAX_LENGTH, maxLength);
        intent.putExtra(Constant.INTENT_MIN_LENGTH, minLength);
        intent.putExtra(Constant.INTENT_TEXT, text);
        intent.putExtra(Constant.INTENT_HINT, hint);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void toSexActivityForResult(Activity activity, int titleResId, boolean showUnkoneSex, int sex,int requestCode) {
        Intent intent = new Intent(activity, SexActivity.class);
        intent.putExtra(Constant.INTENT_TITLE_RES_ID, titleResId);
        intent.putExtra(Constant.INTENT_SHOW_UNKNOW_SEX, showUnkoneSex);
        intent.putExtra(Constant.INTENT_SEX, sex);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void toActivity(Activity activity, Class toActivity) {
        Intent intent = new Intent(activity, toActivity);
        activity.startActivity(intent);
    }

    public static void toActivityForResult(Activity activity, Class toActivity, int requestCode) {
        Intent intent = new Intent(activity, toActivity);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void toActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
    }

    public static void toGameActivity(Context context, String url, String from, boolean isLandscape, GameInfo... gameInfo) {
        Class cls = isLandscape ? GameLandscapeActivity.class : GameActivity.class;
        Intent intent = new Intent(context, cls);
        intent.putExtra(GameActivity.INTENT_URL, url);
        intent.putExtra(GameActivity.INTENT_FROM, from);
        if(gameInfo != null && gameInfo.length > 0) {
            intent.putExtra(GameActivity.INTENT_GAME_INFO, gameInfo[0]);
        }
        context.startActivity(intent);
    }

    public static void toGameActivity(Context context, GameInfo gameInfo, String from, boolean isLandscape) {
        Class cls = isLandscape ? GameLandscapeActivity.class : GameActivity.class;
        Intent intent = new Intent(context, cls);
        intent.putExtra(GameActivity.INTENT_URL, gameInfo);
        context.startActivity(intent);
    }

    public static void toLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void toRegister(Context context, String name) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.putExtra("name", name);
        context.startActivity(intent);
    }

    public static void toCommUserInfo(Context context, CommUser commUser) {
        Intent intent = new Intent(context, InfoActivity.class);
        intent.putExtra("CommUser", commUser);
        context.startActivity(intent);
    }

    public static void toPostActivity(Context context, Topic topic) {
        Intent intent = new Intent(context, PostActivity.class);
        intent.putExtra("Topic", topic);
        context.startActivity(intent);
    }

    public static void toTopicListActivityForResult(Activity activity, Topic topic, int requestCode) {
        Intent intent = new Intent(activity, TopicListActivity.class);
        intent.putExtra("Topic", topic);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void toPostOrLogin(Activity activity, Topic topic) {
        if(CommonUtils.isLogin(activity)) {
            ActivityUtil.toPostActivity(activity, topic);
        } else {
            ActivityUtil.toLogin(activity);
        }
    }

    public static void toPostActivity(Activity context, String text) {
        Intent intent = new Intent(context, PostActivity.class);
        intent.putExtra("text", text);
        context.startActivity(intent);
    }

    public static boolean toPostOrLogin(Activity activity, String text) {
        if(CommonUtils.isLogin(activity)) {
            ActivityUtil.toPostActivity(activity, text);
            return true;
        } else {
            ActivityUtil.toLogin(activity);
            return false;
        }
    }

    public static void toFeedDetail(Activity activity, FeedItem feedItem) {
        Intent intent = new Intent(activity, FeedDetailActivity.class);
        intent.putExtra("FeedItem", feedItem);
        ActivityUtil.toActivity(activity, intent);
    }

    public static void toFeedDetail(Activity activity, FeedItem feedItem, FeedItem comment) {
        Intent intent = new Intent(activity, FeedDetailActivity.class);
        intent.putExtra("FeedItem", feedItem);
        intent.putExtra("FeedItemComment", comment);
        ActivityUtil.toActivity(activity, intent);
    }

    public static void toFeedDetail(Activity activity, FeedItem feedItem, Like like) {
        Intent intent = new Intent(activity, FeedDetailActivity.class);
        intent.putExtra("FeedItem", feedItem);
        intent.putExtra("Like", like);
        ActivityUtil.toActivity(activity, intent);
    }

    public static void toSpace(Activity activity, CommUser commUser, boolean checkLogin) {
        if(!checkLogin || CommonUtils.isLogin(activity)) {
            Intent intent = new Intent(activity, SpaceActivity.class);
            intent.putExtra("CommUser", commUser);
            ActivityUtil.toActivity(activity, intent);
        } else {
            ActivityUtil.toLogin(activity);
        }
    }

    public static void toFriend(Activity activity, boolean checkLogin) {
        if(!checkLogin || CommonUtils.isLogin(activity)) {
            Intent intent = new Intent(activity, FriendActivity.class);
            ActivityUtil.toActivity(activity, intent);
        } else {
            ActivityUtil.toLogin(activity);
        }
    }

    public static void toCircleChat(Activity activity, CommUser commUser, boolean checkLogin) {
        if(!checkLogin || CommonUtils.isLogin(activity)) {
            Intent intent = new Intent(activity, CircleChatActivity.class);
            intent.putExtra("targetCommUser", commUser);
            ActivityUtil.toActivity(activity, intent);
        } else {
            ActivityUtil.toLogin(activity);
        }
    }

    public static void toCollect(Activity activity, boolean checkLogin) {
        if(!checkLogin || CommonUtils.isLogin(activity)) {
            Intent intent = new Intent(activity, CollectFeedListActivity.class);
            ActivityUtil.toActivity(activity, intent);
        } else {
            ActivityUtil.toLogin(activity);
        }
    }
}
