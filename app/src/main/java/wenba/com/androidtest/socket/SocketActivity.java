package wenba.com.androidtest.socket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import wenba.com.androidtest.NetUtils;
import wenba.com.androidtest.R;

/**
 * Created by silvercc on 18/1/11.
 */

public class SocketActivity extends Activity {
    private static final int PORT = 8688;
    private static final String TAG = SocketActivity.class.getSimpleName();
    private static final int MESSAGE_SERVER_CONNECTED = 0x1;
    private static final int MESSAGE_SERVER_NEW_MSG = 0x2;
    private TextView tv_msg_container;
    private EditText et_msg_edit;
    private Button bt_msg_send;
    private Socket mClientSocket;
    private PrintWriter mPrintWriter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SERVER_CONNECTED:
                    Log.i(TAG, "connect server success!");
                    bt_msg_send.setEnabled(true);
                    break;
                case MESSAGE_SERVER_NEW_MSG:
                    tv_msg_container.append((String) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        tv_msg_container = (TextView) findViewById(R.id.tv_msg_container);
        et_msg_edit = (EditText) findViewById(R.id.et_msg_edit);
        bt_msg_send = (Button) findViewById(R.id.bt_msg_send);
        Intent intent = new Intent(SocketActivity.this, SocketService.class);
        startService(intent);
        Log.i(TAG, NetUtils.getHostIP());
        new Thread(new RequestServer()).start();
    }

    public void sendMsg(View view) {
        String msg = et_msg_edit.getText().toString();
        final String finalString = "client " + formatDate(System.currentTimeMillis()) + ": " + msg + "\n";
        if (!TextUtils.isEmpty(finalString)) {
            mPrintWriter.println(finalString);
            et_msg_edit.setText("");
            tv_msg_container.append(finalString);
        }
    }

    private String formatDate(long time) {
        return new SimpleDateFormat("(HH:mm:ss)").format(new Date(time));
    }

    private class RequestServer implements Runnable {

        @Override
        public void run() {
            Socket socket = null;
            while (socket == null) {//重连机制
                try {
                    socket = new Socket("localhost", PORT);
//                    Log.i(TAG, NetUtils.getHostIP());
                    mClientSocket = socket;
                    mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream())));
                    mHandler.sendEmptyMessage(MESSAGE_SERVER_CONNECTED);
                } catch (UnknownHostException e) {
                    Log.i(TAG, "connect server failed UnknownHostException");
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    Log.i(TAG, "connect server failed IOException");
                    e.printStackTrace();
                    return;
                }
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (!SocketActivity.this.isFinishing()) {
                    String serverMsg = reader.readLine();
                    if (!TextUtils.isEmpty(serverMsg)) {
                        final String msg = "server " + formatDate(System.currentTimeMillis()) + ": " + serverMsg + "\n";
                        mHandler.obtainMessage(MESSAGE_SERVER_NEW_MSG, msg);
                    }
                }
                Log.i(TAG, "quit...");
                mPrintWriter.close();
                reader.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onDestroy() {
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();//再接收到的信息都会自动放弃
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
