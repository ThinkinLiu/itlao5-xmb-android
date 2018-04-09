package com.e7yoo.e7.sql;

import android.content.Context;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.MainActivity;
import com.e7yoo.e7.model.PrivateMsg;
import com.e7yoo.e7.model.PushMsg;
import com.e7yoo.e7.util.Constant;
import com.e7yoo.e7.util.PreferenceUtil;
import com.e7yoo.e7.util.ShortCutUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/9/4.
 */

public class DbThreadPool {
    private ExecutorService singlePool;
    private static DbThreadPool instance;

    private DbThreadPool() {
        singlePool = Executors.newSingleThreadExecutor();
    }

    public static DbThreadPool getInstance() {
        if(instance == null) {
            synchronized (DbThreadPool.class) {
                if(instance == null) {
                    instance = new DbThreadPool();
                }
            }
        }
        return instance;
    }

    public void insertPushMsg(final PushMsg pushMsg, final boolean hasExtras) {
        execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MessageDbHelper.getInstance(E7App.mApp).insertPushMsg(pushMsg, hasExtras);

                    int unRead = PreferenceUtil.getInt(Constant.PREFERENCE_PUSH_MSG_UNREAD, 0);
                    PreferenceUtil.commitInt(Constant.PREFERENCE_PUSH_MSG_UNREAD, ++unRead);
                    ShortCutUtils.addNumShortCut(E7App.mApp, MainActivity.class, true, String.valueOf(unRead));
                } catch (Throwable e) {
                    CrashReport.postCatchedException(e);
                }
            }
        });
    }

    public void insert(final Context context, final PrivateMsg msg) {
        execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MessageDbHelper.getInstance(context).insertMessageInfo(msg);
                } catch (Throwable e) {
                    CrashReport.postCatchedException(e);
                }
            }
        });
    }

    public void delete(final Context context, final long time) {
        execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MessageDbHelper.getInstance(context).deleteMessageInfo(time);
                } catch (Throwable e) {
                    e.printStackTrace();
                    CrashReport.postCatchedException(e);
                }
            }
        });
    }

    public void delete(final Context context, final ArrayList<Long> times) {
        execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MessageDbHelper.getInstance(context).deleteMessageInfo(times);
                } catch (Throwable e) {
                    e.printStackTrace();
                    CrashReport.postCatchedException(e);
                }
            }
        });
    }

    public void deleteByRobotId(final Context context, final int robotId) {
        execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MessageDbHelper.getInstance(context).deleteMessageInfoByRobotId(robotId);
                } catch (Throwable e) {
                    e.printStackTrace();
                    CrashReport.postCatchedException(e);
                }
            }
        });
    }

    public void execute(Runnable runnable) {
        singlePool.execute(runnable);
    }
}
