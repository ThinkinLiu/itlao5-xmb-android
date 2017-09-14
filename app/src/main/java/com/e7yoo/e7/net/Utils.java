package com.e7yoo.e7.net;

/**
 * Created by Administrator on 2017/8/30.
 */

public class Utils {

    public static String replaceTianqi(String text) {
        if(text != null && text.contains("月") && text.contains("周") && text.contains("°") && text.contains("号") && text.contains("-")) {
            text = text.replace(":", ":\n").replace(";", ";\n").replaceFirst("°", "° 当前温度");
        }
        return text;
    }
}
