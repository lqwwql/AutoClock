package com.meteorshower.autoclock.util;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {
    private static final String TAG = "ThreadPoolManager";
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE * 2 + 1 ;
    //单个线程如果闲置会存活60秒
    private static final int KEEP_ALIVE = 60;

    private ThreadPoolExecutor mExcutor;
    private ThreadPoolManager() {
        mExcutor = new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,KEEP_ALIVE,
                TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(128), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardPolicy());//超出 maximumpollsize 处理策略 抛出异常
    }

    public static ThreadPoolManager getInstance(){
        return SingletonHolder.sInstance;
    }

    public void execute(@NonNull String userName, Runnable runnable){
        if(runnable == null){
            return;
        }
        mExcutor.execute(runnable);
        Log.i(TAG," userName="+userName);
    }

    public void remove(Runnable runnable){
        if(runnable == null){
            return;
        }
        mExcutor.remove(runnable);
    }

    private static class SingletonHolder{
        private static ThreadPoolManager sInstance = new ThreadPoolManager();
    }
}
