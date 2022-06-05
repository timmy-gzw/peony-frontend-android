package com.tftechsz.common.refresh;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.util.SmartUtil;
import com.tftechsz.common.R;
import com.tftechsz.common.utils.Utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.common.refresh
 * 描 述 : TODO
 */
public class MyClassicsHeader extends MyClassicsAbstract<MyClassicsHeader> implements RefreshHeader {

    //    public static String REFRESH_HEADER_UPDATE = "'Last update' M-d HH:mm";
    private final DecimalFormat df = new DecimalFormat("#.##");
    protected String KEY_LAST_UPDATE_TIME = "LAST_UPDATE_TIME";

    protected SharedPreferences mShared;
    protected DateFormat mLastUpdateFormat;
    protected boolean mEnableLastTime = true;

    protected String mTextPulling;//"下拉可以刷新";
    protected String mTextRefreshing;//"正在刷新...";
    protected String mTextLoading;//"正在加载...";
    protected String mTextRelease;//"释放立即刷新";
    protected String mTextFinish;//"刷新完成";
    protected String mTextFailed;//"刷新失败";
    protected String mTextUpdate;//"上次更新 M-d HH:mm";
    protected String mTextSecondary;//"释放进入二楼";
    private final LottieAnimationView mLottieRefresh;
    private final ImageView mImgRefresh;

    //<editor-fold desc="RelativeLayout">
    public MyClassicsHeader(Context context) {
        this(context, null);
    }

    public MyClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        View.inflate(context, R.layout.my_srl_classics_header, this);

        final View arrowView = mArrowView = findViewById(R.id.srl_classics_arrow);
        final View progressView = mProgressView = findViewById(R.id.srl_classics_progress);

