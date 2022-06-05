package com.tftechsz.im.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.common.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;



public class AudioPrintView extends View {
    private static final String TAG = AudioPrintView.class.getSimpleName();
    /**
     * 声纹条数(默认五条)
     */
    private int mColumnNum = 5;
    private boolean isPlaying = false;
    /**
     * 控件宽度
     */
    private int mWidth;
    /**
     * 控件宽度
     */
    private int mHeight;
    /**
     * 声纹宽度
     */
    private int mPrintWidth;
    /**
     * 单条声纹动画时长
     */
    private int mEffectDuration = 300;

    private Paint mPaint;

    private List<ValueAnimator> mValueAnimators;

    private List<RectF> mRectFS;


    private float[] mScaleValues;

    public AudioPrintView(Context context) {
        this(context, null);
    }

    public AudioPrintView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioPrintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPaintColor(@ColorRes int colorRes) {
        mPaint.setColor(ContextCompat.getColor(getContext(), colorRes));
        postInvalidate();
    }

    public void setEffectDuration(int duration) {
        mEffectDuration = duration;
    }

    public void init(int duration, @ColorRes int colorRes) {
        if (duration <= 10){
            mColumnNum = 5;
        }else if (duration <= 20){
            mColumnNum = 9;
        }else if (duration <= 30){
            mColumnNum = 13;
        }else if (duration <= 40){
            mColumnNum = 17;
        }else if (duration <= 50){
            mColumnNum = 21;
        }else {
            mColumnNum = 25;
        }

        mValueAnimators = new ArrayList<>();
        mRectFS = new ArrayList<>();
        mScaleValues = new float[mColumnNum];



        for (int i = 0; i < mColumnNum; i++){
            ValueAnimator valueAnimator =  ValueAnimator.ofFloat(0f, 1f);
            valueAnimator.setDuration(mEffectDuration);
            final int finalI = i;
            float norValue = 0;
            switch (i % 8){
                case 0:
                    norValue = 0;
                    break;
                case 1:
                    norValue = 0.5f;
                    break;
                case 2:
                    norValue = 1;
                    break;
                case 3:
                    norValue = 0.5f;
                    break;
                case 4:
                    norValue = 0;
                    break;
                case 5:
                    norValue = 0.5f;
                    break;
                case 6:
                    norValue = 1;
                    break;
                case 7:
                    norValue = 0.5f;
                    break;

            }
            mScaleValues[finalI] =  norValue;//初始值
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mScaleValues[finalI] = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            valueAnimator.setRepeatCount(-1);
            valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            mValueAnimators.add(valueAnimator);
            RectF mRectF = new RectF();
            mRectFS.add(mRectF);
        }


        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(colorRes));
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = ScreenUtils.getScreenWidth(getContext());
        }
        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = DensityUtils.dp2px(getContext(),100);
        } else {
            mHeight = heightSpecSize;
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize);
        mPrintWidth = mWidth / (mColumnNum * 2 + 1);
    }

    public void start(boolean isTo) {
        synchronized (this) {
            if(isPlaying){
                return;
            }
            isPlaying = true;

            if (isTo){
                int index = 0;
                for (int i= mValueAnimators.size() - 1; i >= 0; i--){
                    final int finalI = i;
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (this) {
                                if (isPlaying){
                                    mValueAnimators.get(finalI).start();
                                }
                            }
                        }
                    },index * 100);//动画间隔0.1秒
                    index++;
                }
            }else {
                for (int i= 0; i < mValueAnimators.size(); i++){
                    final int finalI = i;
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (this) {
                                if (isPlaying){
                                    mValueAnimators.get(finalI).start();
                                }
                            }
                        }
                    },i * 100);//动画间隔0.1秒
                }
            }
        }
    }

    public void stop() {
        synchronized (this) {
            for (int i= 0; i < mValueAnimators.size(); i++) {
                float norValue = 0;
                switch (i % 8){
                    case 0:
                        norValue = 0;
                        break;
                    case 1:
                        norValue = 0.5f;
                        break;
                    case 2:
                        norValue = 1;
                        break;
                    case 3:
                        norValue = 0.5f;
                        break;
                    case 4:
                        norValue = 0;
                        break;
                    case 5:
                        norValue = 0.5f;
                        break;
                    case 6:
                        norValue = 1;
                        break;
                    case 7:
                        norValue = 0.5f;
                        break;

                }
                mScaleValues[i] =  norValue;//初始值
                mValueAnimators.get(i).cancel();
            }
            postInvalidate();

            isPlaying = false;
        }


    }

    public boolean isStart() {
        return isPlaying;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int radius = mPrintWidth >> 1;//半径作为圆角

        for (int i = 0; i < mColumnNum; i++){
            RectF mRectF = mRectFS.get(i);
            mRectF.set((float) (mPrintWidth * (i*2 + 1)), getShowTop(mScaleValues[i]), (float) (mPrintWidth * (i*2 + 2)), getShowBottom(mScaleValues[i]));//第n条声纹矩形
            canvas.drawRoundRect(mRectF, radius, radius, mPaint);
        }
    }

    protected float getShowTop(float mScaleValue) {
        float height = (float) (mHeight * 0.4 - (mHeight *0.4) * mScaleValue);
        return height<0 ? -height : height;
    }

    protected float getShowBottom(float mScaleValue) {
        float height = (float) (mHeight * 0.6 + (mHeight*0.4) * mScaleValue);
        return height > mHeight ?  mHeight - (height - mHeight): height;
    }

}
