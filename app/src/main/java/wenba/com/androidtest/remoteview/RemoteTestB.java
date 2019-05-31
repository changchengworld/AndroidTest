package wenba.com.androidtest.remoteview;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import wenba.com.androidtest.Constants;
import wenba.com.androidtest.R;
import wenba.com.androidtest.view.DragViewActivity;

/**
 * Created by silvercc on 18/1/14.
 */

public class RemoteTestB extends Activity {

    private static final String TAG = RemoteTestB.class.getSimpleName();
    private RemoteViews remoteViews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_test_1);
        //构造RemoteViews
        remoteViews = new RemoteViews(getPackageName(), R.layout.layout_remote_test);
        remoteViews.setTextViewText(R.id.tv_clf, "clf");
        remoteViews.setImageViewResource(R.id.iv_icon, R.mipmap.ic_launcher);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, DragViewActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_icon, pendingIntent);
    }

    public void toRemote2(View view) {
        Log.i(TAG, "toRemote2");
        //把RemoteViews以广播的形式交给其它组建
        Intent intent = new Intent(Constants.REMOTE_ACTION);
        intent.putExtra(Constants.REMOTE_VIEW, remoteViews);
        sendBroadcast(intent);
    }

    public void toT2Act(View view) {
        Log.i(TAG, "toT2Act");
        startActivity(new Intent(this, RemoteTestA.class));
    }
}
