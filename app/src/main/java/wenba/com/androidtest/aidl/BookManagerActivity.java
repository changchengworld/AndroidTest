package wenba.com.androidtest.aidl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import wenba.com.androidtest.Book;
import wenba.com.androidtest.IBookManager;
import wenba.com.androidtest.INewBookArriveListener;

/**
 * Created by silvercc on 18/1/8.
 */

public class BookManagerActivity extends Activity {

    private static final String TAG = BookManagerActivity.class.getSimpleName();
    private static final int NEW_BOOK_ARRIVED = 1;
    private IBookManager mBookManager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NEW_BOOK_ARRIVED:
                    Log.i(TAG, "new book arrived : "+msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder == null){
                return;
            }
            Log.i(TAG, "componentName : "+componentName);
            IBookManager bookManager = IBookManager.Stub.asInterface(iBinder);
            mBookManager = bookManager;
            try {
                iBinder.linkToDeath(new DeathLink(), 0);
                //此处直接调用了服务端的方法，看上去是安全的，实际是有风险的，如果方法本身是耗时的
                //会导致程序ANR。而且在调用服务端方法的时候，客户端当前线程会被挂起。
                List<Book> bookList = bookManager.getBookList();
                Log.i(TAG, "query book list : " + bookList.getClass().getCanonicalName());
                Log.i(TAG, "query book list : " + bookList.toString());
                mBookManager.registerINewBookArriveListener(mINewBookArriveListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "onServiceDisconnected");
            mBookManager = null;
        }
    };

    private class DeathLink implements IBinder.DeathRecipient{

        @Override
        public void binderDied() {
            if (mBookManager == null)
                return;
            mBookManager.asBinder().unlinkToDeath(this, 0);
            mBookManager = null;
            Log.i(TAG, "binderDied");
        }
    }

    private INewBookArriveListener mINewBookArriveListener = new INewBookArriveListener.Stub() {

        @Override
        public void onNewBookArriveListener(Book book) throws RemoteException {
            //此方法被服务端调用，运行在客户端的Binder线程池中，所以操作UI时应该抛到主线程去做
            mHandler.obtainMessage(NEW_BOOK_ARRIVED, book).sendToTarget();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (mBookManager != null && mBookManager.asBinder().isBinderAlive()){
            try {
                mBookManager.unregisterINewBookArriveListener(mINewBookArriveListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}
