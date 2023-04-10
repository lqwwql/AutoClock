package com.meteorshower.autoclock.util;

import android.content.Context;
import android.util.Log;
import com.meteorshower.autoclock.application.MyApplication;
import com.meteorshower.autoclock.constant.AppConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 打印log文件
 */
public class LogUtils {
    private Context mContext = null;
    private String logPath = "";
    private static String mTag = "LIGHT";
    private DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat(
            "HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public LogUtils setTag(String tag) {
        mTag = tag;
        return SingletonHolder.sInstance;
    }

    private LogUtils(Context context) {
        mContext = context;
        logPath = IOUtils.getRootStoragePath(mContext) + AppConstant.LOG + File.separator;
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

    public synchronized void write(byte[] contents, int isError) throws Exception {
        String mDay = DAY_FORMAT.format(new Date());
        String path = logPath + (isError == 1 ? "error" : "info") + File.separator + mDay + "_" + mTag  + ".txt";
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fouts = new FileOutputStream(file, true);
        fouts.write(contents);
        fouts.close();
    }

    public synchronized void e(String logStr, int errorORinfo) {
        if (StringUtils.isEmptyOrNull(logStr)) {
            return;
        }
        Log.e(mTag, logStr);
        try {
            String timeStamp = TIMESTAMP_FORMAT.format(new Date());
            String content = timeStamp + logStr
                    + System.getProperty("line.separator");
            write(content.getBytes("UTF-8"), errorORinfo);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private static class SingletonHolder {
        private static LogUtils sInstance = new LogUtils(MyApplication.getContext());
    }

    public void destroy() {
    }
}
