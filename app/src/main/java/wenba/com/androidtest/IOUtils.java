package wenba.com.androidtest;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by silvercc on 18/1/26.
 */

public class IOUtils {
    public static void close(Closeable closeable){
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
