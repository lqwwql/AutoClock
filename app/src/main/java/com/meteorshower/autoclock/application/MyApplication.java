package com.meteorshower.autoclock.application;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.view.Gravity;

import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.greendao.GreenDaoManager;
import com.meteorshower.autoclock.util.CrashLogCatchUtils;
import com.meteorshower.autoclock.util.IOUtils;
import com.meteorshower.autoclock.util.UIUtils;

import java.io.File;

/**
 * 程序application
 */
public class MyApplication extends Application {

    private static Context mContext;//程序上下文
    private static int mMainThreadId;//主线程ID
    private static Handler mHandler;
    public static MyApplication context;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mMainThreadId = Process.myTid();
        mHandler = new Handler();
        // 初始化 Toast 框架
        Toaster.init(this);
        GreenDaoManager.getInstance();
        initCrashUtil();
    }

    private void initCrashUtil(){
        CrashLogCatchUtils.getInstance().init(this, IOUtils.getRootStoragePath(mContext) + AppConstant.DIR_LOG);
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
