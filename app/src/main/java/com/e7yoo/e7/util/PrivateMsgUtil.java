package com.e7yoo.e7.util;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.MsgUrlType;
import com.e7yoo.e7.model.PrivateMsg;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/8/30.
 */

public class PrivateMsgUtil {
    public static PrivateMsg getRecvPrivateMsg(int robotId, String content) {
        return new PrivateMsg(1000, System.currentTimeMillis(), content, null, PrivateMsg.Type.REPLY, robotId);
    }
    public static PrivateMsg getHintPrivateMsg(int robotId, String content) {
        return new PrivateMsg(1000, System.currentTimeMillis(), content, null, PrivateMsg.Type.HINT, robotId);
    }

    public static PrivateMsg getSendPrivateMsg(int robotId, String content) {
        return new PrivateMsg(1000, System.currentTimeMillis(), content, null, PrivateMsg.Type.SEND, robotId);
    }

    public static PrivateMsg getRobotPrivateMsg(JSONObject object, String robotName, int robotId) {
        PrivateMsg msg = null;
        try {
            if (object != null) {
                JSONObject jo = null;
                if(object.has("showapi_res_code")) { // 数据来自showapi
                    jo = object.optJSONObject("showapi_res_body");
                } else if (object.getInt("error_code") == 0) { // 数据来自聚合数据
                    jo = object.optJSONObject("result");
                }
                if (jo != null) {
                    int code = jo.optInt("code");
                    String text = jo.optString("text");
                    text = replaceTianqi(text);
                    text = replaceName(text, robotName);
                    String url = jo.optString("url", null);
                    if(url == null && jo.has("list")) {
                        try {
                            url = jo.optJSONArray("list").optJSONObject(0).optString("detailurl");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    msg = new PrivateMsg(code, System.currentTimeMillis(), text, url, PrivateMsg.Type.REPLY, robotId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(msg == null) {
            msg = new PrivateMsg(1000, System.currentTimeMillis(), E7App.mApp.getString(R.string.tuling_exception), MsgUrlType.login, PrivateMsg.Type.REPLY, robotId);
        }
        return msg;
    }

    private static String replaceTianqi(String text) {
        if(text != null && text.contains("月") && text.contains("周") && text.contains("°") && text.contains("号") && text.contains("-")) {
            text = text.replace(":", ":\n").replace(";", ";\n").replaceFirst("°", "° 当前温度");
        }
        return text;
    }

    private static String replaceName(String text, String replaceTo) {
        if(text != null) {
            for(int i = 0; i < array.length; i++) {
                text = text.replaceAll(array[i], replaceTo);
            }
        }
        return text;

    }

    private static String[] array = {"聚合数据", "萌萌", "图灵机器人", "图灵工程师", "机器宝宝", "默认机器人"};

}
