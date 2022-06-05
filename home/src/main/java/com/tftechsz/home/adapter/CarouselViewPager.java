package com.tftechsz.home.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.IntDef;
import androidx.viewpager.widget.ViewPager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CarouselViewPager extends ViewPager {
    @IntDef({RESUME, PAUSE, DESTROY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LifeCycle {
    }

    public static final int RESUME = 0;
    public static final int PAUSE = 1;
    public static final int DESTROY = 2;
    /**
     * 生命周期状态，保证{@link #mCarouselTimer}在各生命周期选择执行策略
     */
    private int mLifeCycle = RESUME;
    /**
     * 是否正在触摸状态，用以防止触摸滑动和自动轮播冲突
     */
    private boolean mIsTouching = false;

    /**
     * 超时时间
     */
    private int timeOut = 2;

    /**
     * 轮播定时器
     */
    private ScheduledExecutorService mCarouselTimer;

    /**
     * 有数据时，才开始进行轮播
     */
    private boolean hasData;

    public CarouselViewPager(Context context) {
        super(context);
    }

    public CarouselViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLifeCycle(@LifeCycle int lifeCycle) {
        this.mLifeCycle = lifeCycle;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mIsTouching = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsTouching = false;
                break;
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startTimer();
    }

    public void startTimer() {
        if (!hasData) {
            return;
        }
        shutdownTimer();
        mCarouselTimer = Executors.newSingleThreadScheduledExecutor();
        mCarouselTimer.scheduleAtFixedRate(() -> {
            switch (mLifeCycle) {
                case RESUME:
                    if (getAdapter() != null
                            && getAdapter().getCount() > 1) {
                        post(() -> setCurrentItem(getCurrentItem() + 1));
                    }
                    break;
                case PAUSE:
                    break;
                case DESTROY:
                    shutdownTimer();
                    break;
            }
        }, 0, 1000 * timeOut, TimeUnit.MILLISECONDS);
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        shutdownTimer();
    }

    private void shutdownTimer() {
        if (mCarouselTimer != null && !mCarouselTimer.isShutdown()) {
            mCarouselTimer.shutdown();
        }
        mCarouselTimer = null;
    }
}
