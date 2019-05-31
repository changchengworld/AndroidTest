// INewBookArriveListener.aidl
package wenba.com.androidtest;

// Declare any non-default types here with import statements
import wenba.com.androidtest.Book;
interface INewBookArriveListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
            void onNewBookArriveListener(in Book book);
}
