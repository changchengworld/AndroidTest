package wenba.com.androidtest.animation;

import android.view.View;

/**
 * Created by silvercc on 18/1/15.
 */

public class ViewWrapper {
    private View mTarget;

    public ViewWrapper(View view) {
        mTarget = view;
    }

    public void setWidth(int width) {
        mTarget.getLayoutParams().width = width;
        mTarget.requestLayout();
    }

    public int getWidth() {
        return mTarget.getLayoutParams().width;
    }

}
