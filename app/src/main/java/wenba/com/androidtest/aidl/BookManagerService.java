package wenba.com.androidtest.aidl;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import wenba.com.androidtest.Book;
import wenba.com.androidtest.IBookManager;
import wenba.com.androidtest.INewBookArriveListener;

/**
 * Created by silvercc on 18/1/8.
 */

public class BookManagerService extends Service {

    private static final String TAG = BookManagerService.class.getSimpleName();

    private AtomicBoolean isTaskRun = new AtomicBoolean(true);
    private CopyOnWriteArrayList<Book> mList = new CopyOnWriteArrayList<>();
    //这里用到了监听者模式，但是由于进程间通讯对象的传递都是经过序列化的，所以即使对象的内容一样，却不是一个
    //对象，因此系统提供了支持跨进程的监听方式RemoteCallbackList。RemoteCallbackList这个类内部有一个Map集合
    //key是对应的listener的IBinder对象，value的CallBack包装了listener。由于客户端使用的Binder对应到服务端都是
    //一类Binder，所以可以用一个Map集合标记类型存储，移除的时候通过反序列化拿到对象的标示就可以成功移除了
    private RemoteCallbackList<INewBookArriveListener> mNewBookArriveListenerList = new RemoteCallbackList<>();

    //IBinder中的方法运行在服务端的线程池里，所以天然可以做耗时操作，客户端调用的时候要小心，如果
    //服务端的方法是耗时的，客户端调用的时候不要在主线程调用
    private IBinder bookManager = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mList.add(book);
            Log.i(TAG, "add book: " + book);
        }

        @Override
        public void registerINewBookArriveListener(INewBookArriveListener listener) throws RemoteException {
//            if (!mNewBookArriveListenerList.contains(listener)) {
//                mNewBookArriveListenerList.add(listener);
//            }
//            Log.i(TAG, "registerINewBookArriveListener current size: "+mNewBookArriveListenerList.size());
            mNewBookArriveListenerList.register(listener);
        }

        @Override
        public void unregisterINewBookArriveListener(INewBookArriveListener listener) throws RemoteException {
//            if (mNewBookArriveListenerList.contains(listener)) {
//                mNewBookArriveListenerList.remove(listener);
//            }
//            Log.i(TAG, "unregisterINewBookArriveListener current size: "+mNewBookArriveListenerList.size());
            mNewBookArriveListenerList.unregister(listener);
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Log.i(TAG, "onTransact thread = "+ (Looper.myLooper() == Looper.getMainLooper()));
            //为了不让任意客户端都可以调用我们的服务，所以进行校验，同onBind里校验，区别在于onTransact方法运行在服务端Binder线程池里
            //1.权限校验，自定义权限，必须在manifest中声明并调用该权限，否则无法使用服务
            int check = checkCallingOrSelfPermission("wenba.com.androidtest.permission.ACCESS_BOOK_SERVICE");
            if (check == PackageManager.PERMISSION_DENIED) {
                return false;
            }
            //2.包名校验，获取调用者的包名。Linux会给所有进程分配UserId，通过Uid判断其进程是否为我们的服务想要服务的进程
            String[] packagesForUid = getPackageManager().getPackagesForUid(getCallingUid());
            if (packagesForUid != null && packagesForUid.length > 0) {
                if (!packagesForUid[0].startsWith("wenba.com")) {
                    return false;
                }
            } else {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mList.add(new Book(1, "Android"));
        mList.add(new Book(2, "iOS"));
        new Thread(new NewBookArriveTask()).start();
    }

    @Override
    public void onDestroy() {
        isTaskRun.set(false);
        Log.i(TAG, "service onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //服务权限校验，同onTransact，区别在于onBind方法运行在客户端当前线程里
        int check = checkCallingOrSelfPermission("wenba.com.androidtest.permission.ACCESS_BOOK_SERVICE");
        if (check == PackageManager.PERMISSION_DENIED) {
            return null;
        }
        return bookManager;
    }

    private class NewBookArriveTask implements Runnable {

        @Override
        public void run() {
            while (isTaskRun.get()) {
                SystemClock.sleep(5000);
                int bookID = mList.size() + 1;
                //注意RemoteCallbackList的beginBroadcast方法必须配合finishBroadcast一起对称使用，否则会报错Illegal。。。
                int n = mNewBookArriveListenerList.beginBroadcast();
                for (int i = 0; i < n; i++) {
                    INewBookArriveListener broadcastItem = mNewBookArriveListenerList.getBroadcastItem(i);
                    try {
                        broadcastItem.onNewBookArriveListener(new Book(bookID, "new book#" + bookID));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mNewBookArriveListenerList.finishBroadcast();
//                for (INewBookArriveListener listener : mNewBookArriveListenerList) {
//                    try {
//                        listener.onNewBookArriveListener(new BookDBEntity(bookID, "new book#" + bookID));
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        }
    }
}
