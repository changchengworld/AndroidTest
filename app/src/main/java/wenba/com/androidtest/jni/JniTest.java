package wenba.com.androidtest.jni;

/**
 * Created by silvercc on 18/1/31.
 */

public class JniTest {
    static {
        System.loadLibrary("JniTestSample");
    }

    public native void set(String str);
    public native String get();
}
