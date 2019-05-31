package wenba.com.androidtest.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by silvercc on 18/1/9.
 */

public class DBManager {
    private static final String DB_NAME = "test-db";
    public static final boolean ENCRYPTED = true;
    //此参数为安全考虑后续考虑动态生产
    public static final String PASSWORD = "super-secret";
    private static volatile DBManager mInstance;
    private DaoSession daoSession;
    private Context mCtx;
    private SQLiteDatabase db;

    private DBManager(Context ctx) {
        mCtx = ctx;
        initDB();
    }

    private void initDB() {
        DaoMaster.OpenHelper openHelper = new DaoMaster.DevOpenHelper(mCtx, ENCRYPTED ? DB_NAME + "-encrypted" : DB_NAME);
        db = openHelper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();
    }

    public static DBManager initialDBManager(Context ctx) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(ctx);
                }
            }
        }
        return mInstance;
    }

    public static DBManager getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("Have you initialized this class when your Application was created?");
        }
        return mInstance;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void execSQL(String sql){
        db.execSQL(sql);
    }

    public SQLiteDatabase getDB(){
        return db;
    }
}
