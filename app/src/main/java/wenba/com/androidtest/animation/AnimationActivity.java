package wenba.com.androidtest.animation;

import android.animation.ArgbEvaluator;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import wenba.com.androidtest.R;

/**
 * Created by silvercc on 18/1/15.
 */

public class AnimationActivity extends Activity {
    private RelativeLayout rl_container;
    private Button animator_button;
    private ObjectAnimator colorAnimator;
//    private ObjectAnimator expandAnimator;
    private ValueAnimator expandAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim);
        rl_container = (RelativeLayout) findViewById(R.id.rl_container);
        animator_button = (Button) findViewById(R.id.animator_button);
        initColorAnimator(rl_container);
    }

    private void initExpandAnimator(final Button animator_button) {
        final int startWidth = animator_button.getWidth();
        final int endWidth = startWidth * 2;
        // 属性动画必须要求操作的属性有get，set方法，同时get，set方法必须是描述同一个属性，并且能对属性产生变化的。
        // 下面的例子就是要操作一个Button的宽度，虽然有setWidth和getWidth的方法，但是两个方法操作的不是一个对象（看源码），
        // 根本不是一个意思，所以直接操作Button的width属性是无效的，因此用一个类包装它，提供对应的set，get方法，间接的改变对象的参数
//        expandAnimator = ObjectAnimator.ofInt(new ViewWrapper(animator_button), "width", startWidth, endWidth);
//        expandAnimator.setDuration(1000);
        // 另一种方式就是使用ValueAnimator，观察一个变化区间，使用Evaluator计算当前关心属性的值，实时替换，补充知识点
        // ，android动画的默认帧率是10ms一帧
        expandAnimator = ValueAnimator.ofInt(1, 100);
        expandAnimator.setDuration(1000);
        expandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            IntEvaluator evaluator = new IntEvaluator();
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                Integer width = evaluator.evaluate(animatedFraction, startWidth, endWidth);
                animator_button.getLayoutParams().width = width;
                animator_button.requestLayout();
            }
        });
        expandAnimator.start();
    }

    private void initColorAnimator(View target) {
        colorAnimator = ObjectAnimator.ofInt(target, "backgroundColor", 0xFFFF8080, 0xFF8080FF);
        colorAnimator.setDuration(3000);
        colorAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        colorAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        colorAnimator.setEvaluator(new ArgbEvaluator());
    }

    public void startAnimator(View view) {
        if (colorAnimator.isRunning()) {
//            curAnimator.end(); 结束动画，将动画执行到endValue得状态
            colorAnimator.cancel();//立刻终止动画，结束在动画当前执行的位置
        } else {
            colorAnimator.start();
        }
    }

    public void startExpand(View view) {
        if (expandAnimator != null && expandAnimator.isRunning()) {
//            curAnimator.end(); 结束动画，将动画执行到endValue得状态
            expandAnimator.cancel();//立刻终止动画，结束在动画当前执行的位置
        } else {
            initExpandAnimator(animator_button);
            expandAnimator.start();
        }
    }

}
