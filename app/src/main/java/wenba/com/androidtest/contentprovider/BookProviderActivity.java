package wenba.com.androidtest.contentprovider;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import wenba.com.androidtest.R;

/**
 * Created by silvercc on 18/1/9.
 */

public class BookProviderActivity extends Activity {
    private static final String AUTHORITIES = "com.wenba.BOOK_PROVIDER";
    private static final Uri BOOK_CONTENT_URI = Uri.parse("content://" + AUTHORITIES + "/book");
    private static final Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHORITIES + "/user");
    private static final String TAG = BookProviderActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentValues bookValues = new ContentValues();
        bookValues.put("name", "book#1");
        getContentResolver().insert(BOOK_CONTENT_URI, bookValues);
//
        ContentValues userValues = new ContentValues();
        userValues.put("name", "user#1");
        userValues.put("gender", "1");
        getContentResolver().insert(USER_CONTENT_URI, userValues);

        Cursor bookCursor = getContentResolver().query(BOOK_CONTENT_URI, new String[]{"_id", "name"}, null, null, null);
        while (bookCursor.moveToNext()) {
            int _id = bookCursor.getInt(0);
            String name = bookCursor.getString(1);
            Log.i(TAG, "book id = " + _id + "; name = " + name);
        }
        bookCursor.close();

        Cursor userCursor = getContentResolver().query(USER_CONTENT_URI, new String[]{"_id", "name", "gender"}, null, null, null);
        while (userCursor.moveToNext()) {
            int _id = userCursor.getInt(0);
            String name = userCursor.getString(1);
            int gender = userCursor.getInt(2);
            Log.i(TAG, "user id = " + _id + "; name = " + name + "; gender = " + gender);
        }
        userCursor.close();
    }
}
