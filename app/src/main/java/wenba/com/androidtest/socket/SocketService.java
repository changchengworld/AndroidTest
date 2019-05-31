package wenba.com.androidtest.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by silvercc on 18/1/11.
 */

public class SocketService extends Service {

    private static final int PORT = 8688;

    private static final String[] DEFINEDWORDS = new String[]{
            "h", "hh", "hhh", "hhhh", "hhhhh"
    };
    private static final String TAG = SocketService.class.getSimpleName();

    private boolean isServerDestroy;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        new Thread(new TCPServer()).start();
        Log.i(TAG, "Service created");
        super.onCreate();
    }

    private class TCPServer implements Runnable {

        @Override
        public void run() {
            Log.i(TAG, "Service run");
            ServerSocket socket = null;
            try {
                socket = new ServerSocket(PORT);
                Log.i(TAG, "hostAddress = " + socket.getLocalSocketAddress().toString());
                Log.i(TAG, "socket: " + socket.toString());
            } catch (IOException e) {
                Log.i(TAG, "ServerSocket create failed port:" + PORT);
                e.printStackTrace();
            }

            while (!isServerDestroy) {
                try {
                    final Socket client = socket.accept();
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                Log.i(TAG, "ServerSocket create failed inner run");
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket client) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())));
        out.println("welcome");
        while (!isServerDestroy) {
            String clientMsg = in.readLine();
            if (TextUtils.isEmpty(clientMsg)) {
                break;
            }
            Log.i(TAG, clientMsg);
            int i = new Random().nextInt(DEFINEDWORDS.length);
            String serverMsg = DEFINEDWORDS[i];
            out.println(serverMsg);
            Log.i(TAG, serverMsg);
        }
        Log.i(TAG, "client quit!");
        in.close();
        out.close();
        client.close();
    }

    @Override
    public void onDestroy() {
        isServerDestroy = true;
        super.onDestroy();
    }
}
