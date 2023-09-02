package com.meteorshower.autoclock.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>文件描述：<p>
 * <p>作者：asus<p>
 * <p>创建时间：2020/3/25<p>
 */
public class CrashLogCatchUtils implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashLogCatchUtils";
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static  CrashLogCatchUtils mInstance;
    private Context mContext;
    private Map<String, String> mLogInfo = new HashMap();
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
    private String logFilePath = "";

    private CrashLogCatchUtils() {
    }

    public static CrashLogCatchUtils getInstance() {
        if (null == mInstance) {
            mInstance = new  CrashLogCatchUtils();
        }

        return mInstance;
    }

    public void init(Context var1, String var2) {
        this.mContext = var1;
        this.logFilePath = var2;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread var1, Throwable var2) {
        if (!this.handleException(var2) && this.mDefaultHandler != null) {
            this.mDefaultHandler.uncaughtException(var1, var2);
        } else {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException var4) {
                var4.printStackTrace();
            }

            Process.killProcess(Process.myPid());
            System.exit(1);
        }

    }

    public boolean handleException(Throwable var1) {
        if (var1 == null) {
            return false;
        } else {
            (new Thread() {
                public void run() {
                    Looper.prepare();
                    Looper.loop();
                }
            }).start();
            this.getDeviceInfo(this.mContext);
            this.saveCrashLogToFile(var1);
            return true;
        }
    }

    public void getDeviceInfo(Context var1) {
        try {
            PackageManager var2 = var1.getPackageManager();
            PackageInfo var3 = var2.getPackageInfo(var1.getPackageName(), 1);
            if (var3 != null) {
                String var4 = var3.versionName == null ? "null" : var3.versionName;
                String var5 = var3.versionCode + "";
                this.mLogInfo.put("versionName", var4);
                this.mLogInfo.put("versionCode", var5);
            }
        } catch (PackageManager.NameNotFoundException var10) {
            var10.printStackTrace();
        }

        Field[] var11 = Build.class.getDeclaredFields();
        Field[] var12 = var11;
        int var13 = var11.length;

        for(int var14 = 0; var14 < var13; ++var14) {
            Field var6 = var12[var14];

            try {
                var6.setAccessible(true);
                this.mLogInfo.put(var6.getName(), var6.get("").toString());
                Log.d("CrashLogCatchUtils", var6.getName() + ":" + var6.get(""));
            } catch (IllegalArgumentException var8) {
                var8.printStackTrace();
            } catch (IllegalAccessException var9) {
                var9.printStackTrace();
            }
        }

    }

    private String saveCrashLogToFile(Throwable var1) {
        StringBuffer var2 = new StringBuffer();
        Iterator var3 = this.mLogInfo.entrySet().iterator();

        String var6;
        while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            String var5 = (String)var4.getKey();
            var6 = (String)var4.getValue();
            var2.append(var5 + "=" + var6 + "\r\n");
        }

        StringWriter var13 = new StringWriter();
        PrintWriter var14 = new PrintWriter(var13);
        var1.printStackTrace(var14);
        var1.printStackTrace();

        for(Throwable var15 = var1.getCause(); var15 != null; var15 = var15.getCause()) {
            var15.printStackTrace(var14);
            var14.append("\r\n");
        }

        var14.close();
        var6 = var13.toString();
        var2.append(var6);
        String var7 = this.mSimpleDateFormat.format(new Date());
        String var8 = "CrashLog-" + var7 + ".txt";
        if (Environment.getExternalStorageState().equals("mounted")) {
            try {
                if (null != this.mContext) {
                    File var9 = new File(this.logFilePath);
                    if (!var9.exists()) {
                        var9.mkdirs();
                    }

                    FileOutputStream var10 = new FileOutputStream(var9 + "/" + var8);
                    var10.write(var2.toString().getBytes());
                    var10.close();
                    return var8;
                }
            } catch (FileNotFoundException var11) {
                var11.printStackTrace();
            } catch (IOException var12) {
                var12.printStackTrace();
            }
        }

        return null;
    }
}
