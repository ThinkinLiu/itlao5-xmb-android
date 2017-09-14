package com.e7yoo.e7.util;

import android.content.Context;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.PrivateMsg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/9/4.
 */

public class JokeUtil {


    public static PrivateMsg parseJoke(int robotId, String robotName, JSONObject object) {
        PrivateMsg msg = null;
        try {
            if (object != null) {
                if (object.getInt("error_code") == 0) {
                    JSONObject jo = object.optJSONObject("result");
                    if (jo != null) {
                        JSONArray ja = jo.optJSONArray("data");
                        if (ja != null && ja.length() > 0) {
                            JSONObject jo2 = ja.optJSONObject(0);
                            if (jo2 != null) {
                                // String hashId = jo2.optString("hashId");
                                String text = E7App.mApp.getResources().getString(R.string.joke_happy) + "：\n"
                                        + jo2.optString("content");
                                // long unixtime = jo2.optLong("unixtime");
                                // String updatetime =
                                // jo2.optString("updatetime");
                                msg = new PrivateMsg(-1, System.currentTimeMillis(), text, null, PrivateMsg.Type.REPLY, robotId);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(msg == null) {
            msg = new PrivateMsg(1000, System.currentTimeMillis(), E7App.mApp.getString(R.string.joke_parse_error), null, PrivateMsg.Type.REPLY, robotId);
        }
        return msg;
    }
}
