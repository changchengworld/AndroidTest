// IBookManager.aidl
package wenba.com.androidtest;

import wenba.com.androidtest.Book;
import wenba.com.androidtest.INewBookArriveListener;
// Declare any non-default types here with import statements

interface IBookManager {
            List<Book> getBookList();
            void addBook(in Book book);
            void registerINewBookArriveListener(INewBookArriveListener listener);
            void unregisterINewBookArriveListener(INewBookArriveListener listener);
}
