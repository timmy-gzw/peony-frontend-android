package com.tftechsz.common.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tftechsz.common.R;
import com.tftechsz.common.utils.AnimationUtil;
import com.tftechsz.common.utils.GlideUtils;

/**
 * 连击礼物
 */
public class DoubleHitGiftChatView extends RelativeLayout {
    private TextView mTvHitGiftNumber;
    private ImageView mIvGift;
    private CountDownCircleView countDownCircleView;
    private int mGiftId;
    private final Context mContext;
    private OnCountDownFinishListener mListener;
    private RelativeLayout mRlClick;

    public DoubleHitGiftChatView(Context context) {
        super(context);
        mContext = context;
        initView();
    }


    public DoubleHitGiftChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public DoubleHitGiftChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }


    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_double_hitgift_chat, this);
        mTvHitGiftNumber = findViewById(R.id.tv_hit_gift_number);
        mIvGift = findViewById(R.id.iv_double_hit_gift);
        mRlClick = findViewById(R.id.rl_root_click);
        countDownCircleView = findViewById(R.id.cir_view);
        countDownCircleView.startCountDown();
        countDownCircleView.setAddCountDownListener(() -> {
            mNumber = 0;
            if (mListener != null)
                mListener.countDownFinished();
        });
        mRlClick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.sendAgain();
            }
        });
    }

    public void setRootBg(int bg) {
        mRlClick.setBackgroundResource(bg);
    }

    /**
     * 判断是不是同一个礼物
     */
    public void setGiftImage(String url, int giftId) {
        mGiftId = giftId;
        GlideUtils.loadCircleImage(mContext, mIvGift, url);
    }

    private int mNumber = 0;

    public void resetNumber() {
        mNumber = 0;
    }

    public void startAnimation(int num, int giftId) {
        if (mGiftId != giftId)
            mNumber = 0;
        countDownCircleView.startCountDown();
        //数量增加
        mTvHitGiftNumber.setVisibility(View.VISIBLE);
        mNumber = num + mNumber;
        mTvHitGiftNumber.setText("x" + mNumber);
        ObjectAnimator scaleGiftNum = AnimationUtil.scaleGiftNum(mTvHitGiftNumber);
        scaleGiftNum.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTvHitGiftNumber.setText("x" + num);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
    }

    public void destroy() {
        if (countDownCircleView != null)
            countDownCircleView.destroyTime();
    }

    public void setAddCountDownListener(OnCountDownFinishListener mListener) {
        this.mListener = mListener;
    }

    public interface OnCountDownFinishListener {
        void countDownFinished();

        void sendAgain();
    }


}
