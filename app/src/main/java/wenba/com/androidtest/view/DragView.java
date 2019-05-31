package wenba.com.androidtest.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by silvercc on 18/1/11.
 */

public class DragView extends android.support.v7.widget.AppCompatTextView {
    private float mLastX;
    private float mLastY;

    public DragView(Context context) {
        super(context);
    }

    public DragView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DragView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //注意，全屏滑动，所以拿RawX,RawY
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mLastX;
                float deltaY = y - mLastY;
                float transX = ViewHelper.getTranslationX(this) + deltaX;
                float transY = ViewHelper.getTranslationY(this) + deltaY;
                ViewHelper.setTranslationX(this, transX);
                ViewHelper.setTranslationY(this, transY);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }
}
