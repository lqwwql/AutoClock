package com.meteorshower.autoclock.greendao;

import android.util.Log;

import com.meteorshower.autoclock.application.MyApplication;
import com.meteorshower.autoclock.constant.AppConstant;

import org.greenrobot.greendao.database.Database;

/**
 * GreenDao管理类
 */
public class GreenDaoManager {

    private static final String TAG = "GreenDaoManager";
    //创建的数据库是否需要加密，默认创建不加密数据库
    private boolean isDbEncrypted = false;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private GreenDaoManager() {
        init();
    }

    /**
     * 静态内部类，实例化对象使用
     */
    private static class SingleInstanceHolder {
        private static final GreenDaoManager INSTANCE = new GreenDaoManager();
    }

    /**
     * 对外唯一实例的接口
     *
     * @return
     */
    public static GreenDaoManager getInstance() {
        return SingleInstanceHolder.INSTANCE;
    }

    /**
     * 初始化数据
     */
    private void init() {
        try {
            if (isDbEncrypted) {
                //创建加密数据库
                DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(MyApplication.getContext(),  AppConstant.DB_NAME);
                //创建的数据库密码默认为包名，可以自行修改
                Database db = helper.getEncryptedWritableDb(MyApplication.getContext().getPackageName());
                mDaoMaster = new DaoMaster(db);
            } else {
                // 创建不加密的数据库
                MyOpenHelper myOpenHelper = new MyOpenHelper(new DatabaseContext(MyApplication.getContext()), AppConstant.DB_NAME);
                mDaoMaster = new DaoMaster(myOpenHelper.getWritableDatabase());
            }
            mDaoSession = mDaoMaster.newSession();
        } catch (Exception e) {
            Log.e(AppConstant.TAG, "greendao manager error e="+Log.getStackTraceString(e));
            //如果已有未加密的数据库存在，先对这个数据库做一次加密，完成后再次初始化
        } finally {
            Log.e(AppConstant.TAG, "greendao manager finally");
        }

    }

    public DaoMaster getDaoMaster() {
        return mDaoMaster;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

}
