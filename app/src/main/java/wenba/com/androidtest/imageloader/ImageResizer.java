package wenba.com.androidtest.imageloader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * Created by silvercc on 18/1/26.
 * 提供压缩图片方法，注意，是压缩，也就是把图片变小
 */

public class ImageResizer {
    private static final String TAG = ImageResizer.class.getSimpleName();

    public ImageResizer() {
    }

    /**
     * 从资源文件ID生成对应Bitmap
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//设置为true后，只加载图片宽高信息，是一个轻量级方法
        BitmapFactory.decodeResource(res, resId, options);//第一次加载，只是为了获取图片宽高
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;//改成false，这样才能确保真正加载到Bitmap
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 从FileInputStream获取FileDescriptor生成对应Bitmap
     * @param fd
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap decodeSampleBitmapFromFileDescriptor(FileDescriptor fd, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    /**
     * 根据需求的宽高计算inSampleSize
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (reqWidth <= 0 || reqHeight <= 0) {
            return 1;
        }
        int inSampleSize = 1;//默认不缩放
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        Log.i(TAG, "origin size: width = " + outWidth + "; height = " + outHeight);
        if (outWidth > reqWidth || outHeight > reqHeight) {
            int halfWidth = outWidth / 2;
            int halfHeight = outHeight / 2;
            while ((halfWidth / inSampleSize) >= reqWidth && (halfHeight / inSampleSize) >= reqHeight) {
                inSampleSize *= 2;//官方文档要求inSampleSize必须为2的指数，也就是1，2，4，8，16，如果给3，会向下匹配，也就是2，图片的宽高会除2
            }
        }
        return inSampleSize;
    }
}
