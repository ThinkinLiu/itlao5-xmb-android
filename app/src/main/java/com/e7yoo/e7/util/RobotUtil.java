package com.e7yoo.e7.util;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.Robot;

/**
 * Created by Administrator on 2017/9/6.
 */

public class RobotUtil {

    public static String getString(String text) {
        if(text == null) {
            return "";
        }
        return text;
    }

    public static String getSexText(int sex) {
        switch (sex) {
            case 2:
                return E7App.mApp.getString(R.string.sex_female);
            case 1:
                return E7App.mApp.getString(R.string.sex_male);
            case 0:
            default:
                return E7App.mApp.getString(R.string.sex_unknow);
        }
    }

    public static int getSex(String sex) {
        if(E7App.mApp.getString(R.string.sex_female).equals(sex)) {
            return 2;
        } else if(E7App.mApp.getString(R.string.sex_male).equals(sex)) {
            return 1;
        } else {
            return 0;
        }
    }

    public static String getVoiceText(int voice) {
        switch (voice) {
            case 0:
                return E7App.mApp.getString(R.string.voice_female);
            case 1:
                return E7App.mApp.getString(R.string.voice_male);
            case 2:
                return E7App.mApp.getString(R.string.voice_male1);
            case 3:
                return E7App.mApp.getString(R.string.voice_male2);
            case 4:
            default:
                return E7App.mApp.getString(R.string.voice_children);
        }
    }

    public static int getVoice(String voice) {
        if(E7App.mApp.getString(R.string.voice_female).equals(voice)) {
            return 0;
        } else if(E7App.mApp.getString(R.string.voice_male).equals(voice)) {
            return 1;
        } else if(E7App.mApp.getString(R.string.voice_male1).equals(voice)) {
            return 2;
        } else if(E7App.mApp.getString(R.string.voice_male2).equals(voice)) {
            return 3;
        } else {
            return 4;
        }
    }

    public static int getDefaultIconResId(Robot robot) {
        if(robot != null && E7App.mApp.getString(R.string.mengmeng).equals(robot.getName())) {
            return R.mipmap.icon_mengmeng;
        } else {
            return R.mipmap.icon;
        }
    }
}
