package com.yan.pullmorerefreshlayout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * Created by yan on 2016/8/29.
 */
public class PullMoreRefreshLayout extends FrameLayout {
    private FrameLayout headerFrameLayout;
    private FrameLayout footerViewFrameLayout;
    private View childView;
    private View footerView;
    private View headerView;

    private float pullHeight;
    private int headerDefaultHeight = 48;//dp
    private int footerDefaultHeight = 48;//dp

    private int mode = 0;
    private final int MODE_PULL = 1;
    private final int MODE_MORE = 2;

    private float edgeSlop = 8;
    private float firstTouchY = 0;
    private float lastTouchY = -1;
    private float touchDistanceOffsetY = 0;

    private int pointId;

    private OnRefreshListener onRefreshListener;
    private OnLoadMoreListener onLoadMoreListener;

    private ValueAnimator valueAnimatorCurrent;

    public void setOnRefreshListener(OnRefreshListener mOnRefreshListener) {
        this.onRefreshListener = mOnRefreshListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setHeaderView(View mHeaderView) {
        this.headerView = mHeaderView;
    }

    public void setFooterView(View mFooterView) {
        this.footerView = mFooterView;
    }

    public void setPullHeight(float pullHeight) {
        this.pullHeight = pullHeight;
    }

    public void setHeaderDefaultHeight(int headerDefaultHeight) {
        this.headerDefaultHeight = headerDefaultHeight;
    }

    public void setFooterDefaultHeight(int footerDefaultHeight) {
        this.footerDefaultHeight = footerDefaultHeight;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getChildCount() > 1) {
            throw new Error("can hold only one child");
        }
        childView = getChildAt(0);
        addHeaderAndFooter();
    }

    private void addHeaderAndFooter() {
        headerFrameLayout = new FrameLayout(getContext());
        footerViewFrameLayout = new FrameLayout(getContext());
        headerFrameLayout.setLayoutParams(getCenterLayoutParams(getDP(headerDefaultHeight)));
        footerViewFrameLayout.setLayoutParams(getCenterLayoutParams(getDP(footerDefaultHeight)));

        this.addView(headerFrameLayout);
        this.addView(footerViewFrameLayout);

        if (headerView == null) headerView = new PullMoreDefaultHeader(getContext());
        if (footerView == null) footerView = new PullMoreDefaultFooter(getContext());

        headerFrameLayout.addView(headerView);
        footerViewFrameLayout.addView(footerView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        headerFrameLayout.layout(left, top - getDP(headerDefaultHeight), right, 0);
        footerViewFrameLayout.layout(left, bottom, right, bottom + getDP(footerDefaultHeight));
    }

    private boolean isActionDown = true;
    private boolean dragReleaseState = true;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:

                if (isActionDown) {
                    isActionDown = false;
                    firstTouchY = event.getY();
                    pointId = event.getPointerId(0);
                }

                float tempY = event.getY();
                if (Math.abs(tempY - firstTouchY) < edgeSlop) {
                    break;
                } else if (mode == 0) {
                    if (tempY - firstTouchY > 0) {
                        mode = MODE_PULL;
                        footerViewFrameLayout.setVisibility(GONE);
                        headerFrameLayout.setVisibility(VISIBLE);

                        dragReleaseState = -getScrollY() < getDP(headerDefaultHeight);
                    } else {
                        mode = MODE_MORE;
                        headerFrameLayout.setVisibility(GONE);
                        footerViewFrameLayout.setVisibility(VISIBLE);

                        dragReleaseState = getScrollY() < getDP(headerDefaultHeight);
                    }
                }
                if (lastTouchY == -1) lastTouchY = tempY;


                touchDistanceOffsetY = tempY - lastTouchY;

                if (pointId != event.getPointerId(0)) {
                    touchDistanceOffsetY = 0;
                    pointId = event.getPointerId(0);
                }

                if (mode == MODE_PULL) {
                    if (touchDistanceOffsetY > 0) {
                        scrollBy(0, -(int) (touchDistanceOffsetY * getScale(pullHeight / 3)));
                    } else {
                        if (getScrollY() > 0)
                            scrollBy(0, -(int) (touchDistanceOffsetY * getScale(headerDefaultHeight / 2)));
                        else {
                            scrollBy(0, -(int) touchDistanceOffsetY);
                        }
                    }

                    if (-getScrollY() < getDP(headerDefaultHeight) && dragReleaseState) {
                        ((PullMoreRefreshState) headerView).onCallDrag();
                        dragReleaseState = false;
                    } else if (-getScrollY() > getDP(headerDefaultHeight) && !dragReleaseState) {
                        ((PullMoreRefreshState) headerView).onCallRelease();
                        dragReleaseState = true;
                    }
                }
                else if (mode == MODE_MORE) {
                    if (touchDistanceOffsetY < 0) {
                        scrollBy(0, -(int) (touchDistanceOffsetY * getScale(getDP(footerDefaultHeight))));
                    } else {
                        if (getScrollY() < 0)
                            scrollBy(0, -(int) (touchDistanceOffsetY * getScale(headerDefaultHeight / 2)));
                        else scrollBy(0, -(int) touchDistanceOffsetY);
                    }

                    if (getScrollY() < getDP(footerDefaultHeight) && dragReleaseState) {
                        ((PullMoreRefreshState) footerView).onCallDrag();
                        dragReleaseState = false;
                    } else if (getScrollY() > getDP(footerDefaultHeight) && !dragReleaseState) {
                        ((PullMoreRefreshState) footerView).onCallRelease();
                        dragReleaseState = true;
                    }

                }
                lastTouchY = tempY;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if (mode == MODE_PULL && -getScrollY() > getDP(headerDefaultHeight)) {
                    initAnimatorWithListener(getScrollY(),
                            getDP(-headerDefaultHeight),
                            400,
                            commonUpdateListener,
                            new AnimatorAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (onRefreshListener != null) {
                                        onRefreshListener.onRefresh();
                                    }
                                }
                            }).start();

