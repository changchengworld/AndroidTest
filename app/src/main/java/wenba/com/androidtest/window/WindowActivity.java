package wenba.com.androidtest.window;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Button;

import wenba.com.androidtest.R;

/**
 * Created by silvercc on 18/1/16.
 */

public class WindowActivity extends Activity {

    private static final String TAG = WindowActivity.class.getSimpleName();
    private int mTouchSlop;
    private Button mFloatingButton;
    private WindowManager manager;
    private WindowManager.LayoutParams params;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window);
        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        mFloatingButton = new Button(getApplicationContext());
        mFloatingButton.setText("mFloatingButton");
        manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.format = PixelFormat.TRANSPARENT;
        params.gravity = Gravity.START | Gravity.TOP;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.alpha = 1.0f;
        params.x = 0;
        params.y = 0;
        params.width = 300;
        params.height = 100;
        if (Build.VERSION.SDK_INT >= 23) {//23以上需要动态申请权限，在manifest中配置无效
//          <uses-permission android:name="android.permission.SYSTEM_OVERLAY_WINDOW"/>
//          <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivityForResult(intent, 10);
                return;
            } else {
                //执行6.0以上绘制代码
                manager.addView(mFloatingButton, params);
            }
        } else {
            //执行6.0以下绘制代码
            manager.addView(mFloatingButton, params);
        }
        mFloatingButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int rawX = (int) motionEvent.getRawX();
                        int rawY = (int) motionEvent.getRawY();
                        params.x = rawX;
                        params.y = rawY;
                        manager.updateViewLayout(mFloatingButton, params);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "activity ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "activity ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "activity ACTION_UP");
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (Settings.canDrawOverlays(getApplicationContext())) {
                    manager.addView(mFloatingButton, params);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //推出Activity时候从window中移除mFloatingButton，不移除就会一直留在屏幕上，除非杀掉进程
        manager.removeView(mFloatingButton);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }
}