        mLottieRefresh = findViewById(R.id.lottie_refresh);
        mImgRefresh = findViewById(R.id.img_refresh);
        mTitleText = findViewById(R.id.srl_classics_title);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsHeader);

        mFinishDuration = ta.getInt(R.styleable.ClassicsHeader_srlFinishDuration, mFinishDuration);
        mEnableLastTime = ta.getBoolean(R.styleable.ClassicsHeader_srlEnableLastTime, mEnableLastTime);
        mSpinnerStyle = SpinnerStyle.values[ta.getInt(R.styleable.ClassicsHeader_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal)];

        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextSizeTitle)) {
            mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.ClassicsHeader_srlTextSizeTitle, SmartUtil.dp2px(16)));
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlPrimaryColor)) {
            super.setPrimaryColor(ta.getColor(R.styleable.ClassicsHeader_srlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlAccentColor)) {
            setAccentColor(ta.getColor(R.styleable.ClassicsHeader_srlAccentColor, 0));
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextPulling)) {
            mTextPulling = ta.getString(R.styleable.ClassicsHeader_srlTextPulling);
        } else {
            mTextPulling = Utils.getString(R.string.srl_header_pulling);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextLoading)) {
            mTextLoading = ta.getString(R.styleable.ClassicsHeader_srlTextLoading);
        }  else {
            mTextLoading = Utils.getString(R.string.srl_header_loading);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextRelease)) {
            mTextRelease = ta.getString(R.styleable.ClassicsHeader_srlTextRelease);
        }  else {
            mTextRelease = Utils.getString(R.string.srl_header_release);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextFinish)) {
            mTextFinish = ta.getString(R.styleable.ClassicsHeader_srlTextFinish);
        }  else {
            mTextFinish = Utils.getString(R.string.srl_header_finish);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextFailed)) {
            mTextFailed = ta.getString(R.styleable.ClassicsHeader_srlTextFailed);
        } else {
            mTextFailed = Utils.getString(R.string.srl_header_failed);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextSecondary)) {
            mTextSecondary = ta.getString(R.styleable.ClassicsHeader_srlTextSecondary);
        } else {
            mTextSecondary = Utils.getString(R.string.srl_header_secondary);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextRefreshing)) {
            mTextRefreshing = ta.getString(R.styleable.ClassicsHeader_srlTextRefreshing);
        } else {
            mTextRefreshing = Utils.getString(R.string.srl_header_refreshing);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextUpdate)) {
            mTextUpdate = ta.getString(R.styleable.ClassicsHeader_srlTextUpdate);
        } else {
            mTextUpdate = Utils.getString(R.string.srl_header_update);
        }
        mLastUpdateFormat = new SimpleDateFormat(mTextUpdate, Locale.getDefault());

        ta.recycle();

        mTitleText.setText(isInEditMode() ? mTextRefreshing : mTextPulling);

        arrowView.setVisibility(GONE);
        progressView.setVisibility(GONE);

       /* try {//try 不能删除-否则会出现兼容性问题
            if (context instanceof FragmentActivity) {
                FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
                @SuppressLint("RestrictedApi")
                List<Fragment> fragments = manager.getFragments();
                if (fragments.size() > 0) {
                    setLastUpdateTime(new Date());
                    return;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }*/

        KEY_LAST_UPDATE_TIME += context.getClass().getName();
        mShared = context.getSharedPreferences("ClassicsHeader", Context.MODE_PRIVATE);

    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    private int mFinishDuration;

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        mFinishDuration = super.onFinish(layout, success);
        if (success) {
            setRefresh(3);
            mTitleText.setText(mTextFinish);
        } else {
            setRefresh(4);
            mTitleText.setText(mTextFailed);
        }
        return mFinishDuration;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        final View arrowView = mArrowView;
        switch (newState) {
            case None:
            case PullDownToRefresh: //下拉可以刷新
                setRefresh(0);
                mTitleText.setText(mTextPulling);
                arrowView.setVisibility(VISIBLE);
                arrowView.animate().rotation(0);
                break;

            case Refreshing:
            case RefreshReleased: //正在刷新...
                setRefresh(2);
                mTitleText.setText(mTextRefreshing);
                arrowView.setVisibility(GONE);
                break;

            case ReleaseToRefresh: //释放立即刷新
                setRefresh(1);
                mTitleText.setText(mTextRelease);
                arrowView.animate().rotation(180);
                break;

            case ReleaseToTwoLevel: //释放进入二楼
                mTitleText.setText(mTextSecondary);
                arrowView.animate().rotation(0);
                break;

            case Loading: //正在加载更多...
                arrowView.setVisibility(GONE);
                mTitleText.setText(mTextLoading);
                break;
        }
    }

    private void setRefresh(int status) {
        switch (status) {
            case 0: //下拉刷新
                mLottieRefresh.setProgress(0);
                mLottieRefresh.setVisibility(INVISIBLE);
                mImgRefresh.setVisibility(VISIBLE);
                mImgRefresh.setImageResource(R.mipmap.refresh_pulling);
                break;

            case 1: //释放刷新
                mLottieRefresh.setProgress(0);
                mLottieRefresh.setVisibility(INVISIBLE);
                mImgRefresh.setVisibility(VISIBLE);
                mImgRefresh.setImageResource(R.mipmap.refresh_release);
                break;

            case 2: //刷新中...
                mLottieRefresh.setVisibility(VISIBLE);
                mImgRefresh.setVisibility(GONE);
                if (!mLottieRefresh.isAnimating()) {
                    mLottieRefresh.setProgress(0);
                    mLottieRefresh.playAnimation();
                }
                break;

            case 3: //刷新成功
                mLottieRefresh.cancelAnimation();
                mLottieRefresh.setVisibility(INVISIBLE);
                mImgRefresh.setVisibility(VISIBLE);
                mImgRefresh.setImageResource(R.mipmap.refresh_finish);
                break;

            case 4: //刷新失败
                mLottieRefresh.cancelAnimation();
                mLottieRefresh.setVisibility(INVISIBLE);
                mImgRefresh.setVisibility(VISIBLE);
                mImgRefresh.setImageResource(R.mipmap.refresh_failed);
                break;
        }
    }


    //</editor-fold>

    //<editor-fold desc="API">
    public MyClassicsHeader setLastUpdateTime(Date time) {
        final View thisView = this;
        if (mShared != null && !thisView.isInEditMode()) {
            mShared.edit().putLong(KEY_LAST_UPDATE_TIME, time.getTime()).apply();
        }
        return this;
    }

    public MyClassicsHeader setAccentColor(@ColorInt int accentColor) {
        return super.setAccentColor(accentColor);
    }


    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (percent <= 1) {
            //Utils.logE(getHeight() + "滑动比率: " + df.format(percent));
        }
        super.onMoving(isDragging, percent, offset, height, maxDragHeight);
    }
}
