package wenba.com.androidtest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by silvercc on 18/1/13.
 * 注意，当前写法是在默认子元素大小一致的情况下实现的，同时没有根据子控件的LayoutParams来处理子控件的margin，也没有处理当前控件的padding
 */

public class HorizonalScrollViewEx extends ViewGroup {
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private float mLastInterceptX;
    private float mLastInterceptY;
    private float mLastX;
    private float mLastY;

    private int mChildWidth;
    private int mCurChildIndex;
    private int mChildCount;

    public HorizonalScrollViewEx(Context context) {
        super(context);
        init();
    }

    public HorizonalScrollViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizonalScrollViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (mScroller == null) {
            mScroller = new Scroller(getContext());
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        float x = ev.getX();
        float y = ev.getY();
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //如果Scroller没有停止，用户再次触碰屏幕，要终止动画，同时拦截事件交给onTouchEvent去执行
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    intercept = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //如果检测发现横向滑动距离的偏差值大于纵向，拦截事件交给onTouchEvent去执行
                float deltaX = x - mLastInterceptX;
                float deltaY = y - mLastInterceptY;
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    intercept = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        mLastInterceptX = x;
        mLastInterceptY = y;
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //终结动画
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //随着手势滑动让控件的内容也跟着滑动
                float deltaX = x - mLastX;
                float deltaY = y - mLastY;
                scrollBy(-(int) deltaX, -(int) deltaY);
                break;
            case MotionEvent.ACTION_UP:
                // 虽然没有对UP事件进行拦截，但是因为拦截了DOWN事件，所以剩下一个系列的动作都会交给当前控件
                // 执行，所以虽然没有在onInterceptTouchEvent方法中对UP事件拦截，但是当拦截了DOWN事件之
                // 后，UP事件也会被消费
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                if (Math.abs(xVelocity) >= 50) {
                    mCurChildIndex = xVelocity > 0 ? mCurChildIndex - 1 : mCurChildIndex + 1;
                } else {
                    mCurChildIndex = (getScrollX() + mChildWidth / 2) / mChildWidth;
                }
                mCurChildIndex = Math.min(0, Math.max(mCurChildIndex, mChildCount - 1));
                int dx = mCurChildIndex * mChildWidth - getScrollX();
                smoothScrollBy(dx, 0);
                mVelocityTracker.clear();
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = 0;
        int measureHeight = 0;
        int childCount = getChildCount();
        mChildCount = childCount;
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (childCount == 0) {
            //如果没有子控件，控件宽高为0
            //此处并不合理，最好能够根据当前控件的LayoutParams来计算宽高
        } else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            View childView = getChildAt(0);
            measureWidth = childView.getMeasuredWidth() * childCount;
            measureHeight = childView.getMeasuredHeight();
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //如果当前控件宽度为wrap，宽度为所有子元素宽度之和，高度使用父控件指定的高度
            View childView = getChildAt(0);
            measureWidth = childView.getMeasuredWidth() * childCount;
            measureHeight = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //如果当前控件高度为wrap，高度为第一个子元素高度，宽度使用父控件指定的宽度
            View childView = getChildAt(0);
            measureWidth = widthSize;
            measureHeight = childView.getMeasuredHeight();
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        //确定子控件位置，每一个往后错一个控件宽度的距离
        int childLeft = 0;
        int childCount = getChildCount();
        for (int k = 0; k < childCount; k++) {
            View childView = getChildAt(k);
            if (childView.getVisibility() != GONE) {
                int measuredWidth = childView.getMeasuredWidth();
                mChildWidth = measuredWidth;
                int measuredHeight = childView.getMeasuredHeight();
                childView.layout(childLeft, 0, childLeft + measuredWidth, measuredHeight);
                childLeft += measuredWidth;
            }
        }
    }

    private void smoothScrollBy(int dx, int dy) {
        // Scroller不会真正做scroll的动作，它只负责保存View的scroll数据，
        // 刷新后会调用computeScroll，在computeScroll里真正执行滑动，实
        // 现滑动的动画效果，非常高明的设计
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // 当View从Activity中销毁的时候，会回调此方法，可以在这里做View的回收工作
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }
}
