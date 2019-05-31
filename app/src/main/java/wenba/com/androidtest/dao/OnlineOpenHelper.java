package wenba.com.androidtest.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

/**
 * Created by silvercc on 17/1/3.
 */
public class OnlineOpenHelper extends DaoMaster.OpenHelper {
    public OnlineOpenHelper(Context context, String name) {
        super(context, name);
    }

    public OnlineOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //BBLog call replaced
        MigrationHelper.getInstance().migrate(db, BookDBEntityDao.class);
        MigrationHelper.getInstance().migrate(db, UserDBEntityDao.class);
    }
}
