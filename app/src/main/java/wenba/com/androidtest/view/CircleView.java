package wenba.com.androidtest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import wenba.com.androidtest.R;

/**
 * Created by silvercc on 18/1/13.
 */

public class CircleView extends View {
    private final int mPaintcolor;
    private Paint mPaint = new Paint();
    private int mDefaultWidth = 200, mDefaultHeight = 200;

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        mPaintcolor = typedArray.getColor(R.styleable.CircleView_circle_color, Color.RED);
        init();
        typedArray.recycle();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    private void init() {
        mPaint.setAntiAlias(true);
        mPaint.setColor(mPaintcolor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure不能省略
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // View的MeasureSpec是父控件的MeasureSpec和View的layoutparams共同决定的，
        // 具体可以看ViewGroup中的getChildMeasureSpec方法，当子控件的宽高是wrap_content时
        // 默认子控件的宽高与父控件的相等，和match_parent效果相同，因此要在这里做特别处理否则给
        // 自定义控件设置wrap_content会失效
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDefaultWidth, mDefaultHeight);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDefaultWidth, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, mDefaultHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //注意，View的父布局会自动处理margin(ViewGroup #measureChildWithMargins)，但是padding是不会处理的，所以padding必须在View中处理，否则padding属性会失效
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int paddingTop = getPaddingTop();
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;
        int min = Math.min(width, height);
        canvas.drawCircle(paddingLeft + width / 2, paddingTop + height / 2, min / 2, mPaint);
        canvas.drawCircle(width / 2, height / 2, min / 2, mPaint);
    }
}
