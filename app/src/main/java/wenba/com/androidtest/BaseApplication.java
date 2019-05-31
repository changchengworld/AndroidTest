package wenba.com.androidtest;

import android.app.Application;

/**
 * Created by silvercc on 18/1/9.
 */

public abstract class BaseApplication extends Application {
    private static BaseApplication mApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static BaseApplication getInstance() {
        return mApplication;
    }

    public abstract boolean isDebug();
}
