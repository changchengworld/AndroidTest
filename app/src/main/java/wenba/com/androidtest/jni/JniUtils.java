package wenba.com.androidtest.jni;

/**
 * Created by silvercc on 18/2/1.
 */

public class JniUtils {
    static {
        System.loadLibrary("jni-utils");
    }
    public native void set(String str);
    public native String get();
}
