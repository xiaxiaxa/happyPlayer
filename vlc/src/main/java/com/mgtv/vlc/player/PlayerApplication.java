package com.mgtv.vlc.player;



import com.xw.helper.utils.MyApplication;

import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;

public class PlayerApplication extends MyApplication {
    public static DbManager mDBManager;

    @Override
    public void onCreate() {
        super.onCreate();
        DbManager.DaoConfig dbConfig = new DbManager.DaoConfig().setDbDir(new File(ConstData.DB_DIRECTORY))
                .setDbName(ConstData.DB_NAME).setDbVersion(ConstData.DB_VERSION).setAllowTransaction(true)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        db.getDatabase().enableWriteAheadLogging();
                    }
                }).setDbUpgradeListener(null);
        if (null == mDBManager)
            mDBManager = x.getDb(dbConfig);
        mDBManager = x.getDb(dbConfig);
    }
}
