package com.e7yoo.e7.util;

import com.e7yoo.e7.model.PrivateMsg;
import com.e7yoo.e7.model.Robot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/29.
 * 测试|开发类，可用于制造数据或调试，发布时关闭Debug开关即可
 */

public class DebugUtil {

    private static boolean sDebug = false;

    public static void setDebug(boolean debug) {
        sDebug = debug;
    }

    public static boolean isDebug() {
        return sDebug;
    }

    /**
     * sDebug模式有效
     * 产生count条PrivateMsg数据，并将数据放入oldDatas中，数据以send,reply,hint顺序循环返回
     * @param oldDatas
     * @param count
     * @return
     */
    public static void setDatas(List<PrivateMsg> oldDatas, int count, boolean top) {
        if(!isDebug()) {
            return;
        }
        List<PrivateMsg> newDatas = new ArrayList<>();
        for (int i = 0; i < count; i++){
            int index= (i + 1) % 3;
            switch (index) {
                case 0:
                    newDatas.add(new PrivateMsg(0, System.currentTimeMillis(), "i = " + i, "", PrivateMsg.Type.SEND, 0));
                    break;
                case 1:
                    newDatas.add(new PrivateMsg(0, System.currentTimeMillis(), "i = " + i, "", PrivateMsg.Type.REPLY, 0));
                    break;
                case 2:
                default:
                    newDatas.add(new PrivateMsg(0, System.currentTimeMillis(), "i = " + i, "", PrivateMsg.Type.HINT, 0));
                    break;
            }
        }
        if(top) {
            oldDatas.addAll(0, newDatas);
        } else {
            oldDatas.addAll(newDatas);
        }
    }

    /**
     * sDebug模式有效
     * 产生count条Robot数据，并将数据放入oldDatas中，数据以send,reply,hint顺序循环返回
     * @param oldDatas
     * @param count
     * @return
     */
    public static void setRobotDatas(List<Robot> oldDatas, int count, boolean top) {
        if (!isDebug()) {
            return;
        }
        List<Robot> newDatas = new ArrayList<>();
        for (int i = 0; i < count; i++){
            int index= (i + 1) % 3;
            switch (index) {
                case 0:
                    newDatas.add(new Robot("萌萌", ""));
                    break;
                case 1:
                    newDatas.add(new Robot("小萌", ""));
                    break;
                case 2:
                default:
                    newDatas.add(new Robot("哈哈", ""));
                    break;
            }
        }
        if(top) {
            oldDatas.addAll(0, newDatas);
        } else {
            oldDatas.addAll(newDatas);
        }
    }
}
