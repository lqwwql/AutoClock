package com.meteorshower.autoclock.greendao;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meteorshower.autoclock.bean.SqlBean;

import org.greenrobot.greendao.database.Database;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.List;

class MyOpenHelper extends DaoMaster.OpenHelper {
    SoftReference<Context> sr;

    public MyOpenHelper(Context context, String name) {
        super(context, name);
        sr = new SoftReference<>(context);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //手动更新数据库语句
        try(InputStream is = sr.get().getAssets().open("sql_update.json")) {
            String msg = null;
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            msg = new String(bytes);
            if (msg != null){
                Gson gson = new Gson();
                List<SqlBean> sqlList = gson.fromJson(msg,new TypeToken<List<SqlBean>>(){}.getType());
                if (sqlList != null && sqlList.size() > 0){
                    for (SqlBean sqlBean : sqlList){
                        if (oldVersion < sqlBean.getVersion()){
                            for (String s : sqlBean.getSql()){
                                db.execSQL(s);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                    @Override
                    public void onCreateAllTables(Database db, boolean ifNotExists) {
                        DaoMaster.createAllTables(db, ifNotExists);
                    }

                    @Override
                    public void onDropAllTables(Database db, boolean ifExists) {
                        DaoMaster.dropAllTables(db, ifExists);
                    }
                }, ActionModeDao.class);

        //数据库升级时更新数据库语句
        if (oldVersion < 2) {
//            db.execSQL("UPDATE t_RecordSyncInfo SET LAST_SYNC_TIME = '2000-01-01 00:00:00'   WHERE CATEGORY = '4'");
        }
    }
}
