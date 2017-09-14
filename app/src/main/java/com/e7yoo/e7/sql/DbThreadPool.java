package com.e7yoo.e7.sql;

import android.content.Context;

import com.e7yoo.e7.model.PrivateMsg;

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
                MessageDbHelper.getInstance(context).insertMessageInfo(msg);
            }
        });
    }

    public void execute(Runnable runnable) {
        singlePool.execute(runnable);
    }
}
