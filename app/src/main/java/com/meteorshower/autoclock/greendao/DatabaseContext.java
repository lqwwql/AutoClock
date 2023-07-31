package com.meteorshower.autoclock.greendao;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.util.IOUtils;

import java.io.File;
/**
 * 用于自定义greendao数据库路径
 * */
public class DatabaseContext extends ContextWrapper {

    private Context mContext;

    public DatabaseContext(Context base) {
        super(base);
        mContext = base;
    }

    @Override
    public File getDatabasePath(String name) {
        String myPath = IOUtils.getRootStoragePath(mContext) + AppConstant.DB_DIR + File.separator + AppConstant.DB_NAME;
        File file = new File(myPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
            }
        }
        return file;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }

    /**
     * Android 4.0会调用此方法获取数据库。
     *
     * @param name
     * @param mode
     * @param factory
     * @param errorHandler
     * @see ContextWrapper#openOrCreateDatabase(String,
     * int,
     * SQLiteDatabase.CursorFactory,
     * DatabaseErrorHandler)
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }
}
