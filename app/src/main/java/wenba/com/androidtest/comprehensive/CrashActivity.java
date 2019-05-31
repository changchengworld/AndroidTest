package wenba.com.androidtest.comprehensive;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import wenba.com.androidtest.R;

/**
 * Created by silvercc on 18/1/30.
 */

public class CrashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
    }

    public void crash(View view) {
        throw new RuntimeException("Custom exception!!!");
    }
}
