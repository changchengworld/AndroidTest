package wenba.com.androidtest.jni;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import wenba.com.androidtest.R;

/**
 * Created by silvercc on 18/2/1.
 */

public class JniActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView jni_test = (TextView) findViewById(R.id.jni_test);
        JniTest test = new JniTest();
        test.set("hahaha");
        JniUtils utils = new JniUtils();
        jni_test.setText(utils.get());
    }
}
