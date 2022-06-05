package com.tftechsz.common.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;

public class CountDownView extends View {
    private int mRingColor;
    private float mRingWidth;
    //宽度
    private float mWidth;
    //高度
    private float mHeight;
    private Paint mPaint;
    //圆环的矩形区域
    private RectF mRectF;
    private int mCountdownTime;
    private float mCurrentProgress;
    private OnCountDownFinishListener mListener;

    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CountDownView);
        mRingColor = a.getColor(R.styleable.CountDownView_ringColor, context.getResources().getColor(R.color.color_f8d423));
        mRingWidth = a.getFloat(R.styleable.CountDownView_ringWidth, DensityUtils.dp2px(context, 2));
        mWidth = a.getFloat(R.styleable.CountDownView_viewWidth, DensityUtils.dp2px(context, 58));
        mHeight = a.getFloat(R.styleable.CountDownView_viewHeight, DensityUtils.dp2px(context, 58));
        mCountdownTime = a.getInteger(R.styleable.CountDownView_countdownTime, 2);
        a.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        this.setWillNotDraw(false);
    }

    public void setCountdownTime(int mCountdownTime) {
        this.mCountdownTime = mCountdownTime;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mRectF = new RectF(0, 0,
                mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //颜色
        mPaint.setColor(mRingColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRingWidth);
        canvas.drawRoundRect(mRectF, DensityUtils.dp2px(BaseApplication.getInstance(),4),DensityUtils.dp2px(BaseApplication.getInstance(),4), mPaint);
    }

    private ValueAnimator getValA(long countdownTime) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 100);
        valueAnimator.setDuration(countdownTime);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(0);
        return valueAnimator;
    }

    /**
     * 开始倒计时
     */
    public void startCountDown() {
        setClickable(false);
        ValueAnimator valueAnimator = getValA(mCountdownTime * 1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float i = Float.valueOf(String.valueOf(animation.getAnimatedValue()));
                mCurrentProgress = (int) (360 * (i / 100f));
                invalidate();
            }
        });
        valueAnimator.start();
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //倒计时结束回调
                if (mListener != null) {
                    mListener.countDownFinished();
                }
                setClickable(true);
            }

        });
    }

    public void setAddCountDownListener(OnCountDownFinishListener mListener) {
        this.mListener = mListener;
    }

    public interface OnCountDownFinishListener {
        void countDownFinished();
    }
}
