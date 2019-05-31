package wenba.com.androidtest.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import static wenba.com.androidtest.Constants.MSG_FROM_CLIENT;
import static wenba.com.androidtest.Constants.MSG_FROM_SERVICE;

/**
 * Created by silvercc on 17/12/11.
 */

public class MessengerService extends Service {

    private static final String TAG = "MessengerService";
    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    private static class MessengerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FROM_CLIENT:
                    Log.i(TAG, "receive msg from client:" + msg.getData().getString("msg"));
                    Messenger client = msg.replyTo;
                    Message replayMessage = Message.obtain(null, MSG_FROM_SERVICE);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply", "I have received your msg, reply after moments");
                    replayMessage.setData(bundle);
                    try {
                        client.send(replayMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