                    ((PullMoreRefreshState) headerView).onExecute();

                }
                else if (mode == MODE_PULL && -getScrollY() <= getDP(headerDefaultHeight)) {
                    initAnimator(getScrollY(), 0, 300, commonUpdateListener).start();
                    mode = 0;
                }

                else if (mode == MODE_MORE && getScrollY() > getDP(footerDefaultHeight)) {
                    initAnimator(getScrollY(), getDP(headerDefaultHeight), 400, commonUpdateListener).start();

                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    ((PullMoreRefreshState) footerView).onExecute();
                } else if (mode == MODE_MORE && getScrollY() <= getDP(footerDefaultHeight)) {
                    initAnimator(getScrollY(), 0, 300, commonUpdateListener).start();
                    mode = 0;
                }

                lastTouchY = -1;
                isActionDown = true;
                break;
        }
        return true;
    }

    private float onInterceptLastY = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mode != 0 || (!ViewCompat.canScrollVertically(childView, -1) && !ViewCompat.canScrollVertically(childView, 1))) {
            return true;
        } else {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    onInterceptLastY = ev.getY();
                    // onTouchEvent(ev);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if ((ev.getY() - onInterceptLastY) > 0 &&
                            !ViewCompat.canScrollVertically(childView, -1) &&
                            ViewCompat.canScrollVertically(childView, 1)) {
                        return true;
                    } else if ((ev.getY() - onInterceptLastY) < 0 &&
                            ViewCompat.canScrollVertically(childView, -1) &&
                            !ViewCompat.canScrollVertically(childView, 1)) {
                        return true;
                    } else if ((ev.getY() - onInterceptLastY) < 0 &&
                            !ViewCompat.canScrollVertically(childView, -1) &&
                            !ViewCompat.canScrollVertically(childView, 1)) {
                        return true;
                    }
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    ValueAnimator.AnimatorUpdateListener commonUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            scrollTo(0, (int) ((float) animation.getAnimatedValue()));
        }
    };

    public void executeComplete() {
        if (mode == MODE_PULL) ((PullMoreRefreshState) headerView).onFinish();
        if (mode == MODE_MORE) ((PullMoreRefreshState) footerView).onFinish();

        initAnimatorWithListener(getScrollY(), 0, 300, commonUpdateListener,
                new AnimatorAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mode = 0;
                    }
                }).start();
    }

    private LayoutParams getCenterLayoutParams(int height) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        layoutParams.gravity = Gravity.CENTER;
        return layoutParams;
    }

    private ValueAnimator initAnimator(float startValue, float endValue, int duration, ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setFloatValues(startValue, endValue);
        valueAnimator.addUpdateListener(animatorUpdateListener);
        if (valueAnimatorCurrent != null && valueAnimatorCurrent.isRunning()) {
            valueAnimatorCurrent.cancel();
            valueAnimatorCurrent = valueAnimator;
        } else valueAnimatorCurrent = valueAnimator;

        return valueAnimator;
    }

    private ValueAnimator initAnimatorWithListener(float startValue,
                                                   float endValue,
                                                   int duration,
                                                   ValueAnimator.AnimatorUpdateListener animatorUpdateListener,
                                                   Animator.AnimatorListener animatorListener) {
        ValueAnimator valueAnimator = initAnimator(startValue, endValue, duration, animatorUpdateListener);
        valueAnimator.addListener(animatorListener);
        return valueAnimator;
    }

    private float getScale(float height) {
        float tempTouchDistanceY = Math.abs(getScrollY());
        if (tempTouchDistanceY > height) {
            return (1 - ((tempTouchDistanceY - height) / height)) < 0 ? 0 :
                    (1 - ((tempTouchDistanceY - height) / height));
        }
        return 1;
    }

    private void init() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        systemService.getDefaultDisplay().getMetrics(displayMetrics);
        pullHeight = displayMetrics.heightPixels / 2;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private int getDP(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getContext().getResources().getDisplayMetrics());

    }

    public PullMoreRefreshLayout(Context context) {
        super(context);
        init();
    }

    public PullMoreRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullMoreRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
}
