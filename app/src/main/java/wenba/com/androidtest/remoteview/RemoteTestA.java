package wenba.com.androidtest.remoteview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RemoteViews;

import wenba.com.androidtest.Constants;
import wenba.com.androidtest.R;

/**
 * Created by silvercc on 18/1/14.
 */

public class RemoteTestA extends Activity {
    private static final String TAG = RemoteTestA.class.getSimpleName();
    private BroadcastReceiver customBroadCastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive");
            String action = intent.getAction();
            if (action.equals(Constants.REMOTE_ACTION)) {
                RemoteViews remoteViews = intent.getParcelableExtra(Constants.REMOTE_VIEW);
                //接收广播，更新View
                View apply = remoteViews.apply(context, fl_contain);
                fl_contain.addView(apply);
                //如果是在不同的应用中，因为可能没有对应的资源因此需要提前约定好，包括资源文件的名称，ID等，两边APP都要放置，这样才能保证刷新RemoteViews成功
//                int layout_name = getResources().getIdentifier("layout_name", "layout", getPackageName());
//                View view = getLayoutInflater().inflate(layout_name, fl_contain, false);
//                remoteViews.reapply(context, view);
//                fl_contain.addView(view);
            } else {

            }
        }
    };
    private FrameLayout fl_contain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_test_2);
        fl_contain = (FrameLayout)findViewById(R.id.fl_contain);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.REMOTE_ACTION);
        registerReceiver(customBroadCastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(customBroadCastReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
    }
}
