package com.tftechsz.common.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.tftechsz.common.R;

import java.util.ArrayList;
import java.util.List;

public class AutoVerticalTextView extends TextSwitcher {
    private int mInterval = 2000; //文字停留在中间的时长
    private int mSizeCount;//内容数量大小
    private Handler mHandler = new Handler();
    private int mAnimationIn = R.anim.anim_pull_in;//进入的动画
    private int mAnimationOut = R.anim.anim_pull_out;//出去的动画
    private final Context mContext;
    private TextView mDefaultTextView;//默认的文字
    private final List<String> mTexts = new ArrayList<>();
    int mCurrentIndex = 0;

    public AutoVerticalTextView(Context context) {
        this(context, null);
    }

    public AutoVerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        //设置Factory
        setFactory(() -> {
            mDefaultTextView = new TextView(mContext);
            return mDefaultTextView;

        });
    }

    /**
     * 设置进入动画
     *
     * @return
     */
    public AutoVerticalTextView setAnimationIn(int animationIn) {
        mAnimationIn = animationIn;
        return this;
    }

    /**
     * 设置间隔时间
     */
    public AutoVerticalTextView setInterval(int interval) {
        mInterval = interval;
        return this;
    }


    /**
     * 设置退出动画
     */
    public AutoVerticalTextView setAnimationOut(int animationOut) {
        mAnimationOut = animationOut;
        return this;
    }

    public void init(final List<String> texts, OnAdChangeListener listener, int type) {
        if (texts == null || texts.size() == 0) {
            return;
        }
        mSizeCount = texts.size();
        mTexts.clear();
        mTexts.addAll(texts);
        //返回Listener

        //设置进入动画
        if (mAnimationIn != -1) {
            setInAnimation(AnimationUtils.loadAnimation(mContext, mAnimationIn));
        }
        //设置出去动画
        if (mAnimationOut != -1) {
            setOutAnimation(AnimationUtils.loadAnimation(mContext, mAnimationOut));
        }
        //开始滚动
        //设置文字
        //当前的下表

        setText(mTexts.get(mCurrentIndex));
        listener.DiyTextView((TextView) getCurrentView(), mCurrentIndex);
        if (type == 1)
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //递增
                    mCurrentIndex++;
                    if (mCurrentIndex >= mSizeCount) {
                        mCurrentIndex = 0;
                    }
                    //设置i文字
                    setText(mTexts.get(mCurrentIndex));
//                mChangeListener.DiyTextView((TextView) getCurrentView(), mCurrentIndex);
                    //进行下一次
                    mHandler.postDelayed(this, mInterval);
                }
            }, mInterval);
    }

    public interface OnAdChangeListener {
        void DiyTextView(TextView textView, int index);
    }
}
