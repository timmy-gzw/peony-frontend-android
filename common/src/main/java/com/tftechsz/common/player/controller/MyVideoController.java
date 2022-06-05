package com.tftechsz.common.player.controller;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.tftechsz.common.R;
import com.tftechsz.common.player.view.CompleteView;
import com.tftechsz.common.player.view.GestureView;
import com.tftechsz.common.player.view.MyErrorView;
import com.tftechsz.common.player.view.MyTitleView;
import com.tftechsz.common.player.view.MyVodControlView;
import com.tftechsz.common.player.view.PrepareView;
import com.tftechsz.common.player.view.VideoView;
import com.tftechsz.common.utils.Utils;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import xyz.doikki.videoplayer.util.PlayerUtils;

/**
 * 包 名 : com.tftechsz.common.play.controller
 * 描 述 : TODO
 */
public class MyVideoController extends GestureVideoController implements View.OnClickListener {

    protected ImageView mLockButton;
    protected ProgressBar mLoadingProgress;
    private boolean showLockButton;

    public MyVideoController(@NonNull Context context, boolean showLockButton) {
        this(context, null);
        this.showLockButton = showLockButton;
    }

    public MyVideoController(@NonNull Context context) {
        this(context, false);
    }

    public MyVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dkplayer_layout_standard_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        mLockButton = findViewById(R.id.lock);
        mLockButton.setOnClickListener(this);
        mLoadingProgress = findViewById(R.id.loading);
    }

    /**
     * 快速添加各个组件
     *
     * @param title  标题
     * @param isLive 是否为直播
     */
    public void addDefaultControlComponent(String title, boolean isLive) {
        addDefaultControlComponent(title, isLive, false);
    }

    public void addDefaultControlComponent(String title, boolean isLive, boolean isShowBack) {
        CompleteView completeView = new CompleteView(getContext());
        MyErrorView errorView = new MyErrorView(getContext());
        PrepareView prepareView = new PrepareView(getContext());
        prepareView.setClickStart();
        MyTitleView titleView = new MyTitleView(getContext());
        titleView.setTitle(title);
        addControlComponent(completeView, errorView, prepareView, titleView);
        if (isLive) {
            //addControlComponent(new LiveControlView(getContext()));
        } else {
            MyVodControlView vodControlView = new MyVodControlView(getContext());
            vodControlView.showBottomProgress(false);
            addControlComponent(vodControlView);
        }
        addControlComponent(new GestureView(getContext()));
        setCanChangePosition(!isLive);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.lock) {
            mControlWrapper.toggleLockState();
        }
    }

    @Override
    protected void onLockStateChanged(boolean isLocked) {
        if (isLocked) {
            mLockButton.setSelected(true);
            Utils.toast(R.string.dkplayer_locked);
        } else {
            mLockButton.setSelected(false);
            Utils.toast(R.string.dkplayer_unlocked);
        }
    }

    @Override
    protected void onVisibilityChanged(boolean isVisible, Animation anim) {
        if (mControlWrapper.isFullScreen()) {
            if (isVisible) {
                if (mLockButton.getVisibility() == GONE && showLockButton) {
                    mLockButton.setVisibility(VISIBLE);
                    if (anim != null) {
                        mLockButton.startAnimation(anim);
                    }
                }
            } else {
                mLockButton.setVisibility(GONE);
                if (anim != null) {
                    mLockButton.startAnimation(anim);
                }
            }
        }
    }

    @Override
    protected void onPlayerStateChanged(int playerState) {
        super.onPlayerStateChanged(playerState);
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mLockButton.setVisibility(GONE);
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                if (isShowing() && showLockButton) {
                    mLockButton.setVisibility(VISIBLE);
                } else {
                    mLockButton.setVisibility(GONE);
                }
                break;
        }

        if (mActivity != null && hasCutout()) {
            int orientation = mActivity.getRequestedOrientation();
            int dp24 = PlayerUtils.dp2px(getContext(), 24);
            int cutoutHeight = getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                FrameLayout.LayoutParams lblp = (LayoutParams) mLockButton.getLayoutParams();
                lblp.setMargins(dp24, 0, dp24, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                FrameLayout.LayoutParams layoutParams = (LayoutParams) mLockButton.getLayoutParams();
                layoutParams.setMargins(dp24 + cutoutHeight, 0, dp24 + cutoutHeight, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                FrameLayout.LayoutParams layoutParams = (LayoutParams) mLockButton.getLayoutParams();
                layoutParams.setMargins(dp24, 0, dp24, 0);
            }
        }

    }

    @Override
    protected void onPlayStateChanged(int playState) {
        super.onPlayStateChanged(playState);
        switch (playState) {
            //调用release方法会回到此状态
            case VideoView.STATE_IDLE:
                mLockButton.setSelected(false);
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYING:
            case VideoView.STATE_PAUSED:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
            case VideoView.STATE_BUFFERED:
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_BUFFERING:
                mLoadingProgress.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                mLoadingProgress.setVisibility(GONE);
                mLockButton.setVisibility(GONE);
                mLockButton.setSelected(false);
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isLocked()) {
            show();
            Utils.toast(R.string.dkplayer_lock_tip);
            return true;
        }
        if (mControlWrapper.isFullScreen()) {
            return stopFullScreen();
        }
        return super.onBackPressed();
    }
}
