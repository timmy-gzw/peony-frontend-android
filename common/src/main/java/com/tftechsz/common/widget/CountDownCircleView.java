package com.tftechsz.common.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.tftechsz.common.R;

public class CountDownCircleView extends View {
    //圆轮颜色
    private final int mRingColor;
    //圆轮宽度
    private final float mRingWidth;
    private final Paint mPaint;
    //圆环的矩形区域
    private RectF mRectF;
    private int mCountdownTime;
    private float mCurrentProgress;
    private OnCountDownFinishListener mListener;
    private CountDownTimer mCountDownTimer;
    private ValueAnimator valueAnimator;

    public CountDownCircleView(Context context) {
        this(context, null);
    }

    public CountDownCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CountDownView);
        mRingColor = a.getColor(R.styleable.CountDownView_res_ringColor2, context.getResources().getColor(R.color.color_f8d423));
        mRingWidth = a.getFloat(R.styleable.CountDownView_res_ringWidth2, 5);
        mCountdownTime = a.getInteger(R.styleable.CountDownView_res_countdownTime2, 3);
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
        //宽度
        int mWidth = getMeasuredWidth();
        //高度
        int mHeight = getMeasuredHeight();
        mRectF = new RectF(0 + mRingWidth / 2, 0 + mRingWidth / 2,
                mWidth - mRingWidth / 2, mHeight - mRingWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //颜色
        mPaint.setColor(mRingColor);
        //空心
        mPaint.setStyle(Paint.Style.STROKE);
        //宽度
        mPaint.setStrokeWidth(mRingWidth);
        canvas.drawArc(mRectF, -90, mCurrentProgress - 360, false, mPaint);
    }


    public void destroyTime(){
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    /**
     * 开始倒计时
     */
    public void startCountDown() {
        final int[] progress = {0};
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        mCountDownTimer = new CountDownTimer(mCountdownTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //3000/100=3次
                progress[0] += 33.3f;
                mCurrentProgress = (int) (360 * (progress[0] / 100f));
                invalidate();
            }

            @Override
            public void onFinish() {
                if (mListener != null) {
                    mListener.countDownFinished();
                }
            }
        };
        mCountDownTimer.start();

        if (valueAnimator == null)
            valueAnimator = new ValueAnimator();
        valueAnimator = ValueAnimator.ofFloat(0, 100);
        valueAnimator.setDuration(mCountdownTime * 1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(animation -> {
            float i = Float.parseFloat(String.valueOf(animation.getAnimatedValue()));
            mCurrentProgress = (int) (360 * (i / 100f));
            invalidate();
        });
        valueAnimator.pause();
        valueAnimator.start();
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mListener != null) {
                }
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
