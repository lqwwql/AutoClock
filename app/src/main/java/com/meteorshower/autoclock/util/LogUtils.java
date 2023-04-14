package com.meteorshower.autoclock.util;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.meteorshower.autoclock.application.MyApplication;
import com.meteorshower.autoclock.constant.AppConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import retrofit2.Call;
import retrofit2.Response;

public class LogUtils {

    private Context mContext = null;
    private String logPath = "";
    private static String mTag = "LIGHT";
    private DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat(
            "HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private Queue<String> mLogMessageQueue = new LinkedList<>();//日志队列
    private boolean openWrite = true;
    private boolean isRunning = false;


    public LogUtils setTag(String tag) {
        mTag = tag;
        return SingletonHolder.sInstance;
    }

    private LogUtils(Context context) {
        mContext = context;
        logPath = IOUtils.getRootStoragePath(mContext) + AppConstant.DIR_LOG + File.separator;
        String path1 = logPath + "info";
        String path2 = logPath + "error";
        File file1 = new File(path1);
        File file2 = new File(path2);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        if (!file2.exists()) {
            file2.mkdirs();
        }

    }

    public static LogUtils getInstance() {
        return SingletonHolder.sInstance;
    }

    public void write(final byte[] contents, final int isError) throws Exception {
        ThreadPoolManager.getInstance().execute("LogUtils", new Runnable() {
            @Override
            public void run() {
                String mDay = DAY_FORMAT.format(new Date());
                String path = logPath + (isError == 1 ? "error" : "info") + File.separator + mDay + ".txt";
                try {
                    File file = new File(path);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fouts = new FileOutputStream(file, true);
                    fouts.write(contents);
                    fouts.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public synchronized void i(String logStr) {
        if (StringUtils.isEmptyOrNull(logStr)) {
            return;
        }
        Log.e(mTag, "LogUtils i: " + logStr);
        try {
            String timeStamp = TIMESTAMP_FORMAT.format(new Date());
            String content = "[" + timeStamp + "] [" + mTag + "] " + logStr
                    + System.getProperty("line.separator");
            write(content.getBytes("UTF-8"), 0);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void e(final String logStr) {
        if (StringUtils.isEmptyOrNull(logStr)) {
            return;
        }
        Log.e(mTag, "LogUtils e: " + logStr);

        Date date = new Date();
        String timeStamp = TIMESTAMP_FORMAT.format(date);
        String content = "[" + timeStamp + "] " + "[" + date.getTime() + "]" + logStr
                + System.getProperty("line.separator");
        try {
            write(content.getBytes("UTF-8"), 1);
        } catch (Exception e) {
        }
    }


    public synchronized void logResponse(String str, Response response) {
        if (response == null || response.body() == null) {
            e(str + " response == null || response.body() == null ");
            return;
        }
        try {
            String message = str + " code:" + response.code() + " message:" + new Gson().toJson(response.body());
            e(message);
        } catch (Exception e2) {
        }
    }

    public synchronized void logCall(String str, Call call, Throwable throwable) {
        String message = "";
        try {
            if (call != null) {
                message += "url：" + call.request().url();
            }
            if (throwable != null) {
                message += " error:" + throwable.getMessage();
            }
            message += str + message;
            e(message);
        } catch (Exception e2) {
        }
    }

    private static class SingletonHolder {
        private static LogUtils sInstance = new LogUtils(MyApplication.getContext());
    }
}
