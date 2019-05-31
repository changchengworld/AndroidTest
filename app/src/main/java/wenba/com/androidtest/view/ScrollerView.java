package wenba.com.androidtest.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Scroller;

/**
 * Created by silvercc on 18/1/11.
 * 什么几把玩意，太难用了
 */

public class ScrollerView extends android.support.v7.widget.AppCompatTextView {
    private static final String TAG = ScrollerView.class.getSimpleName();
    //Scroller不会真正的操作View，它只是保存记录滑动的数值，滑动全部依赖View自身的方法。
    private Scroller scroller = new Scroller(getContext());

    public ScrollerView(Context context) {
        super(context);
    }

    public ScrollerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void smoothScrollTo(int desX, int desY) {
        int scrollX = getScrollX();
        int dx = desX - scrollX;
        int scrollY = getScrollY();
        int dy = desY - scrollY;
        Log.i(TAG, "scrollX : " + scrollX + " dx : " + dx + " scrollY : " + scrollY + " dy : " + dy);
        scroller.startScroll(scrollX, scrollY, dx, dy, 2000);
        invalidate();
    }

    @Override
    public void computeScroll() {//刷新会调用这个方法，反复调用，在duration之内完成滑动
        //computeScrollOffset方法返回true代表滑动未结束，false表示滑动已经完成
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        } else {
            Log.i(TAG, "x : " + this.getX() + "y : " + this.getY());
        }
    }
}
