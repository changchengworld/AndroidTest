package wenba.com.androidtest.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.nineoldandroids.animation.ValueAnimator;

import wenba.com.androidtest.R;

/**
 * Created by silvercc on 18/1/11.
 */

public class DragViewActivity extends Activity {
    private Button bt_animation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);
        final ScrollerView ScrollerView = (ScrollerView) findViewById(R.id.ScrollerView);
        ScrollerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScrollerView.smoothScrollTo(-500, -500);
            }
        }, 1000);

        bt_animation = (Button) findViewById(R.id.bt_animation);
    }

    public void animationStart(View view) {
        final int x = 0;
        final int deltaX = 100;
        //以下两种实现是一个意思
//        ObjectAnimator.ofFloat(view, "translationX", x, deltaX).setDuration(2000).start();
        ValueAnimator animator = ValueAnimator.ofInt(0, 1).setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                bt_animation.setTranslationX(x + animatedFraction * deltaX);
            }
        });
        animator.start();
    }

}
