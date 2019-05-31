package wenba.com.androidtest.remoteview;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import wenba.com.androidtest.R;
import wenba.com.androidtest.view.DragViewActivity;

/**
 * Created by silvercc on 18/1/14.
 */

public class RemoteViewActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
    }

    public void showNotification(View view) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("hello world")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setShowWhen(true);
        }
        builder.setContentTitle("title");
        builder.setContentText("text");
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        Intent intent = new Intent(this, DragViewActivity.class);
        // PendingIntent最后一个参数说明，
        // FLAG_UPDATE_CURRENT，更新所有拥有这个PendingIntent的notification中的PendingIntent的参数包括Extra，所有通知都可以打开
        // FLAG_CANCEL_CURRENT，拥有相同PendingIntent的notification中只有第一个通知可以打开
        // FLAG_NO_CREATE，PendingIntent不会主动创建，如果之前没有PendingIntent，getActiviyt（getService，getBroadcastReceiver）的返回值为null，这个标记位用的极少
        // FLAG_ONE_SHOT，拥有相同PendingIntent的notification中有一个点击之后，其余所有notification失效
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //以上PendingIntent的标记位效果生效的前提是notification的ID不一样
        manager.notify(1, notification);
    }
}
