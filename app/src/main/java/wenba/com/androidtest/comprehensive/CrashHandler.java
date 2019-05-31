package wenba.com.androidtest.comprehensive;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by silvercc on 18/1/29.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = CrashHandler.class.getSimpleName();
    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;
    private static Context mCtx;
    private static CrashHandler mHandler = new CrashHandler();
    private static final String TRACEPATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "crash" + File.separator + "logs";
    private static final String FILE_NAME = "crash";
    private static final String FILE_SUFFIX = ".trace";

    public void init(Context context) {
        mCtx = context.getApplicationContext();
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CrashHandler getInstance() {
        return mHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.i(TAG, "uncaughtException");
        try {
            dumpExceptionToSDcard(throwable);
            uploadLogToRemote();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throwable.printStackTrace();
        if (defaultUncaughtExceptionHandler != null) {
            defaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    private void uploadLogToRemote() {
        Log.i(TAG, "uploadLogToRemote");
    }

    private void dumpExceptionToSDcard(Throwable throwable) throws IOException {
        Log.i(TAG, "dumpExceptionToSDcard");
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        File dir = new File(TRACEPATH);
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
        }

        long l = System.currentTimeMillis();
        String time = new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date(l));
        File file = null;
        Log.i(TAG, "TRACEPATH = " + TRACEPATH);
        if (new File(TRACEPATH).exists() && new File(TRACEPATH).isDirectory()) {
            file = new File(TRACEPATH + File.separator + FILE_NAME + time + FILE_SUFFIX);
            if (!file.exists()) {
                boolean newFile = file.createNewFile();
                Log.i(TAG, "new File = " + newFile);
                Log.i(TAG, "File path = " + file.getAbsolutePath());
            }
        }

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.println(time);
            dumpPhoneInfo(writer);
            writer.println();
            throwable.printStackTrace(writer);
            writer.close();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void dumpPhoneInfo(PrintWriter writer) throws PackageManager.NameNotFoundException {
        Log.i(TAG, "dumpPhoneInfo");
        PackageManager packageManager = mCtx.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(mCtx.getPackageName(), PackageManager.GET_ACTIVITIES);
        writer.print("APP Version: ");
        writer.print(packageInfo.versionCode);
        writer.print("_");
        writer.println(packageInfo.versionName);

        //Android系统版本
        writer.print("OS Version: ");
        writer.print(Build.VERSION.RELEASE);
        writer.print("_");
        writer.println(Build.VERSION.SDK_INT);

        //手机制造商
        writer.print("Vendor: ");
        writer.println(Build.MANUFACTURER);

        //手机型号
        writer.print("Model: ");
        writer.println(Build.MODEL);

        //CPU架构
        writer.print("CPU: ");
        writer.println(Build.CPU_ABI);
    }
}
