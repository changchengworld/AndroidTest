package libcore.io;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.StatFs;

import java.io.File;
import java.io.IOException;

/**
 * Created by silvercc on 18/1/26.
 */

public class FileUtils {

    public static String getRootPath(Context context) {
        return context.getExternalCacheDir().getAbsolutePath();
    }

    public static boolean createFile(String filePath) {
        boolean result = false;
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                result = file.createNewFile();
            } else if (file.isDirectory()) {
                file.delete();
                result = file.createNewFile();
            } else {
                result = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean mkDir(String dirPath) {
        boolean result;
        File file = new File(dirPath);
        if (!file.exists()) {
            result = file.mkdir();
        } else if (!file.isDirectory()) {
            file.delete();
            result = file.mkdirs();
        } else {
            result = true;
        }
        return result;
    }

    /**
     * 获取磁盘可用空间.
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static long getSDCardAvailaleSize(String dirPath) {
        File path = new File(dirPath);
        StatFs stat = new StatFs(path.getPath());
        long blockSize, availableBlocks;
        if (Build.VERSION.SDK_INT >= 18) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }
        return availableBlocks * blockSize;
    }
}
