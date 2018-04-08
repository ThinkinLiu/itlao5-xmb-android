package com.e7yoo.e7.sql;

import android.content.Context;

import com.e7yoo.e7.model.PrivateMsg;
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

    public void execute(Runnable runnable) {
        singlePool.execute(runnable);
    }
}
