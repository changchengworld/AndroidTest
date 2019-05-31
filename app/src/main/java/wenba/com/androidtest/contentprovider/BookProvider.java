package wenba.com.androidtest.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import wenba.com.androidtest.dao.BookDBEntityDao;
import wenba.com.androidtest.dao.DBManager;
import wenba.com.androidtest.dao.UserDBEntityDao;

/**
 * Created by silvercc on 18/1/9.
 * 除了onCreate方法会运行在主线程之外，其它所有回调都是运行在Binder线程池中的
 * ContentProvider和GreenDao的3.x系列结合并不是很好用，由于ContentProvider里面CRUD方法的限制，
 * 使得GreenDao只能使用SqliteDataBase作为数据库查询工具，无法使用加密，使用ContentProvider跨进程通讯体验很糟糕
 */
public class BookProvider extends ContentProvider {

    private static final String TAG = BookProvider.class.getSimpleName();
    private static final String AUTHORITIES = "com.wenba.BOOK_PROVIDER";
    private static final int BOOK_URI_CODE = 0;
    private static final int USER_URI_CODE = 1;

    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(AUTHORITIES, "book", BOOK_URI_CODE);
        mUriMatcher.addURI(AUTHORITIES, "user", USER_URI_CODE);
    }

    private Context mCtx;
    private DBManager dbManager;

    @Override
    public boolean onCreate() {
        String name = Thread.currentThread().getName();
        int priority = Thread.currentThread().getPriority();
        Log.i(TAG, "thread name = " + name);
        Log.i(TAG, "thread priority = " + priority);
        mCtx = getContext();
//        BaseApplication instance = TestApplication.getInstance();
//        if (instance != null){
            dbManager = DBManager.initialDBManager(mCtx.getApplicationContext());
//        } else {
        if (dbManager == null) {
            Log.i(TAG, "dbManager is null");
        }
//        }
//        dbManager = ((TestApplication) (TestApplication.getInstance())).getDbManager();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        String name = Thread.currentThread().getName();
        int priority = Thread.currentThread().getPriority();
        Log.i(TAG, "thread name = " + name);
        Log.i(TAG, "thread priority = " + priority);
        String tableName = getTableName(uri);
//        DaoFactory daoFactory = new DaoFactory(tableName);
//        AbstractDao dao = daoFactory.getDao();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(tableName);
        return queryBuilder.query(dbManager.getDB(), strings, s, strings1, null, null, s1);
    }

    @Override
    public String getType(Uri uri) {
        Log.i(TAG, "getType");
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.i(TAG, "insert");
        String tableName = getTableName(uri);
        dbManager.getDB().insert(tableName, null, contentValues);
        mCtx.getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        Log.i(TAG, "delete");
        String tableName = getTableName(uri);
        int delete = dbManager.getDB().delete(tableName, s, strings);
        mCtx.getContentResolver().notifyChange(uri, null);
        return delete;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        Log.i(TAG, "update");
        String tableName = getTableName(uri);
        int update = dbManager.getDB().update(tableName, contentValues, s, strings);
        mCtx.getContentResolver().notifyChange(uri, null);
        return update;
    }

    private String getTableName(Uri uri) {
        String tableName = "";
        int match = mUriMatcher.match(uri);
        switch (match){
            case BOOK_URI_CODE:
                tableName = BookDBEntityDao.TABLENAME;
                break;
            case USER_URI_CODE:
                tableName = UserDBEntityDao.TABLENAME;
                break;
            default:
                break;
        }
        return tableName;
    }
}
