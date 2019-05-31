package wenba.com.androidtest.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import libcore.io.DiskLruCache;
import libcore.io.FileUtils;
import wenba.com.androidtest.IOUtils;

/**
 * Created by silvercc on 18/1/26.
 */

public class ImageLoader {
    private static final String TAG = ImageLoader.class.getSimpleName();
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 500; //500MB
    private static final int DISK_CACHE_INDEX = 0;
    private static final int IO_BUFFER_SIZE = 1024 * 8;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;
    private static final int BITMAP_URL_TAG = 0;
    private static final int BITMAP_CALLBACK_RESULT = 0;
    private final Context mContext;
    private LruCache<String, Bitmap> mLruCache;
    private DiskLruCache mDiskLruCache;
    private ImageResizer mImageResizer = new ImageResizer();
    private static Handler mUIHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BITMAP_CALLBACK_RESULT:
                    LoaderResult loaderResult = (LoaderResult) msg.obj;
                    Bitmap bitmap = loaderResult.bitmap;
                    ImageView imageView = loaderResult.imageView;
                    String url = loaderResult.url;
                    String bitmapUrl = (String) imageView.getTag(BITMAP_URL_TAG);
                    if (url.equals(bitmapUrl)) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        Log.i(TAG, "url has changed, ignore it!");
                    }
                    break;
            }
        }
    };

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private AtomicInteger atomicInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new Thread(runnable, "ImageLoader#" + atomicInteger.get());
        }
    };

    private static final Executor THREAD_POOL_EXCUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), sThreadFactory);

    public static ImageLoader buildImageLoader(Context context) {
        return new ImageLoader(context);
    }

    private ImageLoader(Context context) {
        mContext = context.getApplicationContext();
        int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;//取当前进程分配的最大内存KB
        int loaderMemory = maxMemory / 8;//当前最大内存的1/8
        mLruCache = new LruCache<String, Bitmap>(loaderMemory) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
        String dirPath = FileUtils.getRootPath(mContext) + File.separator + "images";
        boolean mkDir = FileUtils.mkDir(dirPath);
        if (mkDir) {
            long sdCardAvailaleSize = FileUtils.getSDCardAvailaleSize(dirPath);
            if (sdCardAvailaleSize > DISK_CACHE_SIZE) {
                File file = new File(dirPath);
                try {
                    mDiskLruCache = DiskLruCache.open(file, 1, 1, DISK_CACHE_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addBitmapToMemory(String key, Bitmap bitmap) {
        if (getBitmapFromMemory(key) == null) {
            mLruCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemory(String key) {
        return mLruCache.get(key);
    }

    private Bitmap loadBitmapFromMemory(String key) {
        String urlKey = hashKeyFromUrl(hashKeyFromUrl(key));
        return mLruCache.get(urlKey);
    }

    private Bitmap loadBitmapFromRemote(String bitmapUrl, int reqWidth, int reqHeight) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("can not visit network from UI Thread!");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        DiskLruCache.Editor edit = mDiskLruCache.edit(hashKeyFromUrl(bitmapUrl));
        if (edit != null) {
            OutputStream outputStream = edit.newOutputStream(DISK_CACHE_INDEX);
            if (downloadUrlToStream(bitmapUrl, outputStream)) {
                edit.commit();
            } else {
                edit.abort();
            }
            mDiskLruCache.flush();
        }
        return loadBitmapFromLocal(bitmapUrl, reqWidth, reqHeight);
    }

    private Bitmap loadBitmapFromLocal(String bitmapUrl, int reqWidth, int reqHeight) throws IOException {
        Bitmap bitmap = null;
        String key = hashKeyFromUrl(bitmapUrl);
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
        if (snapshot != null) {
            FileInputStream inputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
            FileDescriptor fd = inputStream.getFD();
            bitmap = mImageResizer.decodeSampleBitmapFromFileDescriptor(fd, reqWidth, reqHeight);
            BitmapFactory.decodeStream(inputStream);
        }
        if (bitmap != null) {
            addBitmapToMemory(key, bitmap);
        }
        return bitmap;
    }

    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        boolean result = false;
        HttpURLConnection httpURLConnection = null;
        BufferedInputStream is = null;
        BufferedOutputStream os = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(httpURLConnection.getInputStream(), IO_BUFFER_SIZE);
            os = new BufferedOutputStream(outputStream);
            int b;
            while ((b = is.read()) != -1) {
                os.write(b);
            }
            result = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            IOUtils.close(is);
            IOUtils.close(os);
        }
        return result;
    }

    private String hashKeyFromUrl(String url) {
        String cacheKey;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = url.getBytes();
            digest.update(bytes);
            cacheKey = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String s = Integer.toHexString(0xFF & bytes[i]);
            if (s.length() == 1) {
                builder.append("0");
            } else {
                builder.append(s);
            }
        }
        return builder.toString();
    }

    /**
     * 在子线程调用
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap loadBitmap(String url, int reqWidth, int reqHeight) {
        Bitmap bitmap = loadBitmapFromMemory(hashKeyFromUrl(url));
        if (bitmap != null) {
            return bitmap;
        }
        try {
            bitmap = loadBitmapFromLocal(url, reqWidth, reqHeight);
            if (bitmap != null) {
                return bitmap;
            } else {
                bitmap = loadBitmapFromRemote(url, reqWidth, reqHeight);
                if (bitmap != null) {
                    return bitmap;
                }
                bitmap = downLoadBitmapFromUrl(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap downLoadBitmapFromUrl(String bitmapUrl) {
        HttpURLConnection urlConnection = null;
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(bitmapUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = urlConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                IOUtils.close(inputStream);
            }
        }
        return bitmap;
    }

    /**
     * 在主线程调用
     * @param bitmapUrl
     * @param imageView
     * @param reqWidth
     * @param reqHeight
     */
    public void bindBitmap(final String bitmapUrl, final ImageView imageView, final int reqWidth, final int reqHeight) {
        //这里setTag的原因是为了对比url，防止出现imageView加载错误的bitmap，导致错位
        imageView.setTag(BITMAP_URL_TAG, bitmapUrl);
        Bitmap bitmap = loadBitmapFromMemory(bitmapUrl);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        //此处用的Callable仅仅为了尝试API，其实使用Runnable就可以，因为已经使用Handler抛给主线程了
        //使用异步的原因是考虑到这个方法的调用通常是在主线程调用的，loadBitmap有可能是一个耗时操作。
        Callable<Bitmap> callable = new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                Bitmap bitmap1 = loadBitmap(bitmapUrl, reqWidth, reqHeight);
                if (bitmap1 != null) {
                    LoaderResult result = new LoaderResult();
                    result.bitmap = bitmap1;
                    result.imageView = imageView;
                    result.url = bitmapUrl;
                    mUIHandler.obtainMessage(BITMAP_CALLBACK_RESULT, result).sendToTarget();
                }
                return bitmap1;
            }
        };
        //下面就是callback当runnable用
        FutureTask<Bitmap> futureTask = new FutureTask<Bitmap>(callable);
        THREAD_POOL_EXCUTOR.execute(futureTask);
        //返回值的获取，可以以下这种用法，但是要注意的是，每次submit，只是执行一个Runnable，如果需要有多个callback执行，需要多个返回值，em...多调用几次吧，或者考虑封装返回值，多个callback做策略
//        ExecutorService service = Executors.newCachedThreadPool(sThreadFactory);
//        Future<Bitmap> submit = service.submit(callable);
//        try {
//            Bitmap bitmap1 = submit.get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }

}
