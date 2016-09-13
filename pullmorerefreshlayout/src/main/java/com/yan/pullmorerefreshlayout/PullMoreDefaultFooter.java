package com.yan.pullmorerefreshlayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/8/30.
 */
class PullMoreDefaultFooter extends TextView implements PullMoreRefreshState {
    private ValueAnimator animator;

    public PullMoreDefaultFooter(Context context) {
        super(context);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void onCallRelease() {
        setText("release and load");
    }

    @Override
    public void onCallDrag() {
        setText("pull up");
    }

    @Override
    public void onExecute() {
        if (animator == null) {
            animator = ValueAnimator.ofInt(0, 3);
            animator.setDuration(1200);
            animator.setInterpolator(new LinearInterpolator());
            animator.setRepeatMode(ValueAnimator.INFINITE);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setText("loading");
                    for (int i = 0; i < ((int) animation.getAnimatedValue()); i++) {
                        append(".");
                    }
                }
            });
        }
        animator.start();
    }

    @Override
    public void onFinish() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator = null;
        }
        setText("finish ");
    }
}
