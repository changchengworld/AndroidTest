package wenba.com.androidtest;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

/**
 * Created by silvercc on 18/1/9.
 */

public class AppUtils {
    public static final String getProcessName(Context context) {
        String processName = null;

        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;
                    break;
                }
            }

            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
