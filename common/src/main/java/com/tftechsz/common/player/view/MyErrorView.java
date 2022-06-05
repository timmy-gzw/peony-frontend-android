package com.tftechsz.common.player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.NetworkUtils;
import com.tftechsz.common.R;
import com.tftechsz.common.player.controller.ControlWrapper;
import com.tftechsz.common.player.interfaces.IControlComponent;
import com.tftechsz.common.utils.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 包 名 : com.tftechsz.common.play.view
 * 描 述 : TODO
 */
public class MyErrorView extends LinearLayout implements IControlComponent {

    private float mDownX;
    private float mDownY;

    private ControlWrapper mControlWrapper;

    public MyErrorView(Context context) {
        this(context, null);
    }

    public MyErrorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_error_view, this, true);
        findViewById(R.id.status_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isConnected()) {
                    setVisibility(GONE);
                    mControlWrapper.replay(false);
                } else {
                    Utils.toast(R.string.net_error);
                }
            }
        });
        setClickable(true);
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == VideoView.STATE_ERROR) {
            bringToFront();
            setVisibility(VISIBLE);
        } else if (playState == VideoView.STATE_IDLE) {
            setVisibility(GONE);
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLock) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                // True if the child does not want the parent to intercept touch events.
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float absDeltaX = Math.abs(ev.getX() - mDownX);
                float absDeltaY = Math.abs(ev.getY() - mDownY);
                if (absDeltaX > ViewConfiguration.get(getContext()).getScaledTouchSlop() ||
                        absDeltaY > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
