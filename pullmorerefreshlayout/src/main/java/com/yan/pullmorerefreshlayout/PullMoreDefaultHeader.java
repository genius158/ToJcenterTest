package com.yan.pullmorerefreshlayout;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/8/30.
 */
class PullMoreDefaultHeader extends RelativeLayout implements PullMoreRefreshState {
    private ObjectAnimator animator;
    private TextView tvState;
    private ImageView ivState;

    public PullMoreDefaultHeader(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.pull_more_common_refresh_view, this);
        tvState = (TextView) findViewById(R.id.pull_more_text_state);
        ivState = (ImageView) findViewById(R.id.pull_more_img_state);
    }

    @Override
    public void onCallRelease() {
        ivState.setAlpha(1f);
        tvState.setText("release to refresh");
        ivState.setAlpha(1f);
        ivState.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.rotate_arrow));
        ivState.setRotation(180);
    }

    @Override
    public void onCallDrag() {
        tvState.setText("pull down ");
        ivState.setAlpha(1f);
        ivState.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.rotate_arrow));
        ivState.setRotation(0);
    }

    @Override
    public void onExecute() {
        tvState.setText("refreshing");

        ivState.setAlpha(0.5f);
        ivState.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_refresh));

        if (animator == null) {
            animator = ObjectAnimator.ofFloat(ivState, "rotation", 0f, 360f);
            animator.setDuration(800);
            animator.setInterpolator(new LinearInterpolator());
            animator.setRepeatMode(ValueAnimator.INFINITE);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.start();
        }
    }

    @Override
    public void onFinish() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator = null;
        }
        tvState.setText("finish");
    }
}
