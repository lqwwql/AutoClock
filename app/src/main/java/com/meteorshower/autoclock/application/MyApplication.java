package com.meteorshower.autoclock.application;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;

/**
 * 程序application
 */
public class MyApplication extends Application {

    private static Context mContext;//程序上下文
    private static int mMainThreadId;//主线程ID
    private static Handler mHandler;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mMainThreadId = Process.myTid();
        mHandler = new Handler();
    }

    public static Context getContext() {
        return mContext;
    }

    public static int getMainThreadId() {
        return mMainThreadId;
    }

    public static Handler getHandler(){
        return mHandler;
    }
}
