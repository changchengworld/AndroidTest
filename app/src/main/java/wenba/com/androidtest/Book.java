package wenba.com.androidtest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by silvercc on 17/12/11.
 */

public class Book implements Parcelable {
    private int bookID;
    private String bookName;

    public Book(int bookID, String bookName) {
        this.bookID = bookID;
        this.bookName = bookName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.bookID);
        dest.writeString(this.bookName);
    }

    protected Book(Parcel in) {
        this.bookID = in.readInt();
        this.bookName = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
