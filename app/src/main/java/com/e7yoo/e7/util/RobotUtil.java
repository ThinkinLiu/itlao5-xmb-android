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

    public static int getDefaultIconResId(Robot robot) {
        if(robot != null && E7App.mApp.getString(R.string.mengmeng).equals(robot.getName())) {
            return R.mipmap.icon_mengmeng;
        } else {
            return R.mipmap.icon;
        }
    }
}
