package wenba.com.androidtest;

import android.util.Log;

import wenba.com.androidtest.comprehensive.CrashHandler;

/**
 * Created by silvercc on 18/1/9.
 */

public class TestApplication extends BaseApplication {

    private static final String TAG = TestApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        if (inMainProcess()) {
            CrashHandler.getInstance().init(this);
        }
    }

    @Override
    public boolean isDebug() {
        return true;
    }

    private String getProcessName() {
        return AppUtils.getProcessName(this);
    }

    private boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = AppUtils.getProcessName(this);
        Log.i(TAG, "processName : " + processName);
        return packageName.equals(processName);
    }
}
