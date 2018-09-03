package com.e7yoo.e7.util;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.Joke;
import com.e7yoo.e7.model.PrivateMsg;
import com.e7yoo.e7.model.feed;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/4.
 */

public class JokeUtil {


    public static PrivateMsg parseJoke(int robotId, String robotName, JSONObject object) {
        PrivateMsg msg = null;
        try {
            if (object != null) {
                String content = null;
                if(object.has("showapi_res_code")) {
                    if (object.getInt("showapi_res_code") == 0) {
                        JSONObject jo = object.optJSONObject("showapi_res_body");
                        if (jo != null) {
                            JSONArray ja = jo.optJSONArray("contentlist");
                            if (ja != null && ja.length() > 0) {
                                JSONObject jo2 = ja.optJSONObject(0);
                                if (jo2 != null) {
                                    content = jo2.getString("title") + "\n\n" + jo2.getString("text");
                                }
                            }
                        }
                    }
                } else if (object.getInt("error_code") == 0) {
                    JSONObject jo = object.optJSONObject("result");
                    if (jo != null) {
                        JSONArray ja = jo.optJSONArray("data");
                        if (ja != null && ja.length() > 0) {
                            JSONObject jo2 = ja.optJSONObject(0);
                            if (jo2 != null) {
                                // String hashId = jo2.optString("hashId");
                                content = jo2.optString("content");
                                // long unixtime = jo2.optLong("unixtime");
                                // String updatetime =
                                // jo2.optString("updatetime");
                            }
                        }
                    }
                }
                if (content != null) {
                    String text = E7App.mApp.getResources().getString(R.string.joke_happy) + "：\n"
                            + content;
                    msg = new PrivateMsg(-1, System.currentTimeMillis(), text, null, PrivateMsg.Type.REPLY, robotId);
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

    public static PrivateMsg parseBmobJoke(int robotId, String robotName, feed object) {
        PrivateMsg msg = null;
        if (object != null) {
            String content = object.getTitle();
            if(object.getContent() != null && object.getContent().length() > 0) {
                if(content != null && content.length() > 0) {
                    content = content + "\n\n" + object.getContent();
                } else {
                    content = object.getContent();
                }
            }
            String url = object.getImg();
            if(url == null || url.length() == 0) {
                url = null;
            }
            if(url != null && url.trim().length() > 0) {
                String text = E7App.mApp.getResources().getString(R.string.joke_happy_pic) + content;
                msg = new PrivateMsg(-2, System.currentTimeMillis(), text, url, PrivateMsg.Type.REPLY, robotId);
            } else if (content != null && content.length() > 0) {
                String text = E7App.mApp.getResources().getString(R.string.joke_happy) + "：\n" + content;
                msg = new PrivateMsg(-1, System.currentTimeMillis(), text, url, PrivateMsg.Type.REPLY, robotId);
            }
        }
        if(msg == null) {
            msg = new PrivateMsg(1000, System.currentTimeMillis(), E7App.mApp.getString(R.string.joke_parse_error), null, PrivateMsg.Type.REPLY, robotId);
        }
        return msg;
    }

    public static PrivateMsg parseJokeRand(int robotId, String robotName, JSONObject object, boolean isPic) {
        PrivateMsg msg = null;
        try {
            if (object != null) {
                String text = null;
                String url = null;
                if(object.has("showapi_res_code")) {
                    if (object.getInt("showapi_res_code") == 0) {
                        JSONObject jo = object.optJSONObject("showapi_res_body");
                        if (jo != null) {
                            JSONArray ja = jo.optJSONArray("contentlist");
                            if (ja != null && ja.length() > 0) {
                                JSONObject jo2 = ja.optJSONObject(0);
                                if(jo2 != null) {
                                    // String hashId = jo2.optString("hashId");
                                    text = "：\n" + jo2.optString("title");
                                    if(jo2.has("text")) {
                                        text = text + "\n\n" + jo2.optString("text");
                                    }
                                    // long unixtime = jo2.optLong("unixtime");
                                    url = jo2.optString("img");
                                }
                            }
                        }
                    }
                } else if (object.getInt("error_code") == 0) {
                    JSONArray ja = object.optJSONArray("result");
                    if (ja != null && ja.length() > 0) {
                        JSONObject jo2 = ja.optJSONObject(0);
                        if (jo2 != null) {
                            // String hashId = jo2.optString("hashId");
                            text = "：\n" + jo2.optString("content");
                            // long unixtime = jo2.optLong("unixtime");
                            url = jo2.optString("url");
                        }
                    }
                }
                if(text != null) {
                    if(isPic && url != null && url.trim().length() > 0) {
                        text = E7App.mApp.getResources().getString(R.string.joke_happy_pic) + text;
                        msg = new PrivateMsg(-2, System.currentTimeMillis(), text, url, PrivateMsg.Type.REPLY, robotId);
                    } else {
                        text = E7App.mApp.getResources().getString(R.string.joke_happy) + text;
                        msg = new PrivateMsg(-1, System.currentTimeMillis(), text, url, PrivateMsg.Type.REPLY, robotId);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(msg == null) {
            if(isPic) {
                msg = new PrivateMsg(1000, System.currentTimeMillis(), E7App.mApp.getString(R.string.joke_parse_error), null, PrivateMsg.Type.REPLY, robotId);
            } else {
                msg = new PrivateMsg(1000, System.currentTimeMillis(), E7App.mApp.getString(R.string.joke_rand_parse_error), null, PrivateMsg.Type.REPLY, robotId);
            }
        }
        return msg;
    }

    public static ArrayList<Joke> parseJokeRand(JSONObject object) {
        try {
            if (object != null) {
                if(object.has("showapi_res_code")) {
                    if (object.getInt("showapi_res_code") == 0) {
                        JSONObject jo = object.optJSONObject("showapi_res_body");
                        if (jo != null) {
                            JSONArray ja = jo.optJSONArray("contentlist");
                            if (ja != null && ja.length() > 0) {
                                ArrayList<Joke> list = new ArrayList<>();
                                JSONObject joI;
                                Joke joke;
                                for(int i = 0; i < ja.length(); i++) {
                                    joI = ja.optJSONObject(i);
                                    joke = new Joke();
                                    String content = "";
                                    if(joI.has("text")) {
                                        content = "\n\n" + joI.optString("text").replaceAll("<br />", "").replaceAll("<br/>", "");
                                    }
                                    joke.setContent(joI.optString("title") + content);
                                    if(joI.has("img")) {
                                        joke.setUrl(joI.optString("img"));
                                    }
                                    joke.setUpdatetime(joI.optString("ct"));
                                    //joke.setUnixtime(joI.optInt());
                                    list.add(joke);
                                }
                                return list;
                            }
                        }
                    }
                } else if (object.getInt("error_code") == 0) {
                    JSONArray ja = object.optJSONArray("result");
                    if (ja != null && ja.length() > 0) {
                        ArrayList<Joke> list = new ArrayList<>();
                        Gson gson = new Gson();
                        for(int i = 0; i < ja.length(); i++) {
                            list.add(gson.fromJson(ja.optString(i), Joke.class));
                        }
                        return list;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
